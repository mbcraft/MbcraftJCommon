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
import it.mbcraft.libraries.utils.Polish;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@Polish("...")
@CodeClassification(CodeType.GENERIC)
public class CreateArchiveFromFolderCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(CreateArchiveFromFolderCommand.class.getName());

    private final String saveFilePath;
    private final String sourceDirPath;

    public CreateArchiveFromFolderCommand(String savePath, String sourcePath) {
        if (savePath == null) throw new InvalidParameterException("The name of the archive can't be null.");
        saveFilePath = savePath;

        if (sourcePath == null)
            throw new InvalidParameterException("The path of the directory to archive can't be null.");
        File f = new File(sourcePath);
        if (!f.isDirectory())
            throw new InvalidParameterException("The specified path is not a directory.");
        sourceDirPath = sourcePath;
    }

    @Override
    public void execute() {
        try {
            try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(saveFilePath))) {
                File archiveFilesDir = new File(sourceDirPath);
                addFilesToArchive(zip, "/", archiveFilesDir.listFiles());
            }
        } catch (IOException ex) {
            logger.error("", ex);
        }
    }

    private final byte[] buffer = new byte[4096];

    private void addFilesToArchive(ZipOutputStream zip, String archivePath, File[] dirFiles) throws IOException {
        for (File f : dirFiles) {
            ZipEntry e;

            if (f.isDirectory()) {
                e = new ZipEntry(archivePath + f.getName() + "/");
                e.setSize(0);
                logger.info("Adding entry : " + e.getName());
                e.setTime(f.lastModified());
                zip.putNextEntry(e);
                zip.closeEntry();
                addFilesToArchive(zip, archivePath + f.getName() + "/", f.listFiles());
            } else {
                e = new ZipEntry(archivePath + f.getName());
                logger.info("Adding entry : " + e.getName());
                e.setSize(f.length());
                e.setTime(f.lastModified());
                zip.putNextEntry(e);
                long toRead = f.length();
                try (FileInputStream fis = new FileInputStream(f)) {
                    while (toRead > 0) {
                        int readed = fis.read(buffer);
                        if (readed > 0) {
                            toRead -= readed;
                            zip.write(buffer, 0, readed);
                        }
                    }
                }

                zip.closeEntry();
            }
        }
    }
}