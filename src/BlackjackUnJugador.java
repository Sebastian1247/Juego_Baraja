/**
 * Versión 1 del Blackjack: un jugador contra el croupier.
 */
public class BlackjackUnJugador extends Blackjack {

    private Jugador jugador;

    public BlackjackUnJugador(ConsolaBlackjack consola) {
        super(consola);
    }

    /** Pide el nombre y el saldo inicial del jugador. */
    @Override
    protected void registrarJugadores() {
        String nombre = consola.pedirNombreJugador(1, false);
        int saldo = consola.pedirSaldoInicial(croupier.getApuestaMinima());
        jugador = new Jugador(nombre, saldo);
    }

    /** @return true si el jugador sigue en la mesa */
    @Override
    protected boolean hayJugadoresActivos() {
        return jugador.estaActivo();
    }

    /** Juega una ronda completa del jugador contra el croupier. */
    @Override
    protected void jugarRonda() {
        prepararRonda();
        solicitarApuesta(jugador);
        croupier.repartirManoInicial(jugador);
        consola.mostrarMesa(croupier, jugador);
        if (!croupier.tieneBlackjack()) {
            turnoJugador(jugador);
        }
        croupier.revelarCarta();
        consola.mostrarMesa(croupier, jugador);
        turnoCroupier();
        resolverJugador(jugador);
        croupier.limpiarMano();
        jugador.limpiarMano();
        decidirContinuar(jugador);
    }

    /** Con un solo jugador, siempre corta él. */
    @Override
    protected Jugador elegirCortador(int numeroRonda) {
        return jugador;
    }
}