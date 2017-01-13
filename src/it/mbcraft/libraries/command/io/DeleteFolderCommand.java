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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidParameterException;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class DeleteFolderCommand implements ICommand, FileVisitor {

    private static final Logger logger = LogManager.getLogger(DeleteFolderCommand.class);
    private final File myDirToDelete;

    public DeleteFolderCommand(File dirToDelete) {
        if (dirToDelete == null) throw new InvalidParameterException("The directory to delete can't be null.");

        if (dirToDelete.exists() && !dirToDelete.isDirectory())
            throw new InvalidParameterException("The specified file is not a directory.");

        myDirToDelete = dirToDelete;
    }

    @Override
    public void execute() {
        try {
            Files.walkFileTree(myDirToDelete.toPath(), this);
        } catch (IOException ex) {
            logger.error("", ex);
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        File f = ((Path) file).toFile();
        f.delete();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        return FileVisitResult.TERMINATE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        File f = ((Path) dir).toFile();
        f.delete();
        return FileVisitResult.CONTINUE;
    }

}