package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Blood extends GameObject {
    public Blood(Point2D position, Room room) {
        super(position, room, "blood", 1);
    }
}

