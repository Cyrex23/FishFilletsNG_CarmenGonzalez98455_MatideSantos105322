package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Detonator extends GameObject{

    public Detonator(Point2D position, Room room) {
        super(position, room, "detonator", 1);
    }


}
