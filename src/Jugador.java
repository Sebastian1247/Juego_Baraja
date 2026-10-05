public class Jugador extends Participante {
    private int saldo;
    private int apuesta;
    private final int posicion;
    private boolean plantado;
    private boolean retirado;
    
    public Jugador(String nombre, int saldo, int posicion) {
        super(nombre);
        this.saldo = saldo;
        this.posicion = posicion;
    }

    public void apostar(int apuesta) {
        this.apuesta = apuesta;
    }

    public void ganarApuesta() {
        saldo += apuesta;
        apuesta = 0;
    }

    public void ganarBlackjack(){
        saldo += apuesta * 3 / 2;
        apuesta = 0;
    }

    public void perderApuesta() {
        saldo -= apuesta;
        apuesta = 0;
    }

    public void recuperarApuesta(){
        apuesta = 0;
    }

    public void plantarse() {
        plantado = true;
    }

    public boolean estaPlantado() {
        return plantado;
    }

    @Override 
    public void limpiarMano(){
        super.limpiarMano();
        plantado = false;
    }

    public boolean puedeApostar(int apuestaMinima) {
        return saldo >= apuestaMinima;
    }

    public void retirarse() {
        retirado = true;
    }

    public boolean estaActivo() {
        return !retirado;
    }

    public int getSaldo() {
        return this.saldo;
    }

    public int getApuesta() {
        return this.apuesta;
    }

    public int getPosicion() {
        return this.posicion;
    }

    @Override 
    public String toString(){
        return super.toString() + " | Saldo: " + saldo + " | Apuesta: " + apuesta;
    }
}