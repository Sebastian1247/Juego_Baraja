/**
 * Reglas comunes de una partida de Blackjack: preparación de la ronda, apuestas,
 * turnos y resultados de cada jugador. Las subclases definen cuántos jugadores
 * hay en la mesa y cómo se recorren. La entrada y salida la delega en ConsolaBlackjack.
 */
public abstract class Blackjack extends JuegoDeCartas {

    private final Croupier croupier;
    private final ConsolaBlackjack consola;
    private int numeroRonda;

    protected Blackjack(ConsolaBlackjack consola) {
        this.consola = consola;
        this.croupier = new Croupier("Croupier", new EstrategiaPlantarseEn17(), consola.pedirApuestaMinima());
    }

    /** @return el jugador que corta el mazo en la ronda indicada */
    protected abstract Jugador elegirCortador(int numeroRonda);

    /** Inicia una nueva ronda: prepara el mazo y lo corta en la posición que elige el cortador. */
    protected void prepararRonda() {
        numeroRonda++;
        croupier.prepararMazo();
        Jugador cortador = elegirCortador(numeroRonda);
        croupier.partirMazo(consola.iniciarRonda(numeroRonda, cortador, croupier.cartasEnMazo() - 1));
    }

    /** Pide la apuesta del jugador hasta que el croupier la acepte. */
    protected void solicitarApuesta(Jugador jugador) {
        int minimo = croupier.getApuestaMinima();
        int apuesta = consola.pedirApuesta(jugador, minimo, false);
        while (!croupier.esApuestaValida(jugador, apuesta)) {
            apuesta = consola.pedirApuesta(jugador, minimo, true);
        }
        croupier.recibirApuesta(jugador, apuesta);
    }

    /** El jugador pide cartas hasta plantarse, llegar a 21 o pasarse. */
    protected void turnoJugador(Jugador jugador) {
        if (jugador.tieneBlackjack()) {
            jugador.plantarse();
        }
        while (!jugador.estaPlantado() && !jugador.sePaso() && !jugador.tiene21()) {
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

    /** El croupier pide cartas según su estrategia. */
    protected void turnoCroupier() {
        while (croupier.debePedirCarta()) {
            croupier.repartirCarta(croupier);
            consola.mostrarParticipante(croupier);
        }
        consola.mostrarFinTurno(croupier);
    }

    /** Paga, devuelve o cobra la apuesta del jugador según su resultado. */
    protected void resolverJugador(Jugador jugador) {
        int apuesta = jugador.getApuesta();
        boolean gano = croupier.ganaJugador(jugador);
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

    /** Retira al jugador si no alcanza la apuesta mínima o decide dejar la mesa. */
    protected void decidirContinuar(Jugador jugador) {
        if (!consola.preguntarContinuar(jugador, croupier.puedeSeguirJugando(jugador))) {
            jugador.retirarse();
        }
    }

    /** Anuncia el fin de la partida. */
    @Override
    protected void finalizarPartida() {
        consola.mostrarFinPartida();
    }

    /** @return el croupier de la mesa */
    protected Croupier getCroupier() {
        return croupier;
    }

    /** @return la vista de la partida */
    protected ConsolaBlackjack getConsola() {
        return consola;
    }
}
