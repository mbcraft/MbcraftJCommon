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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extracts a zip archive into the specified folder.
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class UnpackArchiveCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(UnpackArchiveCommand.class);

    private final File archiveFile;
    private final File extractDirFile;

    public UnpackArchiveCommand(File archive, File extractDir) {
        if (archive == null) {
            throw new InvalidParameterException("The path of the archive can't be null.");
        }
        archiveFile = archive;


        if (extractDir == null) {
            throw new InvalidParameterException("The directory in which extract the archive can't be null.");
        }
        if (!extractDir.isDirectory()) {
            throw new InvalidParameterException("The specified extractDir is not a directory.");
        }

        extractDirFile = extractDir;
    }

    private final byte[] buffer = new byte[4096];

    @Override
    public void execute() {

        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(archiveFile))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                File ent = new File(extractDirFile, entry.getName());

                if (entry.isDirectory()) {
                    ent.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(new File(extractDirFile, entry.getName()))) {
                        long toRead = entry.getSize();
                        int readed = 0;
                        while (toRead > 0) {
                            readed = zip.read(buffer);
                            if (readed > 0) {
                                toRead -= readed;
                                fos.write(buffer, 0, readed);
                            }
                        }
                    }
                    zip.closeEntry();
                }
            }
        } catch (IOException ex) {
            logger.error("", ex);
        }
    }

}
