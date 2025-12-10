package pt.iscte.poo.game;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Interfaces.Holder;
import Interfaces.ReactiveBehavior;
import objects.*;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Room {

	private List<GameObject> objects;
	private String roomName;
	private GameEngine engine;
	private Point2D smallFishStartingPosition;
	private Point2D bigFishStartingPosition;

	public Room() {
		objects = new ArrayList<>();
	}

	private void setName(String name) {
		roomName = name;
	}

	private void setEngine(GameEngine engine) {
		this.engine = engine;
	}

	public void addObject(GameObject obj) {
		objects.add(obj);
	}

	public void removeObject(GameObject obj) {
		synchronized(objects) {
			objects.remove(obj);
		}
	}

	public void removeAll() {
		//remove todos os elementos mas sem apagar o ArrayList (para apagar o ArrayList com o GarbageCollector era fazer objects = null.
		objects.clear();
	}

	public GameObject getObject(int index) {
		synchronized(objects) {
			if (index >= 0 && index < objects.size()) {
				return objects.get(index);
			}
			return null;
		}
	}

	//Devolve um único GameObject numa posição pretendida (Cup, Anchor, Bomb, etc)
	public GameObject getGameObjectFromPoint2D(Vector2D v, GameObject obj) {
		Point2D position = obj.getPosition().plus(v);
		for (int i = 0; i < getSizeObjects(); i++) {
			if (getObject(i).getPosition().equals(position)) {
				return getObject(i);
			}
		}
		return null;
	}

	//Devolve uma lista de GameObjects que estão na mesma position (temos de dar a direção que queremos) --> ex: ver objetos que estão
	//debaixo da bomba (peixe pequeno + trap; peixe pequeno + holed wall)
	public List<GameObject> getGameObjectsFromPoint2D(Direction dir, GameObject obj) {
		List<GameObject> objs = new ArrayList<>();
		Point2D position = obj.getPosition().plus(dir.asVector());
		for (int i = 0; i < getSizeObjects(); i++) {
			if (getObject(i).getPosition().equals(position)) {
				objs.add(getObject(i));
			}
		}
		return objs;
	}

	//Retornar uma lista dos objetos que estão numa mesma posição igual à do obj do argumento
	public List<GameObject> getGameObjectsFromPosition(GameObject obj) {
		List<GameObject> objs = new ArrayList<>();
		Point2D position = obj.getPosition();
		for (int i = 0; i < getSizeObjects(); i++) {
			if (getObject(i).getPosition().equals(position)) {
				objs.add(getObject(i));
			}
		}
		return objs;
	}

	//Verifica se uma lista só contém objetos layer 2.
	public boolean containsOnlyFish(List<GameObject> objs) {
		boolean fish = false;
		boolean otherObject = false;

		for (GameObject obj : objs) {
			if (obj.getLayer() == 2) {
				fish = true;
			} else if (obj.getLayer() == 1) {
				otherObject = true;
			}

			if (fish && !otherObject) {
				return true;
			}
		}
		return false;
	}

	public int getSizeObjects() {
		synchronized(objects) {
			return objects.size();
		}
	}

	public void setSmallFishStartingPosition(Point2D heroStartingPosition) {
		this.smallFishStartingPosition = heroStartingPosition;
	}

	public Point2D getSmallFishStartingPosition() {
		return smallFishStartingPosition;
	}

	public void setBigFishStartingPosition(Point2D heroStartingPosition) {
		this.bigFishStartingPosition = heroStartingPosition;
	}

	public Point2D getBigFishStartingPosition() {
		return bigFishStartingPosition;
	}

	//As Water só vão ter um efeito visual. Não vão pertencer ao ArrayList objects.
	public void fillWaters() {
		Dimension dimension = ImageGUI.getInstance().getGridDimension();
		int x = (int) dimension.getWidth() / 48;
		int y = (int) dimension.getHeight() / 48;
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				ImageGUI.getInstance().addImage(new Water(new Point2D(i, j), this));
			}
		}
	}

	public static Room readRoom(File f, GameEngine engine) {
		Room r = new Room();
		Scanner scanner;
		String line;
		Point2D position;
		int x = 0;
		int y = 0;

		r.setEngine(engine);
		r.setName(f.getName());

		try {
			scanner = new Scanner(f);
			while(scanner.hasNextLine()){
				line = scanner.nextLine();

				for (int i = 0; i < line.length(); i++) {
					position = new Point2D(x, y);
					addNewObject(line.charAt(i), r, position);
					x++;
					if (x >= 10) {
						x = 0;
						y++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Erro ao ler o ficheiro dos rooms: " + e);
			throw new RuntimeException(e);
		}
		r.sortObjects();
		return r;
	}

	private static GameObject createObject(char c, Room r, Point2D position){
		switch(c) {
			case 'B':
				r.setBigFishStartingPosition(position);
				return BigFish.getInstance();
			case 'S':
				r.setSmallFishStartingPosition(position);
				return SmallFish.getInstance();
			case 'W':
				return new Wall(position, r);
			case 'H':
				return new SteelHorizontal(position, r);
			case 'V':
				return new SteelVertical(position, r);
			case 'C':
				return new Cup(position, r);
			case 'R':
				return new Stone(position, r);
			case 'A':
				return new Anchor(position, r);
			case 'b':
				return new Bomb(position, r);
			case 'T':
				return new Trap(position, r);
			case 'Y':
				return new Trunk(position, r);
			case 'X':
				return new HoledWall(position, r);
			case 'F':
				return new Buoy(position, r);
			default:
				return new Water(position, r);
		}
	}

	private static void addNewObject(char c, Room r, Point2D position) {
		GameObject object = createObject(c, r, position);
		//Se o objeto nao for Water, adicionamos ao arraylist objects.
		if (object.getLayer() != 0) {
			r.addObject(object);
		}
	}

	//Ordena-se o código por layers.
	//Comparator: número negativo → a vem antes de b; número positivo → b vem antes de a. Utilização da expressão lambda no sort.
	public void sortObjects(){
		objects.sort((a, b) -> a.getLayer() - b.getLayer());
	}

	public void applyReactiveBehaviors() {
		for (int i = objects.size() - 1; i >= 0; i--) {
			if (objects.get(i) instanceof ReactiveBehavior reactive) reactive.react();
		}
	}

	public void doAutomaticAction() {
		//Criamos uma cópia do ArrayList objects por problemas relacionados ao ConcurrentModificationException. Se percorrermos a lista objects e eliminarmos objetos,
		//estamos a alterar o tamanho da lista, mas ao mesmo tempo estamos a percorre-la, logo lança essa exceção.
		List<GameObject> list = new ArrayList<>(objects);

		//Pela ação de gravidade temos de começar ao contrário, do fim para o início. Se não fizermos dessa forma e tivermos vários objetos a cair na vertical em fila,
		//vão haver alguns objetos do fim que vão ter avanço e só quando se volta a fazer o for é que caem os objetos de cima (mas os de baixo já têm uma casa de avanço e não faz sentido).
		for (int i = list.size() - 1; i >= 0; i--) {
			GameObject obj = list.get(i);

			if (obj instanceof Movable m) m.applyGravity();
			if (obj instanceof Holder h) h.checkSupportedObjects();
		}
	}

	public void resetAnchors() {
		for (int i = 0; i <= objects.size() - 1; i++) {
			if (objects.get(i) instanceof Anchor anchor) anchor.setPushedOnce(false);
		}
	}
}