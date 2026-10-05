/**
 * Regla que sigue el croupier para decidir si pide otra carta.
 */
public interface EstrategiaCroupier {

    /**
     * @param puntuacion puntos actuales del croupier
     * @return true si debe pedir otra carta
     */
    boolean debePedirCarta(int puntuacion);
}
