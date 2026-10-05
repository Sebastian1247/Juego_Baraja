import java.io.PrintStream;

public class App {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        ConsolaBlackjack consola = new ConsolaBlackjack();
        JuegoDeCartas juego = new Blackjack(consola.pedirNumeroJugadores(), consola);
        juego.iniciarPartida();
    }
}