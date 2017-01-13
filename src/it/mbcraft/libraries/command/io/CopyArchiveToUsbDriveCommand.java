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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidParameterException;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@Polish("Is useful??")
@CodeClassification(CodeType.GENERIC)
public class CopyArchiveToUsbDriveCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(CopyArchiveToUsbDriveCommand.class);
    
    private final String myDrivePath, myFolderName, archiveSearchDirPath;

    public CopyArchiveToUsbDriveCommand(String archiveSearchDir, String drivePath, String folderName) {

        if (archiveSearchDir == null) {
            throw new InvalidParameterException("The path of the directory that contains the archive can't be null.");
        }
        File fDownload = new File(archiveSearchDir);
        if (!fDownload.isDirectory()) {
            throw new InvalidParameterException("The specified path is not a directory.");
        }
        archiveSearchDirPath = archiveSearchDir;

        if (drivePath == null) throw new InvalidParameterException("The drivePath can't be null!");
        myDrivePath = drivePath;
        if (folderName == null) throw new InvalidParameterException("The folder name can't be null!");
        myFolderName = folderName;
    }

    @Override
    public void execute() {

        File f = new File(archiveSearchDirPath);
        File availableFiles[] = f.listFiles();
        if (availableFiles.length != 1) {
            throw new IllegalStateException("Exactly one archive file is needed.");
        }

        File archive = availableFiles[0];

        File drive = new File(myDrivePath);
        File storeFolder = new File(drive, myFolderName);
        if (!storeFolder.exists())
            storeFolder.mkdir();
        try {
            Files.copy(archive.toPath(), storeFolder.toPath().resolve(archive.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            logger.error("", ex);
        }

    }

}
