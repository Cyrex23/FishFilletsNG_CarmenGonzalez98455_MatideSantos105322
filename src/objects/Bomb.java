package objects;

import Interfaces.Explosive;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Bomb extends Movable implements Explosive {

    private boolean falling = false;

    public Bomb(Point2D position, Room room) {
        super(position, room, "bomb", Weight.LIGHT);
    }

    public boolean isFalling() {
        return falling;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    @Override
    public void explode(GameObject b) {
        List<GameObject> removeImages = new ArrayList<>();

        //Ativa-se o som da explosão
        SoundManager.playExplosionThread();

        //Lista das direções do Direction
        //Cria uma lista imutável (não se pode adicionar, remover ou alterar elementos)
        List<Direction> dirs = List.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);

        //Remover a bomba principal
        getRoom().removeObject(b);
        removeImages.add(b);

        //Guardar objetos vizinhos
        List<GameObject> neighbours = new ArrayList<>();

        for (Direction d : dirs) {
            List<GameObject> objs = getRoom().getGameObjectsFromPoint2D(d, b);
            for (GameObject obj : objs) {
                if (obj != null) {
                    if (obj instanceof SmallFish) {
                        SmallFish.getInstance().setDead(true);
                    }
                    if (obj instanceof BigFish) {
                        BigFish.getInstance().setDead(true);
                    }
                    neighbours.add(obj);

                    //Remove-se do ArrayList objects da classe Room
                    getRoom().removeObject(obj);
                    //Coloca-se na lista para remover das images (do ImageGUI)
                    removeImages.add(obj);
                }
            }
        }

        ImageGUI.getInstance().removeImages(removeImages);

        //As bloods só vão ter impacto na parte visual (não vão entrar no ArrayList de objects)
        List<GameObject> bloods = createBloodPattern(b);
        ImageGUI.getInstance().update();
        try {
            sleep(100);
        } catch (InterruptedException e) {
            System.err.println("Erro, deu a seguinte exceção: " + e);
        }
        ImageGUI.getInstance().removeImages(bloods);

        //Explosão em cadeia
        for (GameObject obj : neighbours) {
            if (obj instanceof Bomb) {
                BombExplosion be = new BombExplosion((GameObject) obj);
                be.start();
            }
        }
    }

    public class BombExplosion extends Thread {
        //Como a função explosionBomb exige um obj como argumento, temos de criar um GameObject e colocar o obj no construtor.
        GameObject obj;

        public BombExplosion(GameObject obj) {
            this.obj = obj;
        }

        public void run() {
            try {
                explode(obj);
            } catch (Exception e) {
                System.err.println("Erro, deu a seguinte exceção: " + e);
            }
        }
    }

    @Override
    public List<GameObject> createBloodPattern(GameObject obj) {
        List<GameObject> bloods = new ArrayList<>();
        List<Direction> dirs = List.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);
        for (Direction dir : dirs) {
            Point2D pos = obj.getPosition().plus(dir.asVector());
            Blood blood = new Blood(pos, getRoom());
            bloods.add(blood);
        }
        Blood bloodCenter = new Blood(obj.getPosition(), getRoom());
        bloods.add(bloodCenter);
        ImageGUI.getInstance().addImages(bloods);
        return bloods;
    }

    @Override
    public void applyGravity() {
        //A Bomb redefine a gravidade: em vez de apenas cair, ativa a lógica de explosão --> é uma "gravidade" diferente.
        explosionTrigger();
    }

    @Override
    public void explosionTrigger() {
        //Se a bomba realmente estiver a cair, o boolean falling vai passar a ser true. Se estiver apoiada no
        //inicio, continua a false.
        //O move pode dar false se no inicio a bomba estiver apoiada ou se colidir com um objeto depois de cair.
        if (move(Direction.DOWN.asVector())) {
            setFalling(true);
        } else {
            //Só quando a bomba já estiver a cair e o destino (posição abaixo dela) não contiver só um peixe, explode.
            //O destino da bomba (o que está debaixo dela) é uma lista de GameObject. Essa lista é criada em
            //getGameObjectsFromPoint2D(Direction dir, GameObject obj) da classe Room.
            if (isFalling() && !getRoom().containsOnlyFish(getRoom().getGameObjectsFromPoint2D(Direction.DOWN, this))) {
                BombExplosion be = new BombExplosion(this);
                be.start();
                try {
                    sleep(200);
                } catch (Exception e) {
                    System.err.println("Erro ao criar a explosão: " + e);
                }
            }
        }
    }
}
