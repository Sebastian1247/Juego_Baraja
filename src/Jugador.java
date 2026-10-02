public class Jugador extends Participante {
    private int saldo = 0;
    private int apuesta = 0;
    private int posicion = 0;
    private boolean plantado = false;
    private boolean retirado = false;
    
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

    public void perderApuesta() {
        saldo -= apuesta;
        apuesta = 0;
    }

    public void recuperarApuesta(){
        apuesta = 0;
    }

    public boolean puedeApostar() {
        return saldo > 0;
    }
    @Override 
    public void limpiarMano(){
        super.limpiarMano();
        plantado = false;
    }

    public void ganarBlackjack(){
        saldo += apuesta * 3 / 2;
        apuesta = 0;
    }

    public void plantarse() {
        plantado = true;
    }
    
    public boolean estaPlantado() {
        return plantado;
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

    public char acciones(char comando) {
        char jugada = '0';
        if (comando == 'H') { //Hit: Pide otra carta
            jugada = 'H';
        }
        else if (comando == 'S') { //Stand: Ya no pide cartas
            jugada = 'S';
        }
        else if (comando == 'R') { //Retire: Se retira del juego
            jugada = 'R';
        }
        return jugada;
    }

    @Override 
    public String toString(){
        return super.toString() + " | Saldo: " + saldo + " | Apuesta: " + apuesta;
    }
}