import java.util.ArrayList;
import java.util.List;

public class Mano {

    private final List<Carta> cartas = new ArrayList<>();

    public void agregarCarta(Carta carta) {
        cartas.add(carta);
    }

    public List<Carta> getCartas() {
        return cartas;
    }

    public int calcularPuntos() {
        int puntos = puntosBase();
        if(esSuave()){
            puntos += 10;
        }
        return puntos;
    }
    
    private int puntosBase() {
        int puntos = 0;
        for(Carta carta : cartas){
            if(carta.esFigura()){
                puntos += 10;
            }
            else{
                puntos += carta.rango().getValor();
            }
        }
        return puntos;
    }

    public boolean esSuave() {
        boolean tieneAs = false; 
        int i = 0;
        while(!tieneAs && i < cartas.size()){
            tieneAs = cartas.get(i).esAs();
            i++;
        }
        return tieneAs && puntosBase() + 10 <= 21;
    }

    public int cantidadCartas() {
        return cartas.size();
    }

    public boolean esBlackjack(){
        return cantidadCartas() == 2 && calcularPuntos() == 21;
    }

    public boolean sePaso(){
        return calcularPuntos() > 21;
    }

    public void vaciar() {
        cartas.clear();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Carta carta : cartas){
            if(sb.length() > 0){
                sb.append(" | ");
            }
            sb.append(carta);
        }
        return sb.toString();
    }

}
