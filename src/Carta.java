/**
 * Carta de la baraja: combinación inmutable de un palo y un rango.
 */
public record Carta(Palo palo, Rango rango) {

    /** @return true si la carta es un As */
    public boolean esAs() {
        return rango == Rango.AS;
    }

    /** @return true si la carta es Jota, Reina o Rey */
    public boolean esFigura() {
        return rango == Rango.JOTA || rango == Rango.REINA || rango == Rango.REY;
    }

    /** @return la carta como texto, por ejemplo "Rey de ♠" */
    @Override
    public String toString() {
        return rango.getNombre() + " de " + palo.getSimbolo();
    }
}
