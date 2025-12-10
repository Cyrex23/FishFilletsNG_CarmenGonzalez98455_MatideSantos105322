package objects;

import Interfaces.ApplyGravity;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class Movable extends GameObject implements ApplyGravity {

    private Weight weight;

    public Movable(Point2D position, Room room, String name, Weight weight) {
        super(position, room, name, 1);
        this.weight = weight;
        setPushable(true);
    }

    public boolean move(Vector2D v) {
        Point2D destination = this.getPosition().plus(v);
        if (destination.getX() < 10 && destination.getX() >= 0
                && destination.getY() < 10 && destination.getY() >= 0 && !isCollision(v)) {
            setPosition(destination);
            return true;
        } else {
            return false;
        }
    }

    public Weight getWeight() {
        return weight;
    }

    @Override
    public void applyGravity() {
        this.move(Direction.DOWN.asVector());
    }
}
