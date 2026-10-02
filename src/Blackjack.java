import java.util.List;

public class Blackjack extends JuegoDeCartas {
    private Croupier croupier;
    private List<Jugador> jugadores;
    private final int PUNTOS_BLACKJACK = 21;
    private int APUESTA_MINIMA;
    private int numeroJugadores;

    public Blackjack(int numeroJugadores) {
        this.numeroJugadores = numeroJugadores;
        APUESTA_MINIMA = Keyboard.readInt();
    }
    

    private void registrarJugadores() {
        // Favor de revisar
        String nombre;
        int saldo = 0;
        System.out.println("Registre a los jugadores");
        for(int i = 0; i < numeroJugadores; i++) {
            System.out.print("Jugador "+(i+1)+": ");
            nombre = Keyboard.readString();
            System.out.print("Saldo inicial: ");
            saldo = Keyboard.readInt();
            jugadores.add(new Jugador(nombre, saldo, i));
        }
    }
    public void iniciarPartida() {
        croupier = new Croupier("croupier", new EstrategiaPlantarseEn17());
        registrarJugadores();
        croupier.rellenarMazo();
        croupier.barajar();
        repartirInicial();

        
    }
    protected void jugarRonda() {

    }
    private void solicitarApuestas() {
        
    }
    private void repartirInicial() {
        croupier.repartirManoInicial(jugadores);
    }
    private void turnoJugador(Jugador jugador) {

    }
    private boolean preguntarPedirCarta(Jugador jugador) {
        return false;
    }
    private void turnoCroupier() {
    }
    private void resolverRonda() {

    }
    private void mostrarMesa( ){
        System.out.println("\n===========MESA===========");
        if(croupier.tieneCartaOculta()){
            System.out.println(croupier.getNombre());
        }
        else {
            System.out.println(croupier);
        }
        System.out.println("----------------------------");
        for(Jugador jugador: jugadores){
            if(jugador.estaActivo()){
                System.out.println(jugador);
            }
        }
        System.out.println("===========================\n");
    }
    private void limpiarRonda() {
        croupier.limpiarMano();
        for(Jugador jugador: jugadores) {
            jugador.limpiarMano();
        }
        croupier.rellenarMazo();
        croupier.barajar();
    }
    private void retirarJugadoresSinFondos() {
        for(Jugador jugador: jugadores) {
            if(!jugador.puedeApostar())
                jugador.retirarse();
        }
    }
    public boolean hayJugadoresActivos() {
        boolean comprobacion = false;
        for(Jugador jugador: jugadores) {
            if(jugador.estaActivo())
            comprobacion = true;
        }
        return comprobacion;
    }
    public boolean esBlackjack(Mano mano) {
        return mano.calcularPuntos() == PUNTOS_BLACKJACK;
    }
    public boolean sePaso(Mano mano) {
        return mano.calcularPuntos() > PUNTOS_BLACKJACK;
    }
}