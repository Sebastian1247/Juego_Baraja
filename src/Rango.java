/**
 * Rangos de la baraja inglesa. El valor es la posición natural de la carta
 * (As = 1 ... Rey = 13); los puntos de cada juego los calcula ese juego.
 */
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

    /** @return nombre de la carta, por ejemplo "Reina" */
    public String getNombre(){
        return nombre;
    }

    /** @return posición de la carta, de 1 a 13 */
    public int getValor() {
        return valor;
    }

    
}