/**
 * Estrategia estándar de casino: el croupier pide carta con 16 o menos
 * y se planta con 17 o más.
 */
public class EstrategiaPlantarseEn17 implements EstrategiaCroupier {
    private static final int LIMITE_PARA_PLANTARSE = 17;

    @Override
    public boolean debePedirCarta(int puntuacion) {
        return puntuacion < LIMITE_PARA_PLANTARSE;
    }
}
