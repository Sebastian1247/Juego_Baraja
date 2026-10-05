/**
 * Base de cualquier juego de cartas. Define el ciclo general de una partida
 * (Template Method): registrar jugadores, jugar rondas mientras haya jugadores
 * y cerrar la partida. Cada juego implementa esos pasos.
 */
public abstract class JuegoDeCartas {

    /** Ciclo común a cualquier juego; las subclases no pueden cambiar su orden. */
    public final void iniciarPartida() {
        registrarJugadores();
        while (hayJugadoresActivos()) {
            jugarRonda();
        }
        finalizarPartida();
    }

    /** Registra a los jugadores de la partida. */
    protected abstract void registrarJugadores();

    /** @return true si queda al menos un jugador en la mesa */
    protected abstract boolean hayJugadoresActivos();

    /** Juega una ronda del juego. */
    protected abstract void jugarRonda();

    /** Cierra la partida al terminar. */
    protected abstract void finalizarPartida();
}
