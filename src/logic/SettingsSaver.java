package logic;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsSaver implements Observer {
	private static SettingsSaver ourInstance = new SettingsSaver();

	public static SettingsSaver getInstance() {
		return ourInstance;
	}

	private SettingsSaver() {
	}

	@Override
	public void update(Observable o, Object arg) {

		Document document = createDocument();

		// lets write to a file
		try (FileWriter fileWriter = new FileWriter("DummyFileCreatorSettings.xml")) {
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(fileWriter, format);
			writer.write(document);
			writer.close();
			Logger.getGlobal().log(Level.INFO, "Settings-File updated");

			// Pretty print the document to System.out
			writer = new XMLWriter(System.out, format);
			writer.write(document);

			// Compact format to System.out
			format = OutputFormat.createCompactFormat();
			writer = new XMLWriter(System.out, format);
			writer.write(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document createDocument() {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("dummyFileCreatorSettings");

		Settings settings = LogicController.getInstance().getSettings();

		if (settings.getOriginalsDir() != null)
			root.addElement("originalsDir").setText(settings.getOriginalsDir().getPath());
		else {
			root.addElement("originalsDir").setText("");
		}
		if (settings.getDummiesDir() != null)
			root.addElement("dummiesDir").setText(settings.getDummiesDir().getPath());
		else {
			root.addElement("dummiesDir").setText("");
		}
		if (settings.getLastBrowserDir() != null)
			root.addElement("lastBrowsed").setText(settings.getLastBrowserDir().getPath());
		else {
			root.addElement("lastBrowsed").setText("");
		}
		return document;
	}
}
