package c_persistance;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class PersistanceBoundary {

	public static void saveToXML(final SettingsDTO settingsDTO) throws IOException {
		XML_Manager.saveToXML(settingsDTO);
	}

	public static boolean validateXMLSchema(final File xmlSource) throws IOException, SAXException {
		return XML_Manager.validateXMLSchema(xmlSource);
	}

	public static SettingsDTO loadFromXML() throws IOException, DocumentException, SAXException {
		return XML_Manager.loadFromXML();
	}

}
