package a_presentation.model;

import a_presentation.view.ErrorDialog;
import javafx.scene.control.Alert;
import controller.logic.LogicController;
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
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsModel extends Observable {
	private File lastBrowserDir = null;
	private File originalsDir = null;
	private File dummiesDir = null;

	private Thread task = null;

	public synchronized boolean isBusy() {
		return task != null;
	}

	public File getOriginalsDir() {
		return originalsDir;
	}

	public void setOriginalsDir(final File originalsDir) {
		this.originalsDir = originalsDir;
		setChanged();
		notifyObservers();
	}

	public SettingsModel(Observer... observers) {
		for (Observer observer : observers) {
			addObserver(observer);
		}
	}

	public File getDummiesDir() {
		return dummiesDir;
	}

	public void setDummiesDir(final File dummiesDir) {
		this.dummiesDir = dummiesDir;
		setChanged();
		notifyObservers();
	}

	public File getLastBrowserDir() {
		return lastBrowserDir;
	}

	public void setLastBrowserDir(File lastBrowserDir) {
		this.lastBrowserDir = lastBrowserDir;
		setChanged();
		notifyObservers();
	}
	private Document createDocument() {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("dummyFileCreatorSettings");

		SettingsModel settingsModel = LogicController.getInstance().getSettingsModel();

		if (settingsModel.getOriginalsDir() != null)
			root.addElement("originalsDir").setText(settingsModel.getOriginalsDir().getPath());
		else {
			root.addElement("originalsDir").setText("");
		}
		if (settingsModel.getDummiesDir() != null)
			root.addElement("dummiesDir").setText(settingsModel.getDummiesDir().getPath());
		else {
			root.addElement("dummiesDir").setText("");
		}
		if (settingsModel.getLastBrowserDir() != null)
			root.addElement("lastBrowsed").setText(settingsModel.getLastBrowserDir().getPath());
		else {
			root.addElement("lastBrowsed").setText("");
		}
		return document;
	}

	public void saveToXML() {

		if (task == null) {
			task = new Thread(new Runnable() {
				@Override
				public void run() {

					Document document = createDocument();
					// lets write to a file
					try (FileWriter fileWriter = new FileWriter("DummyFileCreatorSettings.xml")) {
						OutputFormat format = OutputFormat.createPrettyPrint();
						XMLWriter writer = new XMLWriter(fileWriter, format);
						writer.write(document);
						writer.close();
						Logger.getGlobal().log(Level.INFO, "a_presentation.model.SettingsModel-File updated");

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

					task = null;
					setChanged();
					notifyObservers();
				}
			});
			setChanged();
			notifyObservers();
			task.start();
		}
	}

	private boolean validateXMLSchema(File xmlSource) {


		if (xmlSource == null) {
			throw new NullPointerException("XML-File is null!");
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
		} catch (IOException e) {
			new ErrorDialog(Alert.AlertType.WARNING, e, "Error", "The Validation failed!");
			return false;
		}
	}

	public void loadFromXML() {
		if (task == null) {
			task = new Thread(new Runnable() {
				@Override
				public void run() {


					File settingsXML = new File("DummyFileCreatorSettings.xml");
					if (settingsXML.exists() && settingsXML.canRead()) {
						Logger.getGlobal().log(Level.INFO, "a_presentation.model.SettingsModel-File foung. Loading...");
						if (validateXMLSchema(new File("DummyFileCreatorSettings.xml"))) {
							SAXReader reader = new SAXReader();
							try {
								Document document = reader.read(settingsXML);
								Element root = document.getRootElement();

//					// iterate through child elements of root
//					for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
//						Element element = it.next();
//						System.out.println(element.getName() + ": " + element.getText());
//					}

								for (Iterator<Element> it = root.elementIterator("originalsDir"); it.hasNext(); ) {
									Element element = it.next();
									System.out.println(element.getName() + ": " + element.getText());
									originalsDir = new File(element.getText());
								}
								for (Iterator<Element> it = root.elementIterator("dummiesDir"); it.hasNext(); ) {
									Element element = it.next();
									System.out.println(element.getName() + ": " + element.getText());
									dummiesDir = new File(element.getText());
								}
								for (Iterator<Element> it = root.elementIterator("lastBrowsed"); it.hasNext(); ) {
									Element element = it.next();
									System.out.println(element.getName() + ": " + element.getText());
									lastBrowserDir = new File(element.getText());
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


								Logger.getGlobal().log(Level.INFO, "a_presentation.model.SettingsModel successfully loaded");
							} catch (DocumentException e) {
								new ErrorDialog(Alert.AlertType.ERROR, e, "Error", "Loading of a_presentation.model.SettingsModel failed!");
							}
						} else {
							Logger.getGlobal().log(Level.WARNING, "a_presentation.model.SettingsModel-File contains an error and can not be used!");
						}
					} else {
						Logger.getGlobal().log(Level.INFO, "a_presentation.model.SettingsModel-File not found. A new File will be created.");
						try {
							settingsXML.createNewFile();
						} catch (IOException e) {
							new ErrorDialog(Alert.AlertType.ERROR, e, "Error", "The Creation of the a_presentation.model.SettingsModel-File failed!");
						}
					}

					task = null;
					setChanged();
					notifyObservers();
				}
			});
			setChanged();
			notifyObservers();
			task.start();
		}

	}
}
