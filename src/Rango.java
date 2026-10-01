public enum Rango {
    AS(1, "As"),
    DOS(2, "Dos"),
    TRES(3, "Tres"),
    CUATRO(4, "Cuatro"),
    CINCO(5, "Cinco"),
    SEIS(6, "Seis"),
    SIETE(7, "Siete"),
    OCHO(8, "Ocho"),
    NUEVE(9, "Nueve"),
    DIEZ(10, "Diez"),
    JOTA(11, "Jota"),
    REINA(12, "Reina"),
    REY(13, "Rey");
    private final String nombre;
    private final int valor;

    Rango(int valor, String nombre) {
        this.valor = valor;
        this.nombre = nombre;
    }

    public String getNombre(){
        return nombre;
    }

    public int getValor() {
        return valor;
    }

    
}