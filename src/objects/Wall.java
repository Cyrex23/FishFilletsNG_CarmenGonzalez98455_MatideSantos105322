package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Wall extends GameObject {
	public Wall(Point2D position, Room room) {
		super(position, room, "wall", 1);
	}
}
