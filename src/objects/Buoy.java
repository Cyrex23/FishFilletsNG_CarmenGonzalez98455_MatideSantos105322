package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class Buoy extends Movable {

    public Buoy(Point2D position, Room room) {
        super(position, room, "buoy", Weight.LIGHT);
    }

    @Override
    public void applyGravity() {
        List<GameObject> supportedObjs = getAdjacentMovableObjects(Direction.UP.asVector());
        if (!supportedObjs.isEmpty()) {
            super.applyGravity();
        } else {
            this.move(Direction.UP.asVector());
        }
    }
}
