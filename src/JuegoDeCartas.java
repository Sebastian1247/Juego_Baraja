/**
 * Base de cualquier juego de cartas. Cada juego define cómo inicia
 * una partida y cómo se juega una ronda.
 */
public abstract class JuegoDeCartas {

    /** Inicia y conduce la partida completa. */
    public abstract void iniciarPartida();

    /** Juega una ronda del juego. */
    protected abstract void jugarRonda();
}
