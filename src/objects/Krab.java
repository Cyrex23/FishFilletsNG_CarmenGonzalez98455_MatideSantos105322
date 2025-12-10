package objects;

import Interfaces.Deadly;
import Interfaces.Killable;
import Interfaces.ReactiveBehavior;
import Interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

import static java.lang.Thread.sleep;

public class Krab extends Movable implements Deadly, Killable, Transposable, ReactiveBehavior {

    private boolean firstTurnKrab;
    public Krab(Point2D position, Room room) {
        super(position, room, "krab", Weight.LIGHT);
        this.setLayer(2);
        setSmall(true);
        //Todos os Movable são pushable excepto o Krab que pusemos a false.
        setPushable(false);
    }

    @Override
    public void react() {
        //Garante que os Krabs não se movem logo que aparecem. Se comentarmos, movem-se logo que são criados.
        if (firstTurnKrab == true) {
            firstTurnKrab = false;
        }

        //n pode assumir 0 ou 1
        int n = (int)(Math.random() * 2);
        List<Direction> dirs = List.of(Direction.LEFT, Direction.RIGHT);

        super.move(dirs.get(n).asVector());
    }

    public void setFirstTurnKrab(boolean firstTurnKrab) {
        this.firstTurnKrab = firstTurnKrab;
    }

    @Override
    public boolean isTransposableBy(GameObject obj) {
        return (obj instanceof HoledWall);
    }

    @Override
    public boolean killCondition(GameObject obj) {
        return (obj instanceof Trap || obj instanceof BigFish);
    }

    @Override
    public boolean sendKill(GameObject obj) {
        if (obj instanceof SmallFish) {
            return true;
        }

        Vector2D dir = Vector2D.movementVector(this.getPosition(), obj.getPosition());
        //Os caranguejos conseguem matar o SmallFish se ambos estiverem dentro da HoledWall
        List<GameObject> objs = getRoom().getGameObjectsFromPoint2D(Direction.forVector(dir), this);
        if (objs.contains(SmallFish.getInstance())) {
            SmallFish.getInstance().death();
        }
        return false;
    }

    @Override
    public boolean isCollisionCondition(GameObject obj) {
        if (killCondition(obj)) {
            new killingKrab().start();
        }
        if (sendKill(obj)) {
            obj.death();
            return false;
        }
        return super.isCollisionCondition(obj);
    }

    @Override
    public void death() {
        //Metemos sleep para só executar o death() quando acabarmos de percorrer o ciclo for do moveAllKrabs. O problema é que estávamos a percorrer o
        //ArrayList krabs, enquanto eliminávamos krabs da lista e dava ConcurrentModificationException. Precisávamos de criar uma Thread só para o killKrab.
        try {
            Blood blood = new Blood(this.getPosition(), this.getRoom());
            sleep(20);
            ImageGUI.getInstance().addImage(blood);
            super.death();
            sleep(100);
            ImageGUI.getInstance().removeImage(blood);
            ImageGUI.getInstance().update();
        } catch(Exception e){
            System.err.println("Erro ao remover o krab da lista de objetos: " + e);
        }
    }

    private class killingKrab extends Thread {
        public void run() {
            try {
                death();
            } catch (Exception e) {
                System.err.println("Erro ao criar a thread do Krab: " + e);
            }
        }
    }
}
