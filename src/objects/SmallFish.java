package objects;

import Interfaces.Deadly;
import Interfaces.Holder;
import Interfaces.Pushable;
import Interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;
import java.util.List;

public class SmallFish extends GameCharacter implements Holder, Deadly, Transposable, Pushable {
	private static SmallFish sf = new SmallFish(null);

	private SmallFish(Room room) {
		super(room);
		setName("smallFishLeft");
		setLayer(2);
		setTurn(true);
		setSmall(true);
	}

	public static SmallFish getInstance() {
		return sf;
	}

	@Override
	public void turnRight() {
		setName("smallFishRight");
	}

	@Override
	public void turnLeft() {
		setName("smallFishLeft");
	}

	@Override
	public boolean isTransposableBy(GameObject obj) {
		//Só objetos "small" (SmallFish, Cup, etc.) podem transpor a Trap
		return (obj instanceof Trap);
	}

	@Override
	public boolean killCondition(GameObject obj) {
		if (obj instanceof Krab) {
			return true;
		}

		Vector2D dir = Vector2D.movementVector(this.getPosition(), obj.getPosition());
		if (dir.equals(Direction.UP.asVector()) && obj instanceof Movable && ((Movable) obj).getWeight() == Weight.HEAVY) {
			return true;
		}

		List<GameObject> objs = getRoom().getGameObjectsFromPoint2D(Direction.forVector(dir), this);
		return objs.stream().anyMatch(o -> o instanceof Krab);
	}

	//Usado para os objetos com que colide o peixe quando se move
	@Override
	public boolean isCollisionCondition(GameObject obj){
		//A função movementVector gera uma nova referência e quando fazemos dir == Direction.DOWN.asVector() não vai comparar bem, porque estamos a comparar as
		//referências e não os valores. Temos de fazer dir.equals(Direction.DOWN.asVector())
		Vector2D dir = Vector2D.movementVector(this.getPosition(), obj.getPosition());
		if(obj instanceof Detonator){
			getRoom().explodeAllBombs();
			return false;
		}
		if (killCondition(obj)) {
			death();
			return true;
		}
		if (!super.isCollisionCondition(obj)) {
			return false;
		}
		return !applyPush(dir, obj);
	}

	@Override
	public boolean applyPush(Vector2D dir, GameObject obj) {
		if (obj.isPushable() && ((Movable) obj).getWeight() == Weight.LIGHT) {
			if (!(obj instanceof Buoy && dir.equals(Direction.DOWN.asVector()))) {
				return ((Movable) obj).move(dir);
			}
		}
		return false;
	}

	@Override
	public void moveOutside() {
		if (BigFish.getInstance().isNotOut()) {
			BigFish.getInstance().switchTurn();
		}
	}

	@Override
	public void checkSupportedObjects() {
		//Obtemos uma lista dos objetos que estão acima do SmallFish (esta lista não inclui objetos não movable - Wall, holedWall, SmallFish, etc)
		List<GameObject> supportedObjs = getAdjacentMovableObjects(Direction.UP.asVector());
		int countLight = 0;
		int countHeavy = 0;
		for (GameObject obj : supportedObjs) {
			if (obj.getLayer() == 1) {
				if (((Movable) obj).getWeight() == Weight.LIGHT) {
					countLight++;
				}
				else {
					if(!isTransposableBy(obj))
						countHeavy++;
				}
			}
		}
		//Se o número de objetos leves for maior que um (o peixe está a suportar vários objetos leves) ou se o número de objetos pesados for maior
		//que 0 (o SmallFish não pode suportar objetos pesados), mata-se o SmallFish
		if (countLight > 1 || countHeavy > 0) {
			//getRoom().getGameObjectsFromPosition(this) devolve os objetos que estão na mesma posição que o peixe
			//Perspetiva do SmallFish: se o SmallFish estiver numa holedWall ou numa Trap, e em cima dele estiver um objeto pesado (ex: âncora),
			//o SmallFish nao morre.
			if (getRoom().containsOnlyFish(getRoom().getGameObjectsFromPosition(this))) {
				death();
			}
		}
	}

	public void appearTorpedo() {
		Direction dir = getName().contains("Right") ? Direction.RIGHT : Direction.LEFT;
		List<GameObject> adjacentObjs = getRoom().getGameObjectsFromPoint2D(dir, this);
		if (!adjacentObjs.isEmpty()) {
			return;
		}
		Point2D destination = this.getPosition().plus(dir.asVector());
		Torpedo torpedo = new Torpedo(destination, getRoom());
		String name = (dir == Direction.RIGHT) ? "torpedoRight" : "torpedoLeft";
		torpedo.setName(name);
		getRoom().addObject(torpedo);
		ImageGUI.getInstance().addImage(torpedo);
		ImageGUI.getInstance().update();
	}
}
