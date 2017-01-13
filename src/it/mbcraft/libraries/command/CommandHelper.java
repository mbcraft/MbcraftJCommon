/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.libraries.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


/**
 * This class contains generic helper methods.
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class CommandHelper {

    private static final Logger logger = LogManager.getLogger(CommandHelper.class);
    /**
     * Executes a command line task.
     * No output is returned.
     *
     * @param task The task to execute.
     */
    public static void executeCommandLineTask(ICommandLine task) {
        String cmd = task.getFullCommandLine();
        String envs[] = task.getEnvParams();

        Runtime rt = Runtime.getRuntime();
        try {
            logger.info("Running command : " + cmd);
            Process p = rt.exec(cmd, envs);
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            logger.error("", ex);
        }
    }

}