public enum Palo {
    
    CORAZON("Corazón"),
    DIAMANTE("Diamante"),
    TREBOL("Trébol"),
    PICA("Pica");

    private final String nombre;

    Palo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
