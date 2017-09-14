package logic;

import java.io.File;

public interface LogicInterface {

	public boolean setOriginalsDir(final File newOriginalsDir);

	public boolean setDummiesDir(final File newDummiesDir);

	public File getOriginalsDir();

	public File getDummiesDir();

	public boolean createDummies();

	public File getLastBrowserDir();

	public boolean updateLastBrowserDir(File browserDir);

	public boolean getRunStatus();

}
