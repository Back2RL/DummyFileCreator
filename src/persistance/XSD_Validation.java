package persistance;

import com.sun.javaws.exceptions.InvalidArgumentException;
import gui.ErrorDialog;
import javafx.scene.control.Alert;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XSD_Validation {

	public static boolean validateXMLSchema(File xmlSource) {
		if (xmlSource == null) {
			throw new NullPointerException("XML-File is null!");
		}
//		try (InputStream in = XSD_Validation.class.getResourceAsStream("/persistance/DummyFileCreatorSettings.xsd");
//		     BufferedReader xsdIn = new BufferedReader(new InputStreamReader(in))
//		) {
		try (BufferedInputStream xsdIn = new BufferedInputStream(
				XSD_Validation.class.getResourceAsStream("/persistance/DummyFileCreatorSettings.xsd"))) {
			try {
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Validator validator = factory.newSchema(new StreamSource(xsdIn)).newValidator();
				validator.validate(new StreamSource(xmlSource));
				return true;
			} catch (Exception e) {
				Logger.getGlobal().log(Level.WARNING, e.getMessage());
				new ErrorDialog(Alert.AlertType.WARNING, e, "Warning", xmlSource.getPath() +
						" is NOT a valid XML-File for this program!");
				return false;
			}
		} catch (IOException e) {
			Logger.getGlobal().log(Level.WARNING, e.getMessage());
			new ErrorDialog(Alert.AlertType.WARNING, e, "Error", "The Validation failed!");
			return false;
		}
	}
}
