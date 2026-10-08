import java.util.ArrayList;
import java.util.List;

/**
 * Versión 2 del Blackjack: varios jugadores contra el croupier en la misma mesa.
 */
public class BlackjackMultijugador extends Blackjack {

    private final List<Jugador> jugadores = new ArrayList<>();
    private final int numeroJugadores;

    public BlackjackMultijugador(int numeroJugadores, ConsolaBlackjack consola) {
        super(consola);
        this.numeroJugadores = numeroJugadores;
    }

    /** Pide nombre y saldo inicial de cada jugador. */
    @Override
    protected void registrarJugadores() {
        for (int i = 0; i < numeroJugadores; i++) {
            String nombre = consola.pedirNombreJugador(i + 1, false);
            while (existeJugador(nombre)) {
                nombre = consola.pedirNombreJugador(i + 1, true);
            }
            int saldo = consola.pedirSaldoInicial(croupier.getApuestaMinima());
            jugadores.add(new Jugador(nombre, saldo));
        }
    }

    /** @return true si queda al menos un jugador en la mesa */
    @Override
    protected boolean hayJugadoresActivos() {
        return !jugadoresActivos().isEmpty();
    }

    /** Juega una ronda completa con los jugadores que siguen en la mesa. */
    @Override
    protected void jugarRonda() {
        List<Jugador> activos = jugadoresActivos();
        prepararRonda();
        for (Jugador jugador : activos) {
            solicitarApuesta(jugador);
        }
        croupier.repartirManoInicial(activos);
        consola.mostrarMesa(croupier, activos);
        if (!croupier.tieneBlackjack()) {
            for (Jugador jugador : activos) {
                turnoJugador(jugador);
            }
        }
        croupier.revelarCarta();
        consola.mostrarMesa(croupier, activos);
        turnoCroupier();
        for (Jugador jugador : activos) {
            resolverJugador(jugador);
        }
        limpiarRonda();
        for (Jugador jugador : activos) {
            decidirContinuar(jugador);
        }
    }

    /** El turno de cortar rota entre los jugadores activos. */
    @Override
    protected Jugador elegirCortador(int numeroRonda) {
        List<Jugador> activos = jugadoresActivos();
        return activos.get(numeroRonda % activos.size());
    }

    /** Vacía las manos del croupier y de los jugadores. */
    private void limpiarRonda() {
        croupier.limpiarMano();
        for (Jugador jugador : jugadores) {
            jugador.limpiarMano();
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

    /** @return true si ya hay un jugador con ese nombre (sin distinguir mayúsculas) */
    private boolean existeJugador(String nombre) {
        boolean existe = false;
        for (Jugador jugador : jugadores) {
            if (jugador.getNombre().equalsIgnoreCase(nombre.trim())) {
                existe = true;
            }
        }
        return existe;
    }
}