package objects;

import Interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject implements ImageTile {

	private Point2D position;
	private Room room;
	private String name;
	private int layer;
	private boolean small;
	private boolean pushable;


	public GameObject(Room room) {
		this.room = room;
	}

	public GameObject(Point2D position, Room room, String name, int layer) {
		this.position = position;
		this.room = room;
		this.name = name;
		this.layer = layer;
	}

	//Dá uma lista de objetos Movable adjacentes numa dada direção que queremos
	public List<GameObject> getAdjacentMovableObjects(Vector2D v) {
		List<GameObject> adjacentObjs = new ArrayList<>();
		GameObject obj = this;

		while (true) {
			obj = getRoom().getGameObjectFromPoint2D(v, obj);
			//Pára o ciclo se for null ou não Movable/Fixo (Holed Wall, Wall, Water, SteelHorizontal, Steel Vertical, SmallFish, BigFish)
			if (!(obj instanceof Movable)) {
				break;
			}
			adjacentObjs.add(obj);
		}
		return adjacentObjs;
	}

	public void death() {
		getRoom().removeObject(this);
		ImageGUI.getInstance().removeImage(this);
		ImageGUI.getInstance().update();
	}

	public boolean isCollision(Vector2D dir) {
		Point2D destination = this.getPosition().plus(dir);
		for (int i = 0; i < getRoom().getSizeObjects(); i++){
			GameObject obj = getRoom().getObject(i);
			if (obj.getPosition().equals(destination)){
				return isCollisionCondition(obj);
			}
		}
		return false;
	}

	public boolean isCollisionCondition(GameObject obj) {
		//Comportamento default da colisão
		if (obj instanceof Transposable transposable && transposable.isTransposableBy(this)) {
			return false;
		}
		return true;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public boolean isSmall() {
		return small;
	}

	public void setSmall(boolean small) {
		this.small = small;
	}

	public boolean isPushable() {
		return pushable;
	}

	public void setPushable(boolean pushable) {
		this.pushable = pushable;
	}
}
