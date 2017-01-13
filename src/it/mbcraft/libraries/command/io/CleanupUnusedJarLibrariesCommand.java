package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class CleanupUnusedJarLibrariesCommand implements ICommand {

    private Logger logger = LogManager.getLogger(CleanupUnusedJarLibrariesCommand.class);

    private final File jarFile;
    private final File folderToClear;
    private final boolean modeDeleteOnExit;

    public CleanupUnusedJarLibrariesCommand(File jar,File toClear,boolean deleteOnExit) {
        if (jar==null || !jar.exists() || !jar.canRead()) throw new InvalidParameterException("Jar file can't be null, must exist and be readable.");
        if (toClear==null || !toClear.exists() || !toClear.canRead() || !toClear.canWrite()) throw new InvalidParameterException("Folder to clear can't be null, must exist and be readable and writable.");

        jarFile = jar;
        folderToClear = toClear;
        modeDeleteOnExit = deleteOnExit;
    }

    @Override
    public void execute() {

        if (jarFile.exists()) {
            GetJarDependenciesCommand getMainJarDeps = new GetJarDependenciesCommand(jarFile);
            getMainJarDeps.execute();

            File mainJarDir = jarFile.getParentFile();
            Set<File> requiredLibraries = new HashSet<>();
            for (String requiredJarPath : getMainJarDeps.getLibraries()) {  //NPE here!!!
                requiredLibraries.add(new File(mainJarDir,requiredJarPath));
            }
            
            Set<File> allLibraries = new HashSet<>();
            for (File f : folderToClear.listFiles()) {
                if (f.isFile()) allLibraries.add(f);
            }

            allLibraries.removeAll(requiredLibraries);
            for (File toDel : allLibraries) {
                if (modeDeleteOnExit) {
                    logger.info("Deprecated library marked for delete : " + toDel.getName());
                    toDel.deleteOnExit();
                } else {
                    logger.info("Deleting library : " + toDel.getName());
                    toDel.delete();
                }
            }

        } else {
            logger.error("Unable to find application main jarFile!");
        }
    }
}
