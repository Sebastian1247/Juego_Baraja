import java.util.ArrayList;
import java.util.List;

public class Mano {

    //private boolean esSuave = false; 
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
        return cartas.size() == 2 && calcularPuntos() == 21;
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
            sb.append(carta.toString()).append(" ");
        }
        return sb.toString().trim();
    }
    /* 
    public int calcularPuntos() {
        int puntos = 0;
        int cantidadAses = 0;
        for(Carta carta : cartas) {
            if(carta.esAs()){
                puntos += 11;
                cantidadAses++;
            }
            else if(carta.esFigura()) {
                puntos += 10;
            }
            else {
                puntos += carta.rango().getValor();
            }
        }
        while(puntos > 21 && cantidadAses > 0) {
            puntos -=10;
            cantidadAses--;
        }
        
        if (cantidadAses > 0) {
            esSuave = true;
        }
        else {
            esSuave = false;
        }
        return puntos;
    }

    public boolean esSuave() {
        return esSuave;
    }
    */
 
}
