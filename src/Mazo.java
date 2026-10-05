import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
public class Mazo {
    private final List<Carta> cartas = new ArrayList<>();

    public Mazo(){
        rellenar();
    }

    /**
     * Mezcla las cartas del mazo en un orden aleatorio usando Collections.shuffle
     * (algoritmo Fisher-Yates):
     *
     *   para i desde la última carta hasta la segunda:
     *       j = posición aleatoria entre 0 e i
     *       intercambiar cartas[i] con cartas[j]
     */
    public void barajar() {
        Collections.shuffle(cartas);
    }

    /**
     * Corta el mazo en la posición indicada: las cartas que quedan por encima
     * del corte pasan al fondo, conservando su orden. Usa Collections.rotate,
     * que gira la lista como una rueda:
     *
     *   cada carta en la posición i pasa a (i + distancia) % total
     *   con distancia = total - posicion:
     *       [A, B | C, D, E, F]  ->  [C, D, E, F | A, B]
     *
     * @param posicion cantidad de cartas a pasar al fondo (entre 1 y el total - 1)
     * @throws IllegalArgumentException si la posición no divide el mazo en dos montones
     */
    public void partir(int posicion) {
        if(posicion <= 0 || posicion >= cartas.size()){
            throw new IllegalArgumentException("Posicion invalida para partir el mazo");
        }
       Collections.rotate(cartas, cartas.size() - posicion);
    }

    /**
     * Devuelve la cantidad de cartas que quedan en el mazo.
     *
     * @return cantidad de cartas restantes
     */
    public int cartasRestantes() {
        return cartas.size();
    }

    /**
     * Saca la última carta del mazo.
     *
     * @return la carta sacada
     * @throws IllegalStateException si el mazo está vacío
     */
    public Carta sacarCarta() {
        if(cartas.isEmpty()){
            throw new IllegalStateException("No hay cartas en el mazo");
        }
        return cartas.remove(cartas.size() - 1);
    }

    /**
     * Comprueba si el mazo está vacío.
     *
     * @return true si el mazo está vacío, false en caso contrario
     */
    public boolean estaVacio() {
        return cartas.isEmpty();
    }

    /**
     * Rellena el mazo con todas las cartas posibles.
     */
    public void rellenar() {
        cartas.clear();
        for(Palo palo : Palo.values()){
            for(Rango rango : Rango.values()){
                cartas.add(new Carta(palo, rango));
            }
        }
    }

}
