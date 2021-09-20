package aero.minova.rcp.rcp.util;

import java.util.HashMap;
import java.util.Map;

import aero.minova.rcp.form.setup.xbs.Map.Entry;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;

public class XBSUtil {

	/**
	 * Liefert die ERSTE Node mit dem angegebenen Namen aus den Preferences
	 * 
	 * @param preferences
	 *            aus der .xbs ausgelesenen Preferences
	 * @param name
	 *            der Name der gesuchten Node
	 */
	public static Node getNodeWithName(Preferences preferences, String name) {

		for (Node node : preferences.getRoot().getNode()) {
			Node nodeWithName = getNodeWithName(node, name);
			if (nodeWithName != null) {
				return nodeWithName;
			}
		}

		return null;
	}

	/**
	 * Liefert die ERSTE Node mit angegebenen Namen aus der übergebenen Parentnode (wenn die Parentnode den Namen hat wird sie zurückgegeben )
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public static Node getNodeWithName(Node node, String name) {
		if (node.getName().equals(name)) {
			return node;
		}

		for (Node childNode : node.getNode()) {
			Node nodeWithName = getNodeWithName(childNode, name);
			if (nodeWithName != null) {
				return nodeWithName;
			}
		}

		return null;
	}

	/**
	 * Liefert eine Map von Feldnamen in der OP zu Feldnamen in der Hauptmaske <br>
	 * TODO: Statt Feldnamen in der Hauptmaske können auch direkt Werte enthalten sein (mit @, siehe #825)<br>
	 * TODO: "visible"-Ausnahme entfernen, sobald das über Helper funktioniert
	 */
	public static Map<String, String> getKeynamesToValues(Node node) {
		Map<String, String> namesToValues = new HashMap<>();

		for (Entry entry : node.getMap().getEntry()) {
			if (!entry.getKey().contains("visible")) {
				namesToValues.put(entry.getKey(), entry.getValue());
			}
		}

		return namesToValues;
	}
}
