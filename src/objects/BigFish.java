package objects;

import Interfaces.Deadly;
import Interfaces.Holder;
import Interfaces.Killable;
import Interfaces.Pushable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

public class BigFish extends GameCharacter implements Holder, Deadly, Killable, Pushable {
	private static BigFish bf = new BigFish(null);

	private BigFish(Room room) {
		super(room);
		setName("bigFishLeft");
		setLayer(2);
		setTurn(false);
	}

	public static BigFish getInstance() {
		return bf;
	}

	@Override
	public void turnRight() {
		setName("bigFishRight");
	}

	@Override
	public void turnLeft() {
		setName("bigFishLeft");
	}

	@Override
	public boolean killCondition(GameObject obj) {
		return (obj instanceof Trap);
	}

	@Override
	public boolean sendKill(GameObject obj){
		return (obj instanceof Krab);
	}

	@Override
	public boolean isCollisionCondition(GameObject obj){
		Vector2D dir = Vector2D.movementVector(this.getPosition(), obj.getPosition());
		if (this.killCondition(obj)) {
			death();
		}
		if (obj instanceof Movable) {
			if (sendKill(obj)) {
				obj.death();
				return false;
			}
			return applyPush(dir, obj);
		}
		return true;
	}

	@Override
	public boolean applyPush(Vector2D dir, GameObject obj) {
		if (checkMultiplePushHorizontal(dir)) {
			return false;
		} else {
			return !((Movable) obj).move(dir);
		}
	}

	@Override
	public void moveOutside() {
		if (SmallFish.getInstance().isNotOut()) {
			SmallFish.getInstance().switchTurn();
		}
	}

	@Override
	public void checkSupportedObjects() {
		//Obtemos uma lista dos objetos que estão acima do BigFish (esta lista não inclui objetos não movable - Wall, holedWall, SmallFish, etc)
		List<GameObject> supportedObjs = getAdjacentMovableObjects(Direction.UP.asVector());
		int countLight = 0;
		int countHeavy = 0;
		for (GameObject obj : supportedObjs) {
			if (((Movable) obj).getWeight() == Weight.LIGHT){
				countLight++;
			} else{
				countHeavy++;
			}
		}
		//Se o número de objetos pesados for superior a 1, mata-se o BigFish. Se houver um objeto pesado e vários leves, mata-se o BigFish.
		if (countHeavy > 1 || (countLight > 0 && countHeavy > 0) ) {
			death();
		}
	}

	public boolean checkMultiplePushHorizontal(Vector2D v) {
		List<GameObject> adjacentObjs = this.getAdjacentMovableObjects(v);
		//Converte um Vector2D numa Direction
		Direction dir = Direction.forVector(v);

		//Só há multiple push se houverem pelo menos dois objetos. A direção tem de ser horizontal.
		if (adjacentObjs.size() > 1 &&
				((dir == Direction.RIGHT) || (dir == Direction.LEFT))) {
			for (GameObject obj : adjacentObjs) {
				if (obj instanceof Anchor anchor && !anchor.canBePushedHorizontally()) {
					return false;
				}
			}

			//Vê primeiro se o último elemento da lista se move na direção pretendida (por isso o ciclo é ao contrário) --> puxa em cadeia a começar do fim
			for (int i = adjacentObjs.size() - 1; i >= 0 ; i--) {
				if (!((Movable) adjacentObjs.get(i)).move(v)) {
					return false;
				}
			}
			return true;
		}
		//Devolve false se a direção não for horizontal (LEFT ou RIGHT) e se não houverem pelo menos dois objetos
		return false;
	}

	@Override
	public void switchTurn() {
		super.switchTurn();
		getRoom().resetAnchors();
	}
}
