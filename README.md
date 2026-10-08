# Juego de Baraja: Blackjack

Implementación en Java de Blackjack para consola, desarrollada para la materia de Tecnologías de Programación. Incluye las dos versiones solicitadas: la versión 1, con un jugador contra el croupier, y la versión 2, con 2 a 6 jugadores en la misma mesa. En ambas, el croupier baraja, reparte, administra las apuestas y juega su propia mano bajo una estrategia fija.

El enunciado anticipa que más adelante se agregarán otros juegos de baraja. Esa condición guió buena parte del diseño: se buscó que la baraja, los participantes y el ciclo de la partida dependieran lo menos posible de las reglas específicas del Blackjack.

## Ejecución

Se requiere Java 16 o superior, ya que el proyecto utiliza `record`. El proyecto no depende de ningún IDE ni de bibliotecas externas; para compilarlo y ejecutarlo desde una terminal, en la raíz del proyecto:

```
javac -encoding UTF-8 -d bin src/*.java
java -cp bin App
```

También puede abrirse en cualquier IDE con soporte para Java (VS Code, IntelliJ IDEA, Eclipse, NetBeans), marcando `src` como carpeta de código fuente y ejecutando la clase `App`.

En Windows la consola usa por defecto una página de códigos que no incluye los símbolos ♥ ♦ ♣ ♠, por lo que los palos pueden mostrarse como caracteres extraños. Se corrige ejecutando `chcp 65001` antes del programa. Para quien use VS Code, `.vscode/settings.json` incluye un perfil de terminal que ya inicia en UTF-8. En Linux y macOS las terminales usan UTF-8 por defecto.

## Flujo de una partida

Al iniciar se captura el número de jugadores, que determina la versión: con un jugador se ejecuta la versión 1 y con dos o más, la versión 2. Después se capturan la apuesta mínima de la mesa y el nombre y saldo de cada jugador; en la versión 2 los nombres no pueden repetirse.

Cada ronda sigue la secuencia de una mesa real. El croupier reúne y baraja las 52 cartas, y un jugador elige la posición del corte; en la versión 2 ese turno rota entre los jugadores. Después se reciben las apuestas, se reparten dos cartas a cada participante (una de las del croupier queda boca abajo), cada jugador decide si pide carta o se planta, juega el croupier y se liquidan las apuestas.

Al cierre de la ronda, el jugador que ya no cubre la apuesta mínima abandona la mesa y al resto se le pregunta si desea continuar. La partida termina cuando no queda ningún jugador.

## Reglas

El juego implementa las reglas estándar de casino:

- Cada jugador compite únicamente contra el croupier, por lo que una misma ronda puede tener varios ganadores.
- Las figuras valen 10 y el As vale 11 o 1, según convenga a la mano.
- Un Blackjack (As más una carta de valor 10 en las dos primeras cartas) paga 3 a 2; una victoria por puntos paga 1 a 1 y un empate devuelve la apuesta.
- Quien supera 21 pierde, aun si el croupier también se pasa después.
- Si el croupier obtiene Blackjack en el reparto, no se juegan los turnos: empatan los jugadores que también tienen Blackjack y los demás pierden.
- El croupier pide carta con 16 o menos y se planta a partir de 17.

Se eligieron las reglas de casino frente a variantes con un único ganador por ronda porque son las reglas de referencia del juego y son consistentes con el requisito de que alcanzar 21 es una condición de victoria.

Las jugadas opcionales (doblar, separar, seguro y rendición) quedaron fuera del alcance, ya que el enunciado indica iniciar con apuestas básicas.

## Decisiones de diseño

### Representación de la baraja

`Rango` y `Palo` son enumeraciones porque representan un dominio cerrado: 13 rangos y 4 palos que no cambian. Un `int` o un `String` admitiría valores inválidos que el compilador no puede detectar. Cada constante almacena sus propios datos (nombre y posición del rango, símbolo del palo), y `values()` permite generar la baraja completa con dos ciclos anidados.

Una decisión menos obvia fue que `Rango` guardara la posición natural de la carta (As = 1, Rey = 13) y no su valor en Blackjack. El valor de una carta es una regla del juego, no una propiedad de la carta; si se hubiera colocado en el enum, cualquier juego futuro heredaría una puntuación que no le corresponde.

`Carta` es un `record`. Se trata de un objeto de valor, identificado por su palo y su rango, que no debe modificarse después de creado. El `record` garantiza esa inmutabilidad y genera el constructor, los accesores, `equals()` y `hashCode()`; solo se implementan `esAs()`, `esFigura()` y `toString()`.

### Estructura del mazo

Las operaciones que el mazo necesita son barajar, partir y extraer la carta superior. La estructura se eligió comparando las alternativas en función de esas operaciones:

| Estructura | Barajar | Partir | Extraer carta |
|---|---|---|---|
| `ArrayList` | `Collections.shuffle`, O(n) | `Collections.rotate`, O(n), sin memoria adicional | O(1) desde el final |
| `ArrayDeque` | Requiere copiar a una lista | Ciclo de `pollFirst`/`addLast` | O(1) |
| Arreglo con índice | Fisher-Yates manual | Manual | O(1), tamaño fijo |

`ArrayDeque` resultaba atractivo porque opera en ambos extremos en tiempo constante, pero el Blackjack nunca inserta cartas por el fondo del mazo y la estructura no permite barajar directamente. El `ArrayList` resuelve las tres operaciones con la biblioteca estándar. Al ser un atributo privado, un cambio de estructura en el futuro quedaría contenido dentro de `Mazo`.

### Cálculo de puntos

La puntuación se calcula en `Mano`, que es la clase que posee la información necesaria (Information Expert). Ahí se determina si la mano es suave, si es Blackjack o si se pasó de 21, y se define la constante `PUNTOS_BLACKJACK` para que el valor 21 exista en un solo lugar.

Los puntos y la condición de mano suave se calculan bajo demanda en lugar de almacenarse en atributos, para que nunca queden desactualizados respecto a las cartas. El cálculo del As se hace en dos pasos: se suman todos los ases como 1 y, si un As puede valer 11 sin exceder 21, se agregan 10. Basta con evaluar un solo As, porque dos ases con valor 11 ya suman 22.

### Jerarquía de participantes

`Jugador` y `Croupier` comparten estado (nombre y mano) y comportamiento (recibir cartas, limpiar la mano, consultar puntos). Esa parte común está en `Participante`, una clase abstracta: en la mesa no existe un participante genérico, y una interfaz no habría permitido declarar los atributos compartidos. Cada participante crea su propia mano en el constructor, ya que la relación es de composición y no tiene sentido que dos participantes compartan una.

Cada subclase agrega solo su especialización. `Jugador` administra saldo, apuesta y estado en la ronda; el saldo se ajusta al liquidar la ronda y no al momento de apostar. `Croupier` concentra lo que el enunciado le asigna: posee la baraja, reparte, administra las apuestas, juega y determina el resultado de cada jugador. El croupier crea su mazo, pero recibe la estrategia por constructor, porque el mazo es fijo y la regla de juego puede variar.

### Estrategia del croupier

`EstrategiaCroupier` es una interfaz con un único método, `debePedirCarta(puntuacion)`. No hay estado ni comportamiento que compartir entre implementaciones, solo un contrato, por lo que una interfaz es la abstracción adecuada. El croupier depende de ella y no de una regla concreta (patrón Strategy). La implementación actual, `EstrategiaPlantarseEn17`, podría acompañarse de una variante que pida carta con 17 suave, como ocurre en algunos casinos, sin modificar `Croupier`.

### Ciclo de la partida

`JuegoDeCartas` aplica Template Method: `iniciarPartida()` contiene el ciclo común (registrar jugadores, jugar rondas mientras haya jugadores activos y cerrar la partida), y cada juego implementa esos cuatro pasos. Los métodos abstractos son únicamente los que la clase base invoca; así no se impone a los juegos ninguna obligación que no se utilice.

Se eligió una clase abstracta en lugar de una interfaz con un método `default` por dos motivos. Un método `default` siempre puede sobrescribirse, mientras que `iniciarPartida()` es `final` para que ningún juego altere el orden. Además, en una interfaz todos los métodos son públicos, y aquí los pasos son `protected` para que no se invoquen de forma aislada.

### Versiones del juego

Las dos versiones comparten las reglas y difieren solo en cuántos jugadores hay en la mesa y en cómo se recorren. Por ello `Blackjack` es también una clase abstracta: concentra la preparación de la ronda, las apuestas, los turnos y la liquidación, todos expresados para un jugador a la vez, y deja a `BlackjackUnJugador` y `BlackjackMultijugador` el registro de jugadores, la condición para continuar, el orden de la ronda y la elección del cortador. La primera administra un único `Jugador`; la segunda, una lista. De esta forma ninguna regla del juego está escrita dos veces.

`App` elige la versión según el número de jugadores y la trata como `JuegoDeCartas`, por lo que el ciclo de la partida es el mismo en ambos casos. El croupier y la consola ofrecen variantes sobrecargadas para un jugador y para una lista (`repartirManoInicial` y `mostrarMesa`), de modo que la versión 1 no necesita construir una lista de un solo elemento. Los atributos `croupier` y `consola` de `Blackjack` son `protected final`: las subclases los usan directamente, pero no pueden reemplazarlos.

### Separación entre lógica y presentación

La entrada y salida está separada en `ConsolaBlackjack` (Pure Fabrication), de modo que las clases del juego no contienen ningún texto y se limitan a coordinar la ronda. Cambiar mensajes, idioma o formato no requiere modificar la lógica del juego.

Cada método público de la consola corresponde a un momento de la partida, y los que siempre ocurren juntos se agrupan en uno solo (por ejemplo, anunciar la ronda y pedir el corte). Se descartó una consola mínima con métodos genéricos de lectura y escritura porque obligaría al juego a construir sus propios mensajes y reintroduciría texto en la lógica.

La validación quedó dividida según quién conoce cada regla. La consola valida el formato de la entrada (que sea un número, que esté en rango, que la respuesta sea "s" o "n"); el croupier valida que una apuesta respete la mínima y el saldo; y `BlackjackMultijugador`, que conoce la lista de jugadores, impide nombres repetidos sin distinguir mayúsculas ni espacios.

### Blackjack del croupier en el reparto

Cuando el croupier obtiene Blackjack en el reparto, el resultado de la ronda ya está decidido, así que se omiten los turnos de los jugadores, como ocurre en una mesa real. No se requiere un método adicional: el croupier revela su carta y juega su turno como en cualquier otra ronda, y al tener 21 no pide más cartas.

## Clases

### App

Punto de entrada del programa. Configura la salida en UTF-8, crea la vista, elige la versión del juego según el número de jugadores e inicia la partida. El juego se declara con el tipo `JuegoDeCartas`, por lo que cambiar de versión o de juego solo implica instanciar otra clase.

| Método | Acceso | Descripción |
|---|---|---|
| `main(String[] args)` | public static | Configura la salida, crea `ConsolaBlackjack`, instancia `BlackjackUnJugador` o `BlackjackMultijugador` y llama a `iniciarPartida()` |

### JuegoDeCartas (clase abstracta)

Define, mediante Template Method, el ciclo común a cualquier juego de cartas. No describe un juego concreto, así que nunca se instancia directamente: cada juego hereda de ella e implementa los cuatro pasos del ciclo.

| Método | Acceso | Descripción |
|---|---|---|
| `iniciarPartida()` | public final | Ejecuta el ciclo completo: registra a los jugadores, juega rondas mientras haya jugadores activos y finaliza la partida |
| `registrarJugadores()` | protected abstract | Paso de registro de los jugadores |
| `hayJugadoresActivos()` | protected abstract | Determina si queda al menos un jugador en la mesa |
| `jugarRonda()` | protected abstract | Juega una ronda completa |
| `finalizarPartida()` | protected abstract | Cierra la partida |

### Blackjack (clase abstracta, hereda de JuegoDeCartas)

Reglas comunes a las dos versiones: preparación de la ronda, apuestas, turnos y liquidación, cada una expresada para un jugador. Implementa `finalizarPartida()` y deja a las subclases los demás pasos del ciclo. No contiene textos: toda la entrada y salida la delega en `ConsolaBlackjack`.

| Método | Acceso | Descripción |
|---|---|---|
| `Blackjack(ConsolaBlackjack consola)` | protected | Guarda la consola, pide la apuesta mínima y crea al croupier con la estrategia `EstrategiaPlantarseEn17` |
| `elegirCortador(int numeroRonda)` | protected abstract | Jugador que corta el mazo en la ronda indicada |
| `prepararRonda()` | protected | Incrementa el número de ronda, prepara el mazo y lo corta en la posición que elige el cortador |
| `solicitarApuesta(Jugador jugador)` | protected | Recibe la apuesta del jugador y la vuelve a pedir hasta que el croupier la acepte |
| `turnoJugador(Jugador jugador)` | protected | Ofrece cartas al jugador hasta que se plante, llegue a 21 o se pase; con Blackjack se planta de inmediato |
| `turnoCroupier()` | protected | El croupier pide cartas mientras su estrategia lo indique |
| `resolverJugador(Jugador jugador)` | protected | Determina si el jugador ganó, empató o perdió, y el croupier paga, devuelve o cobra según el caso |
| `decidirContinuar(Jugador jugador)` | protected | Retira al jugador si ya no cubre la apuesta mínima o decide dejar de jugar |
| `finalizarPartida()` | protected | Pide a la consola el mensaje de cierre |

### BlackjackUnJugador (hereda de Blackjack)

Versión 1: un jugador contra el croupier. Administra un único `Jugador`, que siempre es quien corta el mazo.

| Método | Acceso | Descripción |
|---|---|---|
| `BlackjackUnJugador(ConsolaBlackjack consola)` | public | Crea la versión de un jugador |
| `registrarJugadores()` | protected | Registra el nombre y el saldo inicial del jugador |
| `hayJugadoresActivos()` | protected | Verdadero mientras el jugador siga en la mesa |
| `jugarRonda()` | protected | Prepara la ronda, recibe la apuesta, reparte, juega el turno del jugador (omitido si el croupier tiene Blackjack), revela la carta del croupier y juega su turno, liquida la apuesta, limpia las manos y pregunta si continúa |
| `elegirCortador(int numeroRonda)` | protected | Devuelve al único jugador |

### BlackjackMultijugador (hereda de Blackjack)

Versión 2: varios jugadores contra el croupier en la misma mesa. Administra la lista de jugadores, impide nombres repetidos y rota el turno de cortar.

| Método | Acceso | Descripción |
|---|---|---|
| `BlackjackMultijugador(int numeroJugadores, ConsolaBlackjack consola)` | public | Crea la versión de varios jugadores |
| `registrarJugadores()` | protected | Registra el nombre y el saldo inicial de cada jugador; vuelve a pedir el nombre cuando ya existe |
| `hayJugadoresActivos()` | protected | Comprueba que la lista de jugadores activos no esté vacía |
| `jugarRonda()` | protected | Aplica a cada jugador activo los pasos de la ronda: apuesta, turno, liquidación y decisión de continuar; el reparto, el turno del croupier y la limpieza se realizan una vez por ronda |
| `elegirCortador(int numeroRonda)` | protected | Rota el corte entre los jugadores activos según el número de ronda |
| `limpiarRonda()` | private | Vacía las manos del croupier y de todos los jugadores |
| `jugadoresActivos()` | private | Lista de jugadores que siguen en la mesa |
| `existeJugador(String nombre)` | private | Busca un jugador registrado con ese nombre, sin distinguir mayúsculas ni espacios |

### ConsolaBlackjack

Vista en consola y única clase que utiliza `Keyboard` y `System.out`. Contiene todos los textos del juego, valida el formato de los datos de entrada y repite la pregunta ante un dato inválido.

| Método | Acceso | Descripción |
|---|---|---|
| `pedirNumeroJugadores()` | public | Lee el número de jugadores, entre 1 y 6 |
| `pedirApuestaMinima()` | public | Pide la apuesta mínima de la mesa |
| `pedirNombreJugador(int numero, boolean repetido)` | public | Pide el nombre de un jugador; con `repetido` en verdadero, antes avisa que el nombre anterior ya estaba en uso |
| `pedirSaldoInicial(int apuestaMinima)` | public | Lee el saldo inicial, que debe cubrir al menos la apuesta mínima |
| `iniciarRonda(int numeroRonda, Jugador cortador, int maximo)` | public | Anuncia la ronda y pregunta al jugador en turno dónde cortar el mazo |
| `pedirApuesta(Jugador jugador, int apuestaMinima, boolean repetir)` | public | Muestra el saldo y lee la apuesta; con `repetir` en verdadero, primero avisa que la anterior fue inválida |
| `mostrarMesa(Croupier croupier, Jugador jugador)` | public | Presenta la mano del croupier, con su carta oculta mientras esté tapada, y la del jugador (versión 1) |
| `mostrarMesa(Croupier croupier, List<Jugador> jugadores)` | public | Igual que la anterior, con la mano de cada jugador (versión 2) |
| `preguntarPedirCarta(Jugador jugador)` | public | Enseña la mano del jugador y le pregunta si quiere otra carta |
| `mostrarParticipante(Participante participante)` | public | Imprime la mano de un participante después de recibir una carta |
| `mostrarFinTurno(Participante participante)` | public | Informa si el turno terminó con Blackjack, con 21 o pasándose |
| `mostrarResultado(Jugador jugador, int apuesta, boolean gano, boolean empato)` | public | Comunica si el jugador ganó, empató o perdió, junto con su saldo actual |
| `preguntarContinuar(Jugador jugador, boolean tieneFondos)` | public | Avisa que el jugador se quedó sin fondos o, si aún tiene, le pregunta si sigue jugando |
| `mostrarFinPartida()` | public | Anuncia el fin de la partida |
| `dinero(int cantidad)` | private | Da formato monetario a una cantidad, por ejemplo `$1,500` |
| `mostrarCroupierEnMesa(Croupier croupier)` | private | Encabezado común de la mesa: anuncia si se reveló la carta y muestra al croupier |
| `describirJugador(Jugador jugador)` | private | Arma la línea de un jugador en la mesa: cartas, puntos, saldo y apuesta |
| `mostrarMensaje(String mensaje)` | private | Escribe una línea en consola |
| `leerEntero(String mensaje)` | private | Muestra el mensaje y lee un número entero |
| `leerEnteroEnRango(String mensaje, int minimo, int maximo)` | private | Repite la lectura hasta obtener un entero dentro del rango |
| `leerTexto(String mensaje)` | private | Muestra el mensaje y lee un texto |
| `preguntarSiNo(String pregunta)` | private | Insiste hasta recibir "s" o "n" y devuelve la respuesta como booleano |

### Participante (clase abstracta)

Reúne lo que tienen en común `Jugador` y `Croupier`: un nombre, una mano y las operaciones sobre ella. En la mesa solo hay jugadores y croupier, nunca un participante genérico, y por eso la clase se declaró abstracta. La mano se crea en su constructor.

| Método | Acceso | Descripción |
|---|---|---|
| `Participante(String nombre)` | public | Crea al participante con su nombre y una mano vacía |
| `recibirCarta(Carta carta)` | public | Agrega una carta a su mano |
| `limpiarMano()` | public | Vacía la mano para una nueva ronda |
| `sePaso()` | public | Comprueba si la mano supera 21 puntos |
| `tieneBlackjack()` | public | Determina si la mano es Blackjack |
| `tiene21()` | public | Verdadero cuando la mano suma exactamente 21 |
| `getPuntos()` | public | Puntos actuales de la mano |
| `getNombre()` | public | Nombre del participante |
| `getMano()` | public | Acceso a la mano |
| `toString()` | public | Texto con nombre, cartas y puntos, por ejemplo `Ana: As de ♥ \| Rey de ♠ (21 puntos)` |

### Jugador (hereda de Participante)

Jugador de la mesa. A lo heredado agrega el saldo, la apuesta de la ronda y su estado (plantado o retirado). El saldo se ajusta al liquidar la ronda, no al momento de apostar.

| Método | Acceso | Descripción |
|---|---|---|
| `Jugador(String nombre, int saldo)` | public | Crea al jugador con su nombre y saldo inicial |
| `apostar(int apuesta)` | public | Registra la apuesta de la ronda |
| `ganarApuesta()` | public | Suma la apuesta al saldo (pago 1 a 1) |
| `ganarBlackjack()` | public | Suma 1.5 veces la apuesta (pago 3 a 2) |
| `perderApuesta()` | public | Resta la apuesta del saldo |
| `recuperarApuesta()` | public | Empate: limpia la apuesta y deja el saldo igual |
| `plantarse()` | public | Marca que ya no pedirá cartas en la ronda |
| `estaPlantado()` | public | Consulta si el jugador se plantó |
| `limpiarMano()` | public | Sobrescribe el de `Participante`: vacía la mano y reinicia el estado de plantado |
| `puedeApostar(int apuestaMinima)` | public | Comprueba que el saldo alcance la apuesta mínima |
| `retirarse()` | public | Saca al jugador de la mesa |
| `estaActivo()` | public | Verdadero mientras el jugador siga en la mesa |
| `getSaldo()` | public | Saldo actual |
| `getApuesta()` | public | Apuesta de la ronda en curso |

### Croupier (hereda de Participante)

Posee la baraja, reparte, administra las apuestas, juega su propia mano según una estrategia y determina el resultado de cada jugador. Crea su propio `Mazo` y recibe la estrategia desde el constructor.

| Método | Acceso | Descripción |
|---|---|---|
| `Croupier(String nombre, EstrategiaCroupier estrategia, int apuestaMinima)` | public | Crea al croupier con su mazo, su estrategia y la apuesta mínima de la mesa |
| `prepararMazo()` | public | Rellena el mazo con las 52 cartas y lo baraja |
| `partirMazo(int posicion)` | public | Corta el mazo en la posición elegida por un jugador |
| `cartasEnMazo()` | public | Cantidad de cartas que quedan en el mazo |
| `repartirCarta(Participante participante)` | public | Entrega una carta; si el mazo se agotó, lo vuelve a preparar |
| `repartirManoInicial(Jugador jugador)` | public | Reparte dos cartas al jugador y dos para sí, con la segunda boca abajo (versión 1) |
| `repartirManoInicial(List<Jugador> jugadores)` | public | Reparte dos cartas a cada jugador y dos para sí, con la segunda boca abajo (versión 2) |
| `debePedirCarta()` | public | Consulta a la estrategia según sus puntos actuales |
| `revelarCarta()` | public | Destapa la carta oculta |
| `tieneCartaOculta()` | public | Consulta si la segunda carta sigue tapada |
| `getCartaVisible()` | public | Primera carta, siempre a la vista |
| `getApuestaMinima()` | public | Apuesta mínima de la mesa |
| `esApuestaValida(Jugador jugador, int cantidad)` | public | Valida que la cantidad alcance la mínima y no supere el saldo del jugador |
| `puedeSeguirJugando(Jugador jugador)` | public | Comprueba si el jugador conserva saldo para la apuesta mínima |
| `recibirApuesta(Jugador jugador, int cantidad)` | public | Registra la apuesta; lanza `IllegalArgumentException` si no es válida |
| `pagarApuesta(Jugador jugador)` | public | Paga 3 a 2 con Blackjack y 1 a 1 en cualquier otra victoria |
| `cobrarApuesta(Jugador jugador)` | public | Cobra la apuesta del jugador que perdió |
| `devolverApuesta(Jugador jugador)` | public | Regresa la apuesta en caso de empate |
| `ganaJugador(Jugador jugador)` | public | Determina si el jugador le gana, por Blackjack o por más puntos sin pasarse |
| `esEmpate(Jugador jugador)` | public | Hay empate cuando nadie se pasó, tienen los mismos puntos y ambos tienen o no tienen Blackjack |

### Mano

Cartas de un participante y reglas de puntuación del Blackjack. Define la constante `PUNTOS_BLACKJACK = 21`. Los puntos no se almacenan: se recalculan en cada consulta.

| Método | Acceso | Descripción |
|---|---|---|
| `agregarCarta(Carta carta)` | public | Añade una carta |
| `getCartas()` | public | Las cartas, en una lista de solo lectura |
| `calcularPuntos()` | public | Puntuación de la mano; el As vale 11 salvo que eso haga superar 21 |
| `puntosBase()` | private | Suma contando cada As como 1 y cada figura como 10 |
| `esSuave()` | public | Detecta si hay un As que puede valer 11 sin superar 21 |
| `cantidadCartas()` | public | Número de cartas en la mano |
| `esBlackjack()` | public | Verdadero cuando la mano suma 21 con exactamente dos cartas |
| `sePaso()` | public | Comprueba si la mano supera 21 |
| `vaciar()` | public | Quita todas las cartas |
| `toString()` | public | Las cartas separadas por ` \| ` |

### Mazo

Baraja de 52 cartas almacenada en un `ArrayList`.

| Método | Acceso | Descripción |
|---|---|---|
| `Mazo()` | public | Crea el mazo con las 52 cartas |
| `barajar()` | public | Mezcla las cartas con `Collections.shuffle` (algoritmo Fisher-Yates) |
| `partir(int posicion)` | public | Corta el mazo con `Collections.rotate`: las cartas por encima del corte pasan al fondo; lanza `IllegalArgumentException` si la posición no divide el mazo |
| `cartasRestantes()` | public | Cantidad de cartas que quedan |
| `sacarCarta()` | public | Retira la carta superior; lanza `IllegalStateException` si el mazo está vacío |
| `estaVacio()` | public | Consulta si ya no quedan cartas |
| `rellenar()` | public | Vacía el mazo y lo llena de nuevo con las 52 cartas |

### Carta (record)

Representa una carta mediante dos componentes, `palo` (`Palo`) y `rango` (`Rango`). Se declaró como `record` para que sea inmutable: una vez repartida, ninguna parte del programa puede alterarla.

| Método | Acceso | Descripción |
|---|---|---|
| `palo()`, `rango()` | public | Accesores generados por el `record` |
| `esAs()` | public | Determina si la carta es un As |
| `esFigura()` | public | Comprueba si es Jota, Reina o Rey |
| `toString()` | public | Reemplaza el texto generado por el `record`, por ejemplo `Rey de ♠` |

### Rango (enum)

Los 13 rangos de la baraja inglesa: `AS`, `DOS`, `TRES`, `CUATRO`, `CINCO`, `SEIS`, `SIETE`, `OCHO`, `NUEVE`, `DIEZ`, `JOTA`, `REINA` y `REY`. Al tratarse de un conjunto fijo, el tipo enumerado evita rangos inválidos. Cada constante guarda su nombre y su posición natural (As = 1 … Rey = 13), no los puntos de un juego en particular.

| Método | Acceso | Descripción |
|---|---|---|
| `Rango(int valor, String nombre)` | private | Constructor de cada constante |
| `getNombre()` | public | Nombre del rango, por ejemplo `"Reina"` |
| `getValor()` | public | Posición de la carta, de 1 a 13 |

### Palo (enum)

Los cuatro palos, `CORAZON`, `DIAMANTE`, `TREBOL` y `PICA`, cada uno con el símbolo que se imprime en consola. Igual que `Rango`, se modeló como tipo enumerado.

| Método | Acceso | Descripción |
|---|---|---|
| `Palo(String simbolo)` | private | Constructor de cada constante |
| `getSimbolo()` | public | Símbolo del palo, por ejemplo `"♥"` |

### EstrategiaCroupier (interfaz)

Contrato con la regla que sigue el croupier para decidir si pide otra carta. Entre las posibles reglas no hay estado ni código común, así que basta con una interfaz; `Croupier` depende de ella y no de una implementación concreta (patrón Strategy).

| Método | Acceso | Descripción |
|---|---|---|
| `debePedirCarta(int puntuacion)` | public abstract | Verdadero si, con esa puntuación, el croupier debe pedir otra carta |

### EstrategiaPlantarseEn17 (implementa EstrategiaCroupier)

Estrategia estándar de casino. Define la constante `LIMITE_PARA_PLANTARSE = 17`.

| Método | Acceso | Descripción |
|---|---|---|
| `debePedirCarta(int puntuacion)` | public | Pide carta mientras la puntuación sea menor a 17 |

### Keyboard

Utilería de lectura de teclado proporcionada en el curso (Lewis y Loftus), con métodos estáticos. El juego utiliza `readInt()` y `readString()`, y a la clase se le agregó `readLine()` para leer una línea completa.

## Trabajo futuro

`Mano` y `Participante` todavía contienen reglas propias del Blackjack. Cuando se incorpore un segundo juego, el siguiente paso será separarlas en una capa genérica (Mano, Participante) y una específica (`ManoBlackjack`, `ParticipanteBlackjack`). Esta separación no se hizo todavía para no introducir abstracciones sin un segundo caso que las justifique.

De forma similar, las tareas de barajar, repartir y administrar apuestas son comunes a otros juegos de casino, pero solo en Blackjack el croupier juega su propia mano. Una evolución natural sería extraer esas tareas a una clase `Repartidor` utilizada por composición.

## Diagrama de clases

![Diagrama de clases](docs/JuegoBaraja.png)

Los archivos editables se encuentran en `docs/`: [`JuegoBaraja.drawio`](docs/JuegoBaraja.drawio) para el diagrama de clases y [`flowBlackjack.drawio`](docs/flowBlackjack.drawio) para el flujo de una partida.

## Autores

- Sebastián Verdugo Bermúdez ([Sebastian1247](https://github.com/Sebastian1247))
- Esteban Verduzco Raggio ([OlHonder](https://github.com/OlHonder))
- Daniel Verduzco Raggio ([19170700](https://github.com/19170700))

Tecnologías de Programación, Maestría en Ciencias de la Computación, 1er semestre. [Tecnológico Nacional de México, Campus Culiacán](https://www.culiacan.tecnm.mx/posgrados/maestria-en-ciencias-de-la-computacion/)
