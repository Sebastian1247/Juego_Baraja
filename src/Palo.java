/**
 * Palos de la baraja inglesa con el símbolo que se muestra en consola.
 */
public enum Palo {

    CORAZON("♥"),
    DIAMANTE("♦"),
    TREBOL("♣"),
    PICA("♠");

    private final String simbolo;

    Palo(String simbolo) {
        this.simbolo = simbolo;
    }

    /** @return símbolo del palo, por ejemplo "♥" */
    public String getSimbolo() {
        return simbolo;
    }
}
