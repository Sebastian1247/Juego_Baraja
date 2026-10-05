import java.util.ArrayList;
import java.util.List;

/**
 * Controla una partida de Blackjack: registra a los jugadores y conduce cada ronda
 * (corte, apuestas, reparto, turnos y resultados).
 */
public class Blackjack extends JuegoDeCartas {

    /** Puntuación máxima sin pasarse. */
    public static final int PUNTOS_BLACKJACK = 21;

    private final Croupier croupier;
    private final ConsolaBlackjack consola;
    private final List<Jugador> jugadores = new ArrayList<>();
    private final int numeroJugadores;
    private int numeroRonda;

    public Blackjack(int numeroJugadores, ConsolaBlackjack consola) {
        this.numeroJugadores = numeroJugadores;
        this.consola = consola;
        this.croupier = new Croupier("Croupier", new EstrategiaPlantarseEn17(), consola.pedirApuestaMinima());
    }

    /** Registra a los jugadores y juega rondas mientras quede alguno en la mesa. */
    @Override
    public void iniciarPartida() {
        registrarJugadores();
        while (hayJugadoresActivos()) {
            jugarRonda();
        }
        consola.mostrarFinPartida();
    }

    /** Juega una ronda completa con los jugadores que siguen en la mesa. */
    @Override
    protected void jugarRonda() {
        List<Jugador> activos = jugadoresActivos();
        numeroRonda++;
        croupier.prepararMazo();
        Jugador cortador = activos.get(numeroRonda % activos.size());
        croupier.partirMazo(consola.iniciarRonda(numeroRonda, cortador, croupier.cartasEnMazo() - 1));
        solicitarApuestas(activos);
        croupier.repartirManoInicial(activos);
        consola.mostrarMesa(croupier, activos);
        for (Jugador jugador : activos) {
            turnoJugador(jugador);
        }
        turnoCroupier(activos);
        resolverRonda(activos);
        limpiarRonda();
        retirarJugadores(activos);
    }

    /** Pide nombre y saldo inicial de cada jugador. */
    private void registrarJugadores() {
        for (int i = 0; i < numeroJugadores; i++) {
            String nombre = consola.pedirNombreJugador(i + 1);
            int saldo = consola.pedirSaldoInicial(croupier.getApuestaMinima());
            jugadores.add(new Jugador(nombre, saldo, i));
        }
    }

    /** Pide a cada jugador su apuesta hasta que el croupier la acepte. */
    private void solicitarApuestas(List<Jugador> activos) {
        int minimo = croupier.getApuestaMinima();
        for (Jugador jugador : activos) {
            int apuesta = consola.pedirApuesta(jugador, minimo, false);
            while (!croupier.esApuestaValida(jugador, apuesta)) {
                apuesta = consola.pedirApuesta(jugador, minimo, true);
            }
            croupier.recibirApuesta(jugador, apuesta);
        }
    }

    /** El jugador pide cartas hasta plantarse, llegar a 21 o pasarse. */
    private void turnoJugador(Jugador jugador) {
        if (jugador.tieneBlackjack()) {
            jugador.plantarse();
        }
        while (!jugador.estaPlantado() && !jugador.sePaso() && jugador.getPuntos() < PUNTOS_BLACKJACK) {
            if (consola.preguntarPedirCarta(jugador)) {
                croupier.repartirCarta(jugador);
                consola.mostrarParticipante(jugador);
            } 
            else {
                jugador.plantarse();
            }
        }
        consola.mostrarFinTurno(jugador);
    }

    /** El croupier revela su carta y pide cartas según su estrategia. */
    private void turnoCroupier(List<Jugador> activos) {
        croupier.revelarCarta();
        consola.mostrarMesa(croupier, activos);
        while (croupier.debePedirCarta()) {
            croupier.repartirCarta(croupier);
            consola.mostrarParticipante(croupier);
        }
        consola.mostrarFinTurno(croupier);
    }

    /** Paga, devuelve o cobra la apuesta de cada jugador según su resultado. */
    private void resolverRonda(List<Jugador> activos) {
        List<Jugador> ganadores = croupier.determinarGanadores(activos);
        for (Jugador jugador : activos) {
            int apuesta = jugador.getApuesta();
            boolean gano = ganadores.contains(jugador);
            boolean empato = !gano && croupier.esEmpate(jugador);
            if (gano) {
                croupier.pagarApuesta(jugador);
            } 
            else if (empato) {
                croupier.devolverApuesta(jugador);
            } 
            else {
                croupier.cobrarApuesta(jugador);
            }
            consola.mostrarResultado(jugador, apuesta, gano, empato);
        }
    }

    /** Vacía las manos del croupier y de los jugadores. */
    private void limpiarRonda() {
        croupier.limpiarMano();
        for (Jugador jugador : jugadores) {
            jugador.limpiarMano();
        }
    }

    /** Retira a quien no alcanza la apuesta mínima o decide dejar la mesa. */
    private void retirarJugadores(List<Jugador> activos) {
        for (Jugador jugador : activos) {
            if (!consola.preguntarContinuar(jugador, croupier.puedeSeguirJugando(jugador))) {
                jugador.retirarse();
            }
        }
    }

    /** @return los jugadores que siguen en la mesa */
    private List<Jugador> jugadoresActivos() {
        List<Jugador> activos = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo()) {
                activos.add(jugador);
            }
        }
        return activos;
    }

    /** @return true si queda al menos un jugador en la mesa */
    public boolean hayJugadoresActivos() {
        return !jugadoresActivos().isEmpty();
    }
}