import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cartas que tiene un participante y las reglas de puntuación del Blackjack.
 */
public class Mano {

    /** Puntuación máxima sin pasarse; con dos cartas (Figura y As) es Blackjack. */
    public static final int PUNTOS_BLACKJACK = 21;

    private final List<Carta> cartas = new ArrayList<>();

    /** Agrega una carta a la mano. */
    public void agregarCarta(Carta carta) {
        cartas.add(carta);
    }

    /** @return las cartas de la mano, en solo lectura */
    public List<Carta> getCartas() {
        return Collections.unmodifiableList(cartas);
    }

    /**
     * Calcula los puntos de la mano. Las figuras valen 10 y el As vale 11,
     * salvo que eso haga pasarse de 21, en cuyo caso vale 1.
     */
    public int calcularPuntos() {
        int puntos = puntosBase();
        if (esSuave()) {
            puntos += 10;
        }
        return puntos;
    }

    /** Suma los puntos contando cada As como 1 y las figuras como 10. */
    private int puntosBase() {
        int puntos = 0;
        for (Carta carta : cartas) {
            if (carta.esFigura()) {
                puntos += 10;
            } else {
                puntos += carta.rango().getValor();
            }
        }
        return puntos;
    }

    /** @return true si la mano tiene un As que puede contar como 11 sin pasarse de 21 */
    public boolean esSuave() {
        boolean tieneAs = false;
        int i = 0;
        while (!tieneAs && i < cartas.size()) {
            tieneAs = cartas.get(i).esAs();
            i++;
        }
        return tieneAs && puntosBase() + 10 <= PUNTOS_BLACKJACK;
    }

    /** @return cantidad de cartas en la mano */
    public int cantidadCartas() {
        return cartas.size();
    }

    /** @return true si la mano es Blackjack: 21 puntos con exactamente dos cartas */
    public boolean esBlackjack() {
        return cantidadCartas() == 2 && calcularPuntos() == PUNTOS_BLACKJACK;
    }

    /** @return true si la mano pasa de 21 puntos */
    public boolean sePaso() {
        return calcularPuntos() > PUNTOS_BLACKJACK;
    }

    /** Quita todas las cartas de la mano. */
    public void vaciar() {
        cartas.clear();
    }

    /** @return las cartas separadas por " | ", por ejemplo "As de ♥ | Rey de ♠" */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Carta carta : cartas) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(carta);
        }
        return sb.toString();
    }
}
