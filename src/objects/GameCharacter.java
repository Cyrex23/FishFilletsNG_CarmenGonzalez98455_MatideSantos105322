package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;
import java.awt.event.KeyEvent;

public abstract class GameCharacter extends GameObject {
	private boolean turn;

	//Se o setNotOut for true significa que o GameCharacter está dentro do tabuleiro; se for false, significa que está fora do tabuleiro
	private boolean isNotOut = true;
	private int numTurns;
	private int turnsLastLevel;
	private boolean isDead;

	public GameCharacter(Room room) {
		super(room);
	}

	public void move(Vector2D dir, int k) {
		Point2D destination = this.getPosition().plus(dir);

		if (destination.getX() < 10 && destination.getX() >= 0
				&& destination.getY() < 10 && destination.getY() >= 0) {
			if (isTurn()) {
				if (!isCollision(dir)) {
					setPosition(destination);
					chooseDirection(k);
					setNumTurns(getNumTurns()+1);
					GameEngine.setStatusMessageTurn();
					//Movem-se todos os objetos que reagem ao movimento dos peixes que estão no tabuleiro (ex: krabs)
					getRoom().applyReactiveBehaviors();
				}
			}
		//Caso a posição de destino do peixe esteja fora dos limites (fora do tabuleiro): o peixe iria sair do tabuleiro
		} else {
			//Se um dos peixes saiu do tabuleiro e o outro peixe ainda está no tabuleiro, cede-lhe o turno.
			moveOutside();
			setNumTurns(getNumTurns()+1);
			GameEngine.setStatusMessageTurn();
			ImageGUI.getInstance().removeImage(this);
			//Temos de apagar o BigFish da lista objects porque senão o SmallFish não consegue ir para a saída porque iria colidir com o BigFish, devido
			//ao isCollision. Quando queremos colocar o smallFish na saída, clicamos na seta, que ativa as funções do ImageGUI, que chamam o update do
			//GameEngine e chama o move. O move chama o isCollision.
			getRoom().removeObject(this);
			//Quando o peixe sai do tabuleiro, não se apaga a sua instância, mas coloca-se o atributo isNotOut = false
			this.setNotOut(false);
		}
	}

	@Override
	public void death() {
		Blood blood = new Blood(this.getPosition(), this.getRoom());
		ImageGUI.getInstance().addImage(blood);
		super.death();
		setDead(true);
	}

	private void chooseDirection(int k) {
		switch (k) {
			case KeyEvent.VK_LEFT:
				turnLeft();
				break;
			case KeyEvent.VK_RIGHT:
				turnRight();
		}
	}

	public boolean isTurn() {
		return turn;
	}

	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	public void switchTurn() {
		setTurn(!turn);
	}

	public void setNotOut(boolean b) {
		isNotOut = b;
	}

	public boolean isNotOut() {
		return isNotOut;
	}

	public int getNumTurns() {
		return numTurns;
	}

	public void setNumTurns(int numTurns) {
		this.numTurns = numTurns;
	}

	public int getTurnsLastLevel() {
		return turnsLastLevel;
	}

	public void setTurnsLastLevel(int turnsLastLevel) {
		this.turnsLastLevel = turnsLastLevel;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean dead) {
		isDead = dead;
	}

	public abstract void turnLeft();

	public abstract void turnRight();

	public abstract void moveOutside();
}