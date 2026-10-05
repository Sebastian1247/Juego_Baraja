/**
 * Jugador de la mesa: tiene saldo, hace apuestas y decide si pide carta o se planta.
 */
public class Jugador extends Participante {
    private int saldo;
    private int apuesta;
    private boolean plantado;
    private boolean retirado;

    /**
     * @param nombre nombre del jugador
     * @param saldo  dinero inicial
     */
    public Jugador(String nombre, int saldo) {
        super(nombre);
        this.saldo = saldo;
    }

    /** Registra la apuesta de la ronda. El saldo se ajusta al liquidarla. */
    public void apostar(int apuesta) {
        this.apuesta = apuesta;
    }

    /** Gana la apuesta: se le paga 1 a 1. */
    public void ganarApuesta() {
        saldo += apuesta;
        apuesta = 0;
    }

    /** Gana con Blackjack: se le paga 3 a 2. */
    public void ganarBlackjack() {
        saldo += apuesta * 3 / 2;
        apuesta = 0;
    }

    /** Pierde la apuesta: se descuenta de su saldo. */
    public void perderApuesta() {
        saldo -= apuesta;
        apuesta = 0;
    }

    /** Empate: recupera su apuesta y el saldo no cambia. */
    public void recuperarApuesta() {
        apuesta = 0;
    }

    /** El jugador decide no pedir más cartas en esta ronda. */
    public void plantarse() {
        plantado = true;
    }

    /** @return true si el jugador ya no pide cartas en esta ronda */
    public boolean estaPlantado() {
        return plantado;
    }

    /** Vacía la mano y deja al jugador listo para una nueva ronda. */
    @Override
    public void limpiarMano() {
        super.limpiarMano();
        plantado = false;
    }

    /** @return true si su saldo alcanza la apuesta mínima indicada */
    public boolean puedeApostar(int apuestaMinima) {
        return saldo >= apuestaMinima;
    }

    /** El jugador deja la mesa y ya no participa en más rondas. */
    public void retirarse() {
        retirado = true;
    }

    /** @return true si el jugador sigue en la mesa */
    public boolean estaActivo() {
        return !retirado;
    }

    /** @return dinero disponible del jugador */
    public int getSaldo() {
        return saldo;
    }

    /** @return apuesta de la ronda actual */
    public int getApuesta() {
        return apuesta;
    }
}
