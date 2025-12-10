package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;


public class Anchor extends Movable {

    //Depois de ser empurrada uma vez na horizontal, deixa de se mover por empurrão
    private boolean pushedOnce;

    public Anchor(Point2D position, Room room) {
        super(position, room, "anchor", Weight.HEAVY);
    }

    @Override
    public boolean move(Vector2D v) {
        Direction dir = Direction.forVector(v);

        //Só contamos empurrões horizontais (LEFT/RIGHT)
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {

            // Já foi empurrada antes → não se mexe mais por push
            if (pushedOnce) {
                return false;
            }

            //Primeiro empurrão: tenta mover normalmente
            boolean moved = super.move(v);
            if (moved) {
                setPushedOnce(true);
            }
            return moved;
        }

        //Qualquer movimento não horizontal (ex: gravidade para baixo) continua igual
        return super.move(v);
    }

    public void setPushedOnce(boolean pushedOnce) {
        this.pushedOnce = pushedOnce;
    }

    public boolean canBePushedHorizontally() {
        return !pushedOnce;
    }
}
