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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidParameterException;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class CopyFileCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(CopyFileCommand.class);
    private final File fileToCopy, destinationDir;

    public CopyFileCommand(File toCopy, File dirDest) {

        if (toCopy == null) throw new InvalidParameterException("Il file da copiare non può essere nullo!");
        if (!toCopy.isFile() || !toCopy.canRead())
            throw new InvalidParameterException("Il file da copiare non è un file o non può essere letto.");

        fileToCopy = toCopy;

        if (dirDest == null) throw new InvalidParameterException("La directory da copiare è nulla!");
        if (!dirDest.isDirectory() || !dirDest.canWrite())
            throw new InvalidParameterException("La destinazione non è una directory o non ha i permessi di scrittura!");

        destinationDir = dirDest;
    }

    @Override
    public void execute() {

        File destFile = new File(destinationDir, fileToCopy.getName());
        try {
            Files.copy(fileToCopy.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (fileToCopy.length()!=destFile.length())
                throw new IOException("Unable to correctly copy files with Files.copy!");
        } catch (IOException ex) {
            logger.error("Unable to copy files", ex);
        }
    }

}
