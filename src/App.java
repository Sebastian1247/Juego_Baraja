import java.io.PrintStream;

/**
 * Punto de entrada del programa: configura la consola y arranca la versión
 * de Blackjack que corresponde al número de jugadores.
 */
public class App {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        ConsolaBlackjack consola = new ConsolaBlackjack();
        int numeroJugadores = consola.pedirNumeroJugadores();
        JuegoDeCartas juego;
        if (numeroJugadores == 1) {
            juego = new BlackjackUnJugador(consola);
        }
        else {
            juego = new BlackjackMultijugador(numeroJugadores, consola);
        }
        juego.iniciarPartida();
    }
}