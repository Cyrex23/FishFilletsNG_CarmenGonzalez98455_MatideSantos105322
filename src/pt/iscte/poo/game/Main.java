package pt.iscte.poo.game;

import objects.BigFish;
import objects.SmallFish;
import pt.iscte.poo.gui.ImageGUI;

public class Main {

	public static void main(String[] args) {
		ImageGUI gui = ImageGUI.getInstance();
		ImageGUI.getInstance().setName("Fish Fillets NG");
		GameEngine engine = new GameEngine();
		gui.setStatusMessage("Turns BigFish: " + BigFish.getInstance().getNumTurns() +
				             "; Turns SmallFish: " + SmallFish.getInstance().getNumTurns());
		gui.registerObserver(engine);
		gui.go();
	}
}
