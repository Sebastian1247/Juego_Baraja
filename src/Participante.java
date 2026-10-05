public abstract class Participante {
    private final String nombre;
    private final Mano mano;

    public Participante(String nombre) {
        this.nombre = nombre;
        this.mano = new Mano();
    }
    public void recibirCarta(Carta carta) {
        mano.agregarCarta(carta);
    }
    public void limpiarMano() {
        mano.vaciar();
    }
    public boolean sePaso(){
        return mano.sePaso();
    }
    public boolean tieneBlackjack(){
        return mano.esBlackjack();
    }
    public int getPuntos() {
        return mano.calcularPuntos();
    }
    public String getNombre() {
        return nombre;
    }
    public Mano getMano() {
        return mano;
    }
    @Override 
    public String toString(){
        return nombre + ": " + mano + " (" + getPuntos() + " puntos)";
    }
}