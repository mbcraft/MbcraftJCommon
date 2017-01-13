package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class TryDeleteRootOwnedFileCommand implements ICommand {

    private Logger logger = LogManager.getLogger(TryDeleteRootOwnedFileCommand.class);
    private final File toDelete;

    public TryDeleteRootOwnedFileCommand(File toDel) {
        toDelete = toDel;
    }

    @Override
    public void execute() {
        String command = "rm -f \""+toDelete.getAbsolutePath()+"\"";
        RunAsUnixRootCommand runAsRoot = new RunAsUnixRootCommand(command);
        runAsRoot.execute();
        if (!runAsRoot.wasSuccessful())
            logger.error("Unable to run rm as root");
    }

    public boolean wasDeletedSuccessfully() {
        return !toDelete.exists();
    }
}
