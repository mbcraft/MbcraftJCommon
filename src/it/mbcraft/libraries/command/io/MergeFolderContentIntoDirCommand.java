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
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidParameterException;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class MergeFolderContentIntoDirCommand implements ICommand, FileVisitor {

    private final File mySourceDir;
    private final File myDestDir;

    private static final Logger logger = LogManager.getLogger(MergeFolderContentIntoDirCommand.class.getName());

    public MergeFolderContentIntoDirCommand(File sourceDir, File destDir) {
        if (sourceDir == null) {
            throw new InvalidParameterException("The source directory can't be null.");
        }

        if (!sourceDir.isDirectory()) {
            throw new InvalidParameterException("The specified source dir is not a directory.");
        }
        mySourceDir = sourceDir;

        if (destDir == null) {
            throw new InvalidParameterException("The dest directory can't be null.");
        }

        if (!destDir.isDirectory()) {
            throw new InvalidParameterException("The specified dest dir is not a directory.");
        }
        myDestDir = destDir;
    }

    private Path destDirPath;
    private Path sourceDirPath;

    @Override
    public void execute() {
        try {

            destDirPath = myDestDir.getAbsoluteFile().toPath().normalize();
            sourceDirPath = mySourceDir.getAbsoluteFile().toPath();
            Files.walkFileTree(sourceDirPath, this);
        } catch (IOException ex) {
            logger.error("", ex);
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        Path d = (Path) dir;
        //create the destination directory
        Path destDir = destDirPath.resolve(sourceDirPath.relativize(d));

        destDir.toFile().mkdirs();

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        Path f = (Path) file;

        //logger.info("Source file : "+f.toString());

        Path destFile = destDirPath.resolve(sourceDirPath.relativize(f));

        //logger.info("Destination file : "+destFile.toString());

        Files.copy(f, destFile, StandardCopyOption.REPLACE_EXISTING);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

}
