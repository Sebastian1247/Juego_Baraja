import java.util.ArrayList;
import java.util.List;

public class Croupier extends Participante {

    private final Mazo mazo;
    private final EstrategiaCroupier estrategia;
    private final int apuestaMinima;
    private boolean cartaOculta;

    public Croupier(String nombre, EstrategiaCroupier estrategia, int apuestaMinima) {
        super(nombre);
        this.mazo = new Mazo();
        this.estrategia = estrategia;
        this.cartaOculta = true;
        this.apuestaMinima = apuestaMinima;
    }

    public void prepararMazo() {
        mazo.rellenar();
        mazo.barajar();
    }

    public void partirMazo(int posicion) {
        mazo.partir(posicion);
    }

    public int cartasEnMazo() {
        return mazo.cartasRestantes();
    }

    public void repartirCarta(Participante participante) {
        if(mazo.estaVacio()){
            prepararMazo();
        }
        Carta carta = mazo.sacarCarta();
        participante.recibirCarta(carta);
    }

    public void repartirManoInicial(List<Jugador> jugadores) {
        cartaOculta = true;
        for (Jugador jugador : jugadores) {
            repartirCarta(jugador);
            repartirCarta(jugador);
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

    public boolean tieneCartaOculta() {
        return cartaOculta;
    }  

    public Carta getCartaVisible() {
        Carta cartaVisible = null;
        if(getMano().cantidadCartas() > 0){
            cartaVisible = getMano().getCartas().get(0);
        }
        return cartaVisible;
    }

    public int getApuestaMinima() {
        return apuestaMinima;
    }

    public boolean esApuestaValida(Jugador jugador, int cantidad) {
        return cantidad >= apuestaMinima && cantidad <= jugador.getSaldo();
    }

    public boolean puedeSeguirJugando(Jugador jugador){
        return jugador.puedeApostar(apuestaMinima);
    }

    public void recibirApuesta(Jugador jugador, int cantidad) {
        if(!esApuestaValida(jugador, cantidad)){
            throw new IllegalArgumentException("Apuesta inválida para el jugador " + jugador.getNombre());
        }
        jugador.apostar(cantidad);
    }
    
    public void pagarApuesta(Jugador jugador) {
        if(jugador.tieneBlackjack()){
            jugador.ganarBlackjack();
        }
        else {
            jugador.ganarApuesta();
        }
    }

    public void cobrarApuesta(Jugador jugador) {
        jugador.perderApuesta();
    }
   
    public void devolverApuesta(Jugador jugador) {
        jugador.recuperarApuesta();
    } 

    public List<Jugador> determinarGanadores(List<Jugador> jugadores){
        List<Jugador> ganadores = new ArrayList<>();
        int puntosCroupier = this.getPuntos();
        for(Jugador jugador : jugadores){
            int puntosJugador = jugador.getPuntos();
            boolean ganaPorPuntos = !jugador.sePaso() 
                    && (puntosJugador > puntosCroupier || this.sePaso());
            boolean ganaPorBlackjack = jugador.tieneBlackjack() && !this.tieneBlackjack();
            if(ganaPorPuntos || ganaPorBlackjack){
                ganadores.add(jugador);
            }
        }
        return ganadores;
    } 

    public boolean esEmpate(Jugador jugador){
        return !jugador.sePaso() && !this.sePaso() 
                && jugador.getPuntos() == this.getPuntos()
                && !jugador.tieneBlackjack() == !this.tieneBlackjack();
    }

}