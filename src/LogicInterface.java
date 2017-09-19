package controller.logic;

import java.io.File;
import java.util.Observer;

public interface LogicInterface {

	public boolean setOriginalsDir(final File newOriginalsDir);

	public boolean setDummiesDir(final File newDummiesDir);

	public File getOriginalsDir();

	public File getDummiesDir();

	public boolean createDummies(Observer...observers);

	public File getLastBrowserDir();

	public boolean updateLastBrowserDir(File browserDir);

}
