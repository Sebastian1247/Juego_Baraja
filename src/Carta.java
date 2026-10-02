public record Carta(Palo palo, Rango rango) {
    public boolean esAs() {
        return rango == Rango.AS;
    }

    public boolean esFigura() {
        return rango == Rango.JOTA || rango == Rango.REINA || rango == Rango.REY;
    }

    @Override 
    public String toString() {
        return rango.getNombre() + " de " + palo.getSimbolo();
    }
}
