package name.saak.empire.model.test;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.xml.sax.SAXException;

import name.saak.empire.model.Game;

public class Activator implements BundleActivator {

	static private Game game = null;

	@Override
	public void start(BundleContext context) throws Exception {
		getGame();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		getGame();
	}

	public static Game getGame() {
		if (game == null) {

			try {
				XmlProcessor xmlProcessor = new XmlProcessor(Game.class);
				String filename = System.getProperty("user.home") + "/Documents/america.xml";
				setGame((Game) xmlProcessor.load(new File(filename)));

			} catch (JAXBException | SAXException | IOException e) {
				e.printStackTrace();
			}
			System.out.println("Test");
		}
		return game;
	}

	public static void setGame(Game game) {
		Activator.game = game;
	}

}
