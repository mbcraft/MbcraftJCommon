package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class RunAsUnixRootCommand implements ICommand {

    private final Logger logger = LogManager.getLogger(RunAsUnixRootCommand.class);
    private final String myCommand;

    private boolean executionSuccessful = true;

    public boolean wasSuccessful() {
        return executionSuccessful;
    }

    public RunAsUnixRootCommand(String command) {
        myCommand = command;
    }

    @Override
    public void execute() {
        logger.info("trying to run as root : " + myCommand);
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("sudo -n "+myCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String sudoResult = reader.readLine();
            if (sudoResult!=null && sudoResult.startsWith("sudo: a password is required"))
                executionSuccessful = false;
            p.waitFor();
        } catch (IOException ex) {
            logger.error("IOException in running command as superuser",ex);
        } catch (InterruptedException ex) {
            logger.error("Command interrupted when running as superuser",ex);
        }


    }
}
