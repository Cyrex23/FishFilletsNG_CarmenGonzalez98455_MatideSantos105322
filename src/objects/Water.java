package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Water extends GameObject{
	public Water(Point2D position, Room room) {
		super(position, room, "water", 0);
	}
}
