public enum Palo {
    
    CORAZON("♥"),
    DIAMANTE("♦"),
    TREBOL("♣"),
    PICA("♠");

    private final String simbolo;

    Palo(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getSimbolo() {
        return simbolo;
    }
}
