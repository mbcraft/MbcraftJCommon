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

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class GetFileLastChangeTimeCommand implements ICommand {

    private final File target;
    private long lastModified;


    public GetFileLastChangeTimeCommand(File toCheck) {
        target = toCheck;
    }

    public long getLastModifiedTime() {
        return lastModified;
    }

    @Override
    public void execute() {
        lastModified = target.lastModified();
    }

}
