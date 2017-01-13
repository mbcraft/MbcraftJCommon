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

import java.io.File;
import java.security.InvalidParameterException;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class CreateFolderCommand implements ICommand {

    private final File dirToCreate;

    public CreateFolderCommand(File dir) {
        if (dir == null) throw new InvalidParameterException("The directory to create can't be null.");

        dirToCreate = dir;
    }

    @Override
    public void execute() {
        dirToCreate.mkdirs();
    }

}
