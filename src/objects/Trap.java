package objects;

import Interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Trap extends Movable implements Transposable {

    public Trap(Point2D position, Room room) {
        super(position, room, "trap", Weight.HEAVY);
    }

    @Override
    public boolean isTransposableBy(GameObject obj) {
        //Só objetos "small" (SmallFish, Cup, etc.) podem transpor a Trap
        return (obj instanceof SmallFish);
    }
}
