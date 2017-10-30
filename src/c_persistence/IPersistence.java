package c_persistence;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public interface IPersistence {
	void saveToXML(final SettingsDTO settingsDTO) throws IOException;

	boolean validateXMLSchema(final File xmlSource) throws IOException, SAXException;

	SettingsDTO loadFromXML() throws IOException, DocumentException, SAXException;
}
