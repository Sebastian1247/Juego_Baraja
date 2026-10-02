import java.util.ArrayList;
import java.util.List;

public class Croupier extends Participante {

    private Mazo mazo;
    private boolean cartaOculta;
    private EstrategiaCroupier estrategia;

    public Croupier(String nombre, EstrategiaCroupier estrategia) {
        super(nombre);
        this.mazo = new Mazo();
        this.estrategia = estrategia;
        this.cartaOculta = true;
    }
    public void barajar() {
        mazo.barajar();
    }
    public void partirMazo(int posicion) {
        mazo.partir(posicion);
    }
    public void repartirCarta(Participante participante) {
        if(mazo.estaVacio()){
            mazo.rellenar();
            mazo.barajar();
        }
        Carta carta = mazo.sacarCarta();
        participante.recibirCarta(carta);
    }
    public void repartirManoInicial(List<Jugador> jugadores) {
        cartaOculta = true;
        for (Jugador jugador : jugadores) {
            if(jugador.estaActivo()){
                repartirCarta(jugador);
                repartirCarta(jugador);
            }
        }
        repartirCarta(this);
        repartirCarta(this);
    }
    public boolean debePedirCarta() {
        return estrategia.debePedirCarta(this.getPuntos());
    }
    public void revelarCarta() {
        cartaOculta = false;
        
    }
    public Carta getCartaVisible() {
        Carta cartaVisible = null;
        if(getMano().cantidadCartas() > 0){
            cartaVisible = getMano().getCartas().get(0);
        }
        return cartaVisible;
    }
    public int puntuar(Participante participante) {
        return participante.getPuntos();
    }
    public List<Jugador> determinarGanadores(List<Jugador> jugadores){
        List<Jugador> ganadores = new ArrayList<>();
        Mano manoCroupier = this.getMano();
        int puntosCroupier = manoCroupier.calcularPuntos();
        for(Jugador jugador : jugadores){
            if(jugador.estaActivo()){
                Mano manoJugador = jugador.getMano();
                int puntosJugador = manoJugador.calcularPuntos();
                boolean ganaPorPuntos = !manoJugador.sePaso() 
                        && (puntosJugador > puntosCroupier || manoCroupier.sePaso());
                boolean ganaPorBlackjack = manoJugador.esBlackjack() && !manoCroupier.esBlackjack();
                if(ganaPorPuntos || ganaPorBlackjack){
                    ganadores.add(jugador);
                }
            }
        }
        return ganadores;
    } 
    public void recibirApuesta(Jugador jugador, int cantidad) {
        if(cantidad <= 0 || cantidad > jugador.getSaldo()){
            throw new IllegalArgumentException("Apuesta inválida para el jugador " + jugador.getNombre());
        }
        jugador.apostar(cantidad);
    }
    public void cobrarApuesta(Jugador jugador) {
        jugador.perderApuesta();
    }
    public void pagarApuesta(Jugador jugador) {
        if(jugador.getMano().esBlackjack()){
            jugador.ganarBlackjack();
        }
        else {
            jugador.ganarApuesta();
        }
    }
    public void devolverApuesta(Jugador jugador) {
        jugador.recuperarApuesta();
    }

    public boolean tieneCartaOculta() {
        return cartaOculta;
    }   
    public void rellenarMazo(){
        mazo.rellenar();
    }
}