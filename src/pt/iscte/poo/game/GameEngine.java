package pt.iscte.poo.game;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import objects.*;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.BackgroundMusic;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.HighScore;
import pt.iscte.poo.utils.SoundManager;

//Exercicio de POO: Olá Hello World
//Magikarp
//Envia-me os comentarios dos exercicios
public class GameEngine implements Observer {

	private Map<String,Room> rooms;
	private Room currentRoom;
	private int lastTickProcessed = 0;
	private int level = 0;
	private static LocalTime start;
	private static LocalTime end;
	private static Duration duration;
	private BackgroundMusic backgroundMusic;

	public GameEngine() {
		rooms = new HashMap<String,Room>();
		loadGame();
		currentRoom = loadRoom(level);
		//O updateGUI só pode estar sempre depois de se definir o currentRoom. Não o poderíamos meter nas funções readRoom ou addObject
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
		SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
		BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());

		//Quando o Game Engine iniciar, o tempo começa a contar
		start = LocalTime.now();
		//O PrintTime é uma Thread que atualiza a barra superior do tabuleiro do jogo com os segundos que já se passaram.
		PrintTime pt = new PrintTime();
		pt.start();

		updateGUI();

		//Iniciar a música de fundo/background
		backgroundMusic = new BackgroundMusic("sounds/background.wav");
		backgroundMusic.playLoop();

		//Inicializar som da explosão
		SoundManager.init();
	}

	private void loadGame() {
		File[] files = new File("./rooms").listFiles();
		for (File f : files) {
			String name = f.getName();
			if (name.startsWith("room") && name.endsWith(".txt")) {
				rooms.put(f.getName(), Room.readRoom(f, this));
			}
		}
	}

	//Carrega só um nível no hashmap rooms em vez de carregar todos os rooms --> útil no reset com a tecla "R"
	private void loadGame(int level) {
		File f = new File("./rooms/room" + level + ".txt");
		rooms.put(f.getName(),Room.readRoom(f,this));
	}

	private Room loadRoom(int index) {
		return rooms.get("room" + index + ".txt");
	}

	private void changeLevel() {
		level++;
		ImageGUI.getInstance().clearImages();
		resetSettings();
		//Quando se muda de nível, guarda-se o número de turnos do início do nível (para, caso se queira dar reset, voltar ao número de turnos
		//que se tinha no início desse nível) e guarda-se esse valor na variável turnsLastLevel do GameCharacter.
		BigFish.getInstance().setTurnsLastLevel(BigFish.getInstance().getNumTurns());
		SmallFish.getInstance().setTurnsLastLevel(SmallFish.getInstance().getNumTurns());
	}

	private void resetSettings() {
		currentRoom = loadRoom(level);
		SmallFish.getInstance().setNotOut(true);
		BigFish.getInstance().setNotOut(true);
		SmallFish.getInstance().setTurn(true);
		BigFish.getInstance().setTurn(false);
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
		SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
		BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
		updateGUI();
	}

	@Override
	public void update(Observed source) {

		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();

			//Tecla M usada para ligar e desligar a música
			if (k == KeyEvent.VK_M) {
				if (backgroundMusic != null) {
					backgroundMusic.toggle();
				}
			} else if (k == KeyEvent.VK_R) {
				//--> Se estivermos no primeiro nível (room0) e dermos reset, vai buscar o getTurnsLastLevel() que nunca foi atualizado porque nunca se mudou de nível,
				//logo coloca a zero de novo --> a variável turnsLastLevel do GameCharacter continua a zero, logo a variável numTurns fica a zero porque demos reset.
				//--> Se já tivermos feito mudança de room e dermos refresh, ele dá reset do numTurns para o valor do início do nível.
				BigFish.getInstance().setNumTurns(BigFish.getInstance().getTurnsLastLevel());
				SmallFish.getInstance().setNumTurns(SmallFish.getInstance().getTurnsLastLevel());
				setStatusMessageTurn();
				ImageGUI.getInstance().clearImages();
				currentRoom.removeAll();
				//O loadGame(level) em vez de fazer reset de todos os níveis porque volta a ler os ficheiros txt iniciais, faz reset de apenas um nível.
				//É mais eficiente usar o loadGame(level) do que o loadGame(), neste caso.
				loadGame(level);
				resetSettings();
			//Ativa o torpedo (ver as condições do torpedo na classe Torpedo)
			} else if (k == KeyEvent.VK_T) {
				if (SmallFish.getInstance().isNotOut()) {
					SmallFish.getInstance().appearTorpedo();
				}
			} else if (k == KeyEvent.VK_SPACE) {
				SmallFish.getInstance().switchTurn();
				BigFish.getInstance().switchTurn();
			} else {
				if (Direction.isDirectionFish(k)) {
					if (SmallFish.getInstance().isNotOut() && SmallFish.getInstance().isTurn()) {
						SmallFish.getInstance().move(Direction.directionFor(k).asVector(), k);
					} else if (BigFish.getInstance().isNotOut() && BigFish.getInstance().isTurn()) {
						BigFish.getInstance().move(Direction.directionFor(k).asVector(), k);
					}
					if (!BigFish.getInstance().isNotOut() && !SmallFish.getInstance().isNotOut()) {
						if (level == rooms.size() - 1) {
							winGame();
						} else {
							changeLevel();
						}
					}
					//Verifica a morte dos peixes quando se pressionou uma tecla. Se não tivessemos esta condição, na mesma o peixe morria e aparecia o "Game Over",
					//porque passado 500ms, sem pressionar tecla, chamamos o endGame() no else que está abaixo
					if (SmallFish.getInstance().isDead() || BigFish.getInstance().isDead()) {
						endGame();
					}
				}
			}
		} else {
			//Os peixes podem morrer sem clicar em nenhuma tecla: por exemplo, o peixe pequeno não pode suportar objetos pesados;
			//podem levar a explosão de uma bomba; ou o peixe grande pode morrer pela atuação de vários objetos pesados
			if (SmallFish.getInstance().isDead() || BigFish.getInstance().isDead()) {
				endGame();
			}
		}
		//A cada 500ms, conta um tick. Existe uma thread que é o Ticker que chama o tick() que incrementa o tick a cada 500ms e chama o notifyObservers,
		//que neste caso, chama o método update do GameEngine (o GameEngine é o Observer).
		int t = ImageGUI.getInstance().getTicks();

		while (lastTickProcessed < t) {
			processTick();
			currentRoom.doAutomaticAction();
		}
		ImageGUI.getInstance().update();
	}

	private void winGame() {
		//Guarda-se na variável end, a hora a que se ganhou o jogo. Calcula-se a duração entre o inicio do jogo e o fim. Converte-se para segundos.
		long seconds = getTimeDuration();

		int turnsBig = BigFish.getInstance().getNumTurns();
		int turnsSmall = SmallFish.getInstance().getNumTurns();

		HighScore hs = new HighScore();
		hs.processWin(turnsBig, turnsSmall, seconds);

		ImageGUI.getInstance().dispose();
		System.exit(0);
	}

	public static long getTimeDuration() {
		end = LocalTime.now();
		duration = Duration.between(start, end);
		//Converte de Duration para long, que representa os segundos
		return duration.getSeconds();
	}

	//PrintTime é uma thread que está sempre a correr em paralelo. A cada 1 segundo (sleep(1000)), chama setStatusMessageTurn(). Se não tivessemos a thread PrintTime,
	//e só se chamasse setStatusMessageTurn dentro do update, o tempo só seria atualizado quando se carregasse numa tecla ou o Ticker chama-se o update
	private class PrintTime extends Thread {
		public void run() {
			try {
				while (true) {
					setStatusMessageTurn();
					sleep(1000);
				}
			} catch (Exception e) {
				System.err.println("Erro ao criar a thread da mensagem do label: " + e);
			}
		}
	}

	public static void setStatusMessageTurn() {
		ImageGUI.getInstance().setStatusMessage("Turns BigFish: " + BigFish.getInstance().getNumTurns() +
				"; Turns SmallFish: " + SmallFish.getInstance().getNumTurns() + "; Seconds: " + getTimeDuration() + " s");
	}

	private void endGame() {
		ImageGUI.getInstance().update();
		ImageGUI.getInstance().showMessage("Message", "GAME OVER!");
		ImageGUI.getInstance().dispose();
		System.exit(0);
	}

	private void processTick() {
		lastTickProcessed++;
	}

	public void updateGUI() {
		if (currentRoom != null) {
			currentRoom.fillWaters();
			for (int i = 0; i < currentRoom.getSizeObjects(); i++) {
				ImageGUI.getInstance().addImage(currentRoom.getObject(i));
			}
		}
	}
}
