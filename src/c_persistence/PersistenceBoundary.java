package c_persistence;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class PersistenceBoundary implements IPersistence {

	public void saveToXML(final SettingsDTO settingsDTO) throws IOException {
		XML_Manager.saveToXML(settingsDTO);
	}

	public boolean validateXMLSchema(final File xmlSource) throws IOException, SAXException {
		return XML_Manager.isValidXMLFile(xmlSource, new File("/c_persistence/DummyFileCreatorSettings.xsd"));
	}

	public SettingsDTO loadFromXML() throws IOException, DocumentException, SAXException {
		return XML_Manager.loadFromXML();
	}

}
