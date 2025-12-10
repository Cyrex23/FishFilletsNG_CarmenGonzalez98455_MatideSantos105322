package objects;

import Interfaces.Holder;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class Trunk extends GameObject implements Holder {
    public Trunk(Point2D position, Room room) {
        super(position, room, "trunk", 1);
    }

    @Override
    public void checkSupportedObjects() {
        //Obtemos uma lista dos objetos que estão acima do Trunk (esta lista não inclui objetos não movable - Wall, holedWall, SmallFish, etc)
        List<GameObject> supportedObjs = getAdjacentMovableObjects(Direction.UP.asVector());
        int countHeavy = 0;
        for (GameObject obj : supportedObjs) {
            //Contam-se os objetos pesados da lista
            if (((Movable) obj).getWeight() == Weight.HEAVY){
                countHeavy++;
            }
        }
        //Se o número de objetos pesados for superior a 0, remove-se o trunk (o trunk não suporta objetos pesados).
        if (countHeavy > 0) {
            death();
        }
    }
}
