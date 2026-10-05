import java.util.List;

/**
 * Vista en consola de una partida de Blackjack: contiene todos los textos del juego,
 * pide los datos por teclado y muestra la mesa, los turnos y los resultados.
 * Es la única clase que usa Keyboard y System.out.
 */
public class ConsolaBlackjack {

    private static final int MAXIMO_JUGADORES = 7;

    public int pedirNumeroJugadores() {
        return leerEnteroEnRango("Número de jugadores (1 a " + MAXIMO_JUGADORES + "): ", 1, MAXIMO_JUGADORES);
    }

    public int pedirApuestaMinima() {
        return leerEnteroEnRango("Apuesta mínima de la mesa: ", 1, Integer.MAX_VALUE);
    }

    public String pedirNombreJugador(int numero) {
        return leerTexto("Nombre del jugador " + numero + ": ");
    }

    public int pedirSaldoInicial(int apuestaMinima) {
        return leerEnteroEnRango("Saldo inicial (mínimo " + apuestaMinima + "): ", apuestaMinima, Integer.MAX_VALUE);
    }

    /** Anuncia la ronda y pide a un jugador la posición donde cortar el mazo. */
    public int iniciarRonda(int numeroRonda, Jugador cortador, int maximo) {
        mostrarMensaje("\n===========RONDA " + numeroRonda + "===========");
        int posicion = leerEnteroEnRango(cortador.getNombre() + ", ¿en qué posición desea cortar el mazo? (1 a " + maximo + "): ", 1, maximo);
        mostrarMensaje("El mazo se corta en la posición " + posicion + ".");
        return posicion;
    }

    /**
     * Pide la apuesta de un jugador.
     *
     * @param repetir true si el intento anterior fue inválido
     */
    public int pedirApuesta(Jugador jugador, int apuestaMinima, boolean repetir) {
        if (repetir) {
            mostrarMensaje("Apuesta inválida.");
        } 
        else {
            mostrarMensaje("Jugador " + jugador.getNombre() + ", su saldo es: " + jugador.getSaldo());
        }
        return leerEntero("Ingrese su apuesta (mínimo " + apuestaMinima + "): ");
    }

    /**
     * Muestra la mesa. Si el croupier ya reveló su carta, lo anuncia y muestra su mano completa.
     */
    public void mostrarMesa(Croupier croupier, List<Jugador> jugadores) {
        if (!croupier.tieneCartaOculta()) {
            mostrarMensaje("El croupier revela su carta oculta.");
        }
        mostrarMensaje("\n===========MESA===========");
        if (croupier.tieneCartaOculta()) {
            mostrarMensaje(croupier.getNombre() + ": " + croupier.getCartaVisible() + " | [carta oculta]");
        } 
        else {
            mostrarMensaje(croupier.toString());
        }
        mostrarMensaje("----------------------------");
        for (Jugador jugador : jugadores) {
            mostrarMensaje(jugador.toString());
        }
        mostrarMensaje("===========================\n");
    }

    /** Muestra la mano del jugador y le pregunta si quiere otra carta. */
    public boolean preguntarPedirCarta(Jugador jugador) {
        mostrarMensaje("Turno de " + jugador.getNombre() + ". Su mano es: " + jugador.getMano()+ " (" + jugador.getPuntos() + " puntos)");
        return preguntarSiNo("¿Desea pedir otra carta? (s/n): ");
    }

    /** Muestra la mano de un participante después de recibir una carta. */
    public void mostrarParticipante(Participante participante) {
        mostrarMensaje(participante.toString());
    }

    /** Avisa si el participante terminó su turno con Blackjack, con 21 o pasándose. */
    public void mostrarFinTurno(Participante participante) {
        if (participante.tieneBlackjack()) {
            mostrarMensaje("¡" + participante.getNombre() + " tiene Blackjack!");
        } 
        else if (participante.sePaso()) {
            mostrarMensaje(participante.getNombre() + " se pasó con " + participante.getPuntos() + " puntos.");
        } 
        else if (participante.tiene21()) {
            mostrarMensaje(participante.getNombre() + " tiene 21 puntos.");
        }
    }

    /** Muestra si el jugador ganó, empató o perdió, y su saldo actual. */
    public void mostrarResultado(Jugador jugador, int apuesta, boolean gano, boolean empato) {
        String resultado;
        if (gano) {
            resultado = "gana su apuesta de " + apuesta;
        } 
        else if (empato) {
            resultado = "empata y recupera su apuesta de " + apuesta;
        } 
        else {
            resultado = "pierde su apuesta de " + apuesta;
        }
        mostrarMensaje("Resultado: " + jugador.getNombre() + " " + resultado + " con " + jugador.getPuntos() + " puntos. Saldo actual: " + jugador.getSaldo());
    }

    /**
     * Si el jugador no tiene fondos lo avisa; si tiene, le pregunta si quiere seguir.
     *
     * @return true si el jugador sigue en la mesa
     */
    public boolean preguntarContinuar(Jugador jugador, boolean tieneFondos) {
        boolean continua = false;
        if (!tieneFondos) {
            mostrarMensaje(jugador.getNombre() + " se retira del juego por falta de fondos.");
        } 
        else {
            continua = preguntarSiNo(jugador.getNombre() + ", ¿desea continuar jugando? (s/n): ");
        }
        return continua;
    }

    public void mostrarFinPartida() {
        mostrarMensaje("No hay más jugadores activos. Fin de la partida.");
    }

    private void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    private int leerEntero(String mensaje) {
        System.out.print(mensaje);
        return Keyboard.readInt();
    }

    private int leerEnteroEnRango(String mensaje, int minimo, int maximo) {
        int valor = leerEntero(mensaje);
        while (valor < minimo || valor > maximo) {
            valor = leerEntero("Valor inválido (" + minimo + " a " + maximo + "). " + mensaje);
        }
        return valor;
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return Keyboard.readString();
    }

    private boolean preguntarSiNo(String pregunta) {
        String respuesta = leerTexto(pregunta);
        while (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n")) {
            respuesta = leerTexto("Respuesta inválida. " + pregunta);
        }
        return respuesta.equalsIgnoreCase("s");
    }
}