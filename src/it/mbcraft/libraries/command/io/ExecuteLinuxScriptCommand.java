package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidParameterException;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class ExecuteLinuxScriptCommand implements ICommand {

    private Logger logger = LogManager.getLogger(ExecuteLinuxScriptCommand.class);
    private boolean executedSuccessfully = false;
    private File myScriptFile;

    public ExecuteLinuxScriptCommand(File scriptFile) {
        myScriptFile = scriptFile;
        if (!myScriptFile.exists()) throw new InvalidParameterException("The script file does not exist!");
        if (!myScriptFile.canExecute()) {
            myScriptFile.setExecutable(true);
            if (!myScriptFile.canExecute())
                throw new IllegalStateException("Unable to set executable permission on script file.");
        }
    }

    public String getLaunchCommand() {

        try {
            File currentDir = new File(".");
            Path commandPath = myScriptFile.getCanonicalFile().toPath();
            Path currentDirPath = currentDir.getCanonicalFile().toPath();
            String relativeCommandPath = currentDirPath.relativize(commandPath).toString();

            return "./"+relativeCommandPath;
        } catch (IOException ex) {
            logger.error("Unable to get canonical path",ex);
            return null;
        }

    }

    public boolean wasExecutedSuccessfully() {
        return executedSuccessfully;
    }

    @Override
    public void execute() {

        String command = getLaunchCommand();

        if (command!=null) {
            try {
                Process p = Runtime.getRuntime().exec(command);
                p.waitFor();
                executedSuccessfully = true;
            } catch (IOException ex) {
                logger.error("Error during script execution.", ex);
            } catch (InterruptedException ex) {
                logger.error("Script interrupted during execution", ex);
            }
        }


    }
}
