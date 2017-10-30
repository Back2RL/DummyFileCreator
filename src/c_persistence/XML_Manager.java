package c_persistence;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XML_Manager {

	/**
	 * @param settingsDTO
	 * @return
	 */
	private static Document createDocument(final SettingsDTO settingsDTO) {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("dummyFileCreatorSettings");

		if (settingsDTO.getOriginalsDir() != null)
			root.addElement("originalsDir").setText(settingsDTO.getOriginalsDir().getPath());
		else {
			root.addElement("originalsDir").setText("");
		}
		if (settingsDTO.getDummiesDir() != null)
			root.addElement("dummiesDir").setText(settingsDTO.getDummiesDir().getPath());
		else {
			root.addElement("dummiesDir").setText("");
		}
		if (settingsDTO.getLastBrowserDir() != null)
			root.addElement("lastBrowsed").setText(settingsDTO.getLastBrowserDir().getPath());
		else {
			root.addElement("lastBrowsed").setText("");
		}
		return document;
	}

	/**
	 * @param settingsDTO
	 * @throws IOException on error
	 */
	static void saveToXML(final SettingsDTO settingsDTO) throws IOException {
		Document document = createDocument(settingsDTO);
		// lets write to a file
		try (FileWriter fileWriter = new FileWriter("DummyFileCreatorSettings.xml")) {
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(fileWriter, format);
			writer.write(document);
			writer.close();
			Logger.getGlobal().log(Level.INFO, "Settings saved");

			// Pretty print the document to System.out
			writer = new XMLWriter(System.out, format);
			writer.write(document);

			// Compact format to System.out
			format = OutputFormat.createCompactFormat();
			writer = new XMLWriter(System.out, format);
			writer.write(document);
		}
	}

	/**
	 * @param xmlSource
	 * @param xsdSource
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws NullPointerException
	 */
	static boolean isValidXMLFile(final File xmlSource, final File xsdSource) throws IOException, SAXException, NullPointerException {
		boolean ok = false;
		try {
			if (xmlSource == null || xsdSource == null) {
				throw new NullPointerException();
			}

			try (
//				BufferedInputStream xsdIn = new BufferedInputStream(
//				XML_Manager.class.getResourceAsStream("/c_persistence/DummyFileCreatorSettings.xsd"))
					BufferedInputStream xsdIn = new BufferedInputStream(
							XML_Manager.class.getResourceAsStream(xsdSource.getPath()))
			) {

				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Validator validator = factory.newSchema(new StreamSource(xsdIn)).newValidator();
				validator.validate(new StreamSource(xmlSource));

				ok = true;
				return true;
			}
		} finally {
			if (ok) {
				Logger.getGlobal().log(Level.INFO, "Valid XML-File");
			} else {
				Logger.getGlobal().log(Level.WARNING, "Invalid XML-File");
			}
		}
	}

	/**
	 * @return null on no valid file loaded
	 * @throws IOException
	 * @throws DocumentException
	 */
	static SettingsDTO loadFromXML() throws IOException, DocumentException, SAXException {
		SettingsDTO loadedSettings = null;
		File settingsXML = new File("DummyFileCreatorSettings.xml");
		if (settingsXML.exists() && settingsXML.canRead()) {
			Logger.getGlobal().log(Level.INFO, "Setting-File found. Loading...");
			if (isValidXMLFile(new File("DummyFileCreatorSettings.xml"),
					new File("/c_persistence/DummyFileCreatorSettings.xsd"))) {
				SAXReader reader = new SAXReader();
				Document document = reader.read(settingsXML);
				Element root = document.getRootElement();

//					// iterate through child elements of root
//					for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
//						Element element = it.next();
//						System.out.println(element.getName() + ": " + element.getText());
//					}

				loadedSettings = new SettingsDTO();
				for (Iterator<Element> it = root.elementIterator("originalsDir"); it.hasNext(); ) {
					Element element = it.next();
					System.out.println(element.getName() + ": " + element.getText());
					loadedSettings.setOriginalsDir(new File(element.getText()));
				}
				for (Iterator<Element> it = root.elementIterator("dummiesDir"); it.hasNext(); ) {
					Element element = it.next();
					System.out.println(element.getName() + ": " + element.getText());
					loadedSettings.setDummiesDir(new File(element.getText()));
				}
				for (Iterator<Element> it = root.elementIterator("lastBrowsed"); it.hasNext(); ) {
					Element element = it.next();
					System.out.println(element.getName() + ": " + element.getText());
					loadedSettings.setLastBrowserDir(new File(element.getText()));
				}

//					// iterate through child elements of root with element name "foo"
//					for (Iterator<Element> it = root.elementIterator("foo"); it.hasNext(); ) {
//						Element foo = it.next();
//						// do something
//						System.out.println(foo.getText());
//					}
//
//					// iterate through attributes of root
//					for (Iterator<Attribute> it = root.attributeIterator(); it.hasNext(); ) {
//						Attribute attribute = it.next();
//						// do something
//						System.out.println(attribute.getName());
//					}
				Logger.getGlobal().log(Level.INFO, "Settings successfully loaded");
			} else {
				Logger.getGlobal().log(Level.WARNING, "Settings-File contains an error and can not be used!");
			}
		}
		return loadedSettings;
	}
}
