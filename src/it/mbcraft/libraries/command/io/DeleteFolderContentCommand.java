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
public class DeleteFolderContentCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(DeleteFolderContentCommand.class);
    private final File myDirToEmpty;

    public DeleteFolderContentCommand(File dirToEmpty) {
        if (dirToEmpty == null) throw new InvalidParameterException("The directory to empty can't be null.");

        if (!dirToEmpty.isDirectory())
            throw new InvalidParameterException("The specified path is not a directory.");

        myDirToEmpty = dirToEmpty;
    }

    @Override
    public void execute() {
        try {
            Files.walkFileTree(myDirToEmpty.toPath(), new ContentDeleteVisitor(myDirToEmpty));
        } catch (IOException ex) {
            logger.error("", ex);
        }
    }


    private static class ContentDeleteVisitor implements FileVisitor {

        private final File myDirToEmpty;

        public ContentDeleteVisitor(File myDir) {
            myDirToEmpty = myDir;
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
            if (!f.equals(myDirToEmpty))
                f.delete();
            return FileVisitResult.CONTINUE;
        }
    }
}
