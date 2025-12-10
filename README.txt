README

O código do projeto foi realizado usando o IntelliJ como IDE.
Algumas funcionalidades adicionais relevantes no código são as seguintes:

-----------------------------------------------------------------------

         -> Explosão em cadeia das bombas <-

--> Na classe Bomb criaram-se threads para a explosão de cada uma das bombas. 

--> Se, durante a explosão, forem encontradas outras Bomb, é criada uma thread (BombExplosion) para cada bomba atingida.

--> Cada thread chama novamente a função explode, criando uma reação em cadeia.

--> Isto permite que várias bombas explodam de forma sequencial e fluida, simulando um efeito de propagação.

-----------------------------------------------------------------------

         -> Adição da classe Torpedo <-

A classe Torpedo é uma classe adicional cujo objeto tem as seguintes características:

--> É projetado pelo SmallFish, se ele existir no tabuleiro, quando se pressiona a tecla T.

--> O torpedo só aparece se ao lado do SmallFish existir espaço (se o SmallFish não estiver a colidir com nenhum objeto).

--> O torpedo tem duas imagens: torpedoLeft e torpedoRight. A imagem é escolhida consoante a direção em que está o SmallFish a lançar o torpedo.

--> O torpedo atravessa todos os objetos do tabuleiro e mata os Krabs (é uma forma de ataque do SmallFish). Só não mata os Krabs quando estão
nas holedWalls (é o sítio seguro dos Krabs).

Esta classe visa demonstrar a flexibilidade do código em relação a novas funcionalidades adicionais que se pode construir em cima do código base.

-----------------------------------------------------------------------

          -> PrintTime extends Thread <-

(pt.iscte.poo > game> GameEngine -> private class PrintTime extends Thread)

A thread PrintTime vai:
 -> prevenir os bugs visuais relativamente à barra que ilustra a duração do jogo em segundos e
     a contagem de movimentos dos peixes.
 -> permitir a flexibilidade do código sendo possível atribuir qualquer valor aos ticks (TICK_TIME)
     sem que afete a status message que mostra as informações em questão.

Sem a thread:
 - Embora seja possível implementar a duração do jogo sem a thread estaríamos dependentes de que o valor
     dos ticks fosse sempre <=1000 (1s), porque assim a status message estaria sempre a ser atualizada
     a tempo, porém se o tick atualizar com um tempo superior a 1 segundo podíamos observar o "salto"
     de alguns segundos (por exemplo: 1, 3, 5, 6, 8,...).



-----------------------------------------------------------------------

         -> class BackgroundMusic <-

(pt.iscte.poo > utils > BackgroundMusic)

A classe BackgroundMusic vai ser responsável pela reprodução de música de fundo do jogo.

Inicialização:
   -> Ao ser criado um novo BackgroundMusic, através do objeto Clip vamos procurar o ficheiro de audio
       e carregá-lo para a memória.

Funcionalidades:
   -> playLoop():
      - A música vai ser reproduzida de forma contínua. Colocamos o muted a false.
   -> stop():
      - A música vai parar. Colocamos o estado muted a true.
   -> toggle():
      - Através da tecla 'M' damos ao jogador a opção de escolher se a música toca ou não.
        Se a música estiver a tocar, ao ser pressionado 'M' a música pára e vice versa, isto
        utilizando as duas funções anteriores.


-----------------------------------------------------------------------

         -> class SoundManager <-

(pt.iscte.poo > utils > SoundManager)

A classe SoundManager vai gerir a reprodução dos efeitos sonoros da bomba.

Funcionalidades:
   -> init():
       - Vamos carregar o efeito sonoro da bomba para a variável explosionClip,
          de modo que nos seja possível a reprodução repetida à medida que as bombas explodem em cadeia.
   -> playExplosion():
      - Considerando que existe apenas uma única variável que contém o efeito sonoro, ao
         explodir uma bomba, o som da explosão anterior vai ser cortado. Caso o som não seja carregado
         não acontece nada de modo a evitar erros.
      - Verificamos se o som de uma explosão anterior ainda está a ser reproduzido. Caso isso se verifique paramos
         o som da explosão anterior e colocamos o som da explosão atual desde o início.
   -> class BombSoundExplosion extends Thread{}:
      - Através da thread garantimos o efeito sonoro fluído das bombas, fazendo uma reprodução em
        cadeia de explosões de bombas.


-----------------------------------------------------------------------

         -> getHighScoresBoard() <-

(pt.iscte.poo > utils > HighScore)

A função getHighScoreBoard() vai permitir exibir ao terminar o jogo as 10 melhores pontuações obtidas pelos jogadores.

-> Para representar esta interface visual recorremos ao uso de HTML, sendo possível a formatação dos dados em tabela.

    ->status message sem HTML:

         TOP 10:

         Position 1  name: teste1  seconds: 3 movements: 2
         Position 2  name: testetesteteste  seconds: 4 movements: 2


    -> status message com HTML:

        TOP 10:

        Position        Name        Seconds     Movements
        1           teste1              3               2
        2           testesteteste       4               2


Ou seja, com o uso de HTML temos uma visão mais clara e estética dos dados relativamente aos 10 melhores scores.
O código também limita o aparecimento dos nomes até 20 caracteres na tabela, truncando na tabela os restantes caracteres
que ultrapassam este valor.

-----------------------------------------------------------------------

         -> Eficência do código <-

--> As Water são apenas um efeito visual no código, não pertencendo ao ArrayList objects. As Water são adicionadas ao images 
através da função fillWaters() da classe Room.


--> Utilizamos um factory method onde se delega a criação dos objetos do Room. Esse método chama-se createObject e pertence à classe Room.
O factory method centraliza toda a lógica de criação num único sítio.


--> Usa-se o método do padrão incompleto no código, na função isCollisionCondition da classe GameObject. O código fixo encontra-se no método
isCollision que contém o método isCollisionCondition e há várias classes que dão Override e implementam esse método, de forma a que tenha um 
comportamento específico para cada classe.


--> Criaram-se interfaces que distinguem as características dos diversos tipos de objetos no jogo:

 + ApplyGravity: interface aplicada à classe abstrata Movable, onde pertencem os objetos sujeitos à gravidade; 

 + Deadly: contém um método killCondition, que indica as condições em que cada objeto morre;

 + Explosive: contêm os métodos explode, explosionTrigger e createBloodPattern. É aplicada a objetos explosivos como a Bomb;

 + Holder: é aplicada a objetos que suportam outros objetos (como é o caso do SmallFish e BigFish);

 + Killable: contém o método sendKill que indica as condições em que um objeto mata outro objeto; 

 + Pushable: contém o método applyPush, que indica a forma de empurrar de cada objeto e como se processa o push;

 + ReactiveBehavior: contém a função react. Esta interface é implementada por objetos que dependem do movimento dos GameCharacters
(ex: o Krab implementa a interface ReactiveBehavior e a função react contém o movimento aleatório esquerda/direita que é realizado
quando um dos GameCharacters se move;

 + Transposable: contém a função isTransposableBy. É utilizada para objetos transponíveis por outros objetos.


 --> Utilização de expressão lambda na função sortObjects() da classe Room.


 --> Anteriormente existia no template do projeto este método na classe Room:

     public List<GameObject> getObjects() {
	   return objects;
     } 

     Este método foi removido porque quebrava o encapsulamento. Qualquer outra classe teria acesso à lista completa (poderia fazer add, remove, etc),
     porque se estaria a fornecer a referência da lista às outras classes, podendo saltar validações. Atualmente, a classe que pretender adicionar ou remover
     objetos do ArrayList objects do Room pode fazê-lo apenas a partir dos métodos addObject e removeObject da classe Room.


 --> Na classe GameEngine, quando se faz reset de um nível invoca-se a função loadGame(level) em vez de loadGame(). Assim, em vez de se fazer reset de todos os níveis 
(como o loadGame() faz) porque volta a ler os ficheiros txt iniciais, faz-se reset de apenas um nível com o loadGame(level).

-----------------------------------------------------------------------

Comentários/considerações acerca de opções tomadas:

--> Considera-se que o SmallFish morre se estiver numa HoledWall e em cima da HoledWall estiver um objeto Heavy (como uma âncora) e o SmallFish 
clicar para ir para cima para suportar o objeto.

--> Quando a Trap cai em cima do SmallFish, é atravessada por ele e não morre.

--> O BigFish morre se suportar vários objetos Heavy ou 1 objeto Heavy e 1 ou mais objetos Light.