package objects;

import Interfaces.Killable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

//O Torpedo é uma classe extra. As suas condições são:
//--> É projetado pelo SmallFish, se ele existir no tabuleiro, quando se pressiona a tecla T.
//--> O torpedo só aparece se ao lado do SmallFish existir espaço (se o SmallFish não estiver a colidir com nenhum objeto).
//--> O torpedo tem duas imagens: torpedoLeft e torpedoRight. A imagem é escolhida consoante a direção em que está o SmallFish a lançar o torpedo.
//--> O torpedo atravessa todos os objetos do tabuleiro e mata os Krabs (é uma forma de ataque do SmallFish). Só não mata os Krabs quando estão
//nas holedWalls (é o sítio seguro dos Krabs).
public class Torpedo extends Movable implements Killable {

    public Torpedo(Point2D position, Room room) {
        super(position, room, "torpedoLeft", Weight.HEAVY);
        setSmall(true);
    }

    @Override
    public boolean move(Vector2D v) {
        boolean b = super.move(v);
        if (!b) {
            death();
        }
        return b;
    }

    @Override
    public boolean isCollisionCondition(GameObject obj) {
        if (obj instanceof Movable) {
            if (sendKill(obj)) {
                obj.death();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean sendKill(GameObject obj) {
        return obj instanceof Krab;
    }

    @Override
    public void applyGravity() {
        Vector2D dir = null;
        if (this.getName().contains("Left")) dir = Direction.LEFT.asVector();
        if (this.getName().contains("Right")) dir = Direction.RIGHT.asVector();
        move(dir);
    }
}
