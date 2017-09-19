package c_persistance;

import a_presentation.view.ErrorDialog;
import javafx.scene.control.Alert;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

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
			Logger.getGlobal().log(Level.INFO, "c_persistance.XML_Manager-File updated");

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
	 * @return
	 * @throws IOException
	 */
	static boolean validateXMLSchema(final File xmlSource) throws IOException {
		if (xmlSource == null) {
			throw new NullPointerException();
		}
//		try (InputStream in = c_persistance.XSD_Validation.class.getResourceAsStream("/model.c_persistance/DummyFileCreatorSettings.xsd");
//		     BufferedReader xsdIn = new BufferedReader(new InputStreamReader(in))
//		) {
		try (BufferedInputStream xsdIn = new BufferedInputStream(
				XSD_Validation.class.getResourceAsStream("/res/DummyFileCreatorSettings.xsd"))) {
			try {
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Validator validator = factory.newSchema(new StreamSource(xsdIn)).newValidator();
				validator.validate(new StreamSource(xmlSource));
				return true;
			} catch (Exception e) {
				new ErrorDialog(Alert.AlertType.WARNING, e, "Warning", xmlSource.getPath() +
						" is NOT a valid XML-File for this program!");
				return false;
			}
		}
	}

	/**
	 * @return null on no valid file loaded
	 * @throws IOException
	 * @throws DocumentException
	 */
	static SettingsDTO loadFromXML() throws IOException, DocumentException {
		SettingsDTO loadedSettings = null;
		File settingsXML = new File("DummyFileCreatorSettings.xml");
		if (settingsXML.exists() && settingsXML.canRead()) {
			Logger.getGlobal().log(Level.INFO, "Setting-File found. Loading...");
			if (validateXMLSchema(new File("DummyFileCreatorSettings.xml"))) {
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