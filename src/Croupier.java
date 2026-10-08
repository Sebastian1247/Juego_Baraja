import java.util.List;

/**
 * Croupier de la mesa: administra la baraja y las apuestas, juega su propia mano
 * siguiendo una estrategia y determina el resultado de cada jugador.
 */
public class Croupier extends Participante {

    private final Mazo mazo;
    private final EstrategiaCroupier estrategia;
    private final int apuestaMinima;
    private boolean cartaOculta;

    /**
     * @param nombre        nombre del croupier
     * @param estrategia    regla que decide cuándo pide carta
     * @param apuestaMinima apuesta mínima permitida en la mesa
     */
    public Croupier(String nombre, EstrategiaCroupier estrategia, int apuestaMinima) {
        super(nombre);
        this.mazo = new Mazo();
        this.estrategia = estrategia;
        this.apuestaMinima = apuestaMinima;
        this.cartaOculta = true;
    }

    /** Junta todas las cartas en el mazo y las baraja. */
    public void prepararMazo() {
        mazo.rellenar();
        mazo.barajar();
    }

    /** Parte el mazo en la posición elegida por un jugador. */
    public void partirMazo(int posicion) {
        mazo.partir(posicion);
    }

    /** @return cantidad de cartas que quedan en el mazo */
    public int cartasEnMazo() {
        return mazo.cartasRestantes();
    }

    /** Da una carta del mazo al participante. Si el mazo se acabó, lo vuelve a preparar. */
    public void repartirCarta(Participante participante) {
        if (mazo.estaVacio()) {
            prepararMazo();
        }
        participante.recibirCarta(mazo.sacarCarta());
    }

    /** Reparte dos cartas al jugador y dos al croupier; la segunda del croupier queda oculta. */
    public void repartirManoInicial(Jugador jugador) {
        cartaOculta = true;
        repartirCarta(jugador);
        repartirCarta(jugador);
        repartirCarta(this);
        repartirCarta(this);
    }

    /** Reparte dos cartas a cada jugador y dos al croupier; la segunda del croupier queda oculta. */
    public void repartirManoInicial(List<Jugador> jugadores) {
        cartaOculta = true;
        for (Jugador jugador : jugadores) {
            repartirCarta(jugador);
            repartirCarta(jugador);
        }
        repartirCarta(this);
        repartirCarta(this);
    }

    /** @return true si su estrategia indica que debe pedir otra carta */
    public boolean debePedirCarta() {
        return estrategia.debePedirCarta(getPuntos());
    }

    /** Destapa la carta oculta para jugar su turno. */
    public void revelarCarta() {
        cartaOculta = false;
    }

    /** @return true si su segunda carta sigue tapada */
    public boolean tieneCartaOculta() {
        return cartaOculta;
    }

    /** @return la primera carta del croupier, que siempre está a la vista */
    public Carta getCartaVisible() {
        Carta cartaVisible = null;
        if (getMano().cantidadCartas() > 0) {
            cartaVisible = getMano().getCartas().get(0);
        }
        return cartaVisible;
    }

    /** @return apuesta mínima de la mesa */
    public int getApuestaMinima() {
        return apuestaMinima;
    }

    /** @return true si la cantidad alcanza la mínima y no supera el saldo del jugador */
    public boolean esApuestaValida(Jugador jugador, int cantidad) {
        return cantidad >= apuestaMinima && cantidad <= jugador.getSaldo();
    }

    /** @return true si el jugador tiene saldo para la apuesta mínima */
    public boolean puedeSeguirJugando(Jugador jugador) {
        return jugador.puedeApostar(apuestaMinima);
    }

    /**
     * Recibe la apuesta de un jugador.
     *
     * @throws IllegalArgumentException si la apuesta no es válida
     */
    public void recibirApuesta(Jugador jugador, int cantidad) {
        if (!esApuestaValida(jugador, cantidad)) {
            throw new IllegalArgumentException("Apuesta inválida para el jugador " + jugador.getNombre());
        }
        jugador.apostar(cantidad);
    }

    /** Paga al jugador: 3 a 2 con Blackjack, 1 a 1 en cualquier otra victoria. */
    public void pagarApuesta(Jugador jugador) {
        if (jugador.tieneBlackjack()) {
            jugador.ganarBlackjack();
        } else {
            jugador.ganarApuesta();
        }
    }

    /** Se queda con la apuesta del jugador que perdió. */
    public void cobrarApuesta(Jugador jugador) {
        jugador.perderApuesta();
    }

    /** Devuelve la apuesta al jugador que empató. */
    public void devolverApuesta(Jugador jugador) {
        jugador.recuperarApuesta();
    }

    /**
     * Determina si el jugador le gana al croupier: con Blackjack (si el croupier no lo tiene)
     * o con más puntos sin pasarse. Si el croupier se pasa, gana si el jugador no se pasó.
     */
    public boolean ganaJugador(Jugador jugador) {
        boolean ganaPorBlackjack = jugador.tieneBlackjack() && !tieneBlackjack();
        boolean ganaPorPuntos = !jugador.sePaso()
                && (sePaso() || jugador.getPuntos() > getPuntos());
        return ganaPorBlackjack || ganaPorPuntos;
    }

    /** @return true si nadie se pasa, tienen los mismos puntos y ambos tienen o no tienen Blackjack */
    public boolean esEmpate(Jugador jugador) {
        return !jugador.sePaso() && !sePaso()
                && jugador.getPuntos() == getPuntos()
                && jugador.tieneBlackjack() == tieneBlackjack();
    }
}
