package objects;

import Interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

public class HoledWall extends GameObject implements Transposable {
    public HoledWall(Point2D position, Room room) {
        super(position, room, "holedWall", 1);
    }

    @Override
    public boolean isTransposableBy(GameObject obj) {
        Vector2D dir = Vector2D.movementVector(obj.getPosition(), this.getPosition());
        //Os caranguejos entravam dentro da HoledWall e ocupavam a mesma casa. Agora já não ocorre.
        List<GameObject> objs = getRoom().getGameObjectsFromPoint2D(Direction.forVector(dir), obj);
        //Só objetos "small" (SmallFish, Cup, etc.) podem transpor a HoledWall (a holedWall não pode estar ocupada).
        return (obj.isSmall() && objs.size() < 2);
    }
}

