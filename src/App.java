import java.io.PrintStream;

/**
 * Punto de entrada del programa: configura la consola y arranca una partida de Blackjack.
 */
public class App {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        ConsolaBlackjack consola = new ConsolaBlackjack();
        JuegoDeCartas juego = new Blackjack(consola.pedirNumeroJugadores(), consola);
        juego.iniciarPartida();
    }
}