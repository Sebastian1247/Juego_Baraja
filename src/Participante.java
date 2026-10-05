/**
 * Persona que juega en la mesa y tiene una mano de cartas.
 * Es la base común de Jugador y Croupier.
 */
public abstract class Participante {
    private final String nombre;
    private final Mano mano;

    /** @param nombre nombre del participante */
    public Participante(String nombre) {
        this.nombre = nombre;
        this.mano = new Mano();
    }

    /** Agrega una carta a la mano del participante. */
    public void recibirCarta(Carta carta) {
        mano.agregarCarta(carta);
    }

    /** Vacía la mano para una nueva ronda. */
    public void limpiarMano() {
        mano.vaciar();
    }

    /** @return true si la mano pasa de 21 puntos */
    public boolean sePaso() {
        return mano.sePaso();
    }

    /** @return true si la mano es Blackjack */
    public boolean tieneBlackjack() {
        return mano.esBlackjack();
    }

    /** @return true si la mano suma exactamente 21 puntos */
    public boolean tiene21() {
        return getPuntos() == Mano.PUNTOS_BLACKJACK;
    }

    /** @return puntos actuales de la mano */
    public int getPuntos() {
        return mano.calcularPuntos();
    }

    /** @return nombre del participante */
    public String getNombre() {
        return nombre;
    }

    /** @return la mano del participante */
    public Mano getMano() {
        return mano;
    }

    /** @return nombre, cartas y puntos, por ejemplo "Ana: As de ♥ | Rey de ♠ (21 puntos)" */
    @Override
    public String toString() {
        return nombre + ": " + mano + " (" + getPuntos() + " puntos)";
    }
}
