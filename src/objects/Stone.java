package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Stone extends Movable {

    //Atributo da stone que indica se já originou um Krab
    private boolean crabSpawn;

    public Stone(Point2D position, Room room) {
        super(position, room, "stone", Weight.HEAVY);
    }

    public boolean isCrabSpawn() {
        return crabSpawn;
    }

    public void setCrabSpawn(boolean crabSpawn) {
        this.crabSpawn = crabSpawn;
    }

    @Override
    public boolean move(Vector2D v) {
        //chama o move da superclasse (Movable) que tenta apenas mover a stone.
        boolean b = super.move(v);

        //Criação do Krab
        creationCrab(b, v);

        return b;
    }

    public void creationCrab(boolean movementStone, Vector2D v) {
        Point2D positionUp = this.getPosition().plus(Direction.UP.asVector());
        //Se não pusessemos as condições da Direction criaria um Krab quando estivesse a cair pela gravidade
        if (movementStone && isCrabSpawn() == false && !isCollision(Direction.UP.asVector()) &&
                (Direction.forVector(v) == Direction.LEFT || Direction.forVector(v) == Direction.RIGHT)) {
            Krab krab = new Krab(positionUp, getRoom());
            getRoom().addObject(krab);
            ImageGUI.getInstance().addImage(krab);
            //Coloca-se o atributo a true porque já se criou o Krab em cima da pedra
            setCrabSpawn(true);
            krab.setFirstTurnKrab(true);
        }
    }
}
