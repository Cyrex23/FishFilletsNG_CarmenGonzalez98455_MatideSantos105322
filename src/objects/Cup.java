package objects;

import Interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Cup extends Movable implements Transposable {

    public Cup(Point2D position, Room room) {
        super(position, room, "cup", Weight.LIGHT);
        setSmall(true);
    }

    @Override
    public boolean isTransposableBy(GameObject obj) {
        //Só objetos "small" (SmallFish, Cup, etc.) podem transpor a HoledWall
        return obj instanceof HoledWall;
    }
}
