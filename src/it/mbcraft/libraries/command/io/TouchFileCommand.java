/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.CodeClassification;
import it.mbcraft.libraries.command.CodeType;
import it.mbcraft.libraries.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;


/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class TouchFileCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(TouchFileCommand.class);
    private final File target;

    public TouchFileCommand(File toTouch) {
        target = toTouch;
    }

    @Override
    public void execute() {
        try {
            if (!target.exists())
                target.createNewFile();
            target.setLastModified(System.currentTimeMillis());
        } catch (IOException ex) {
            logger.error("", ex);
        }
    }

}
