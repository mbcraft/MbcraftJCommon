package it.mbcraft.libraries.command.io;

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
 * Created by marco on 11/06/16.
 */
public class ListFilesCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(ListFileVisitor.class);

    private final Path myRoot;
    private final boolean recursiveWalking;
    private final IFilenameListener myListener;


    public ListFilesCommand(File dir, boolean recursive, IFilenameListener listener) {
        if (dir==null || !dir.isDirectory()) throw new InvalidParameterException("The root is null or is not a directory!");
        if (listener==null) throw new InvalidParameterException("The listener is null!");

        myRoot = dir.toPath();
        recursiveWalking = recursive;
        myListener = listener;
    }

    @Override
    public void execute() {
        try {
            Files.walkFileTree(myRoot,new ListFileVisitor(recursiveWalking,myListener));
        } catch (IOException e) {
            logger.catching(e);
        }
    }

    public static interface IFilenameListener {

        /**
         * Called before file listing starts.
         */
        void init();

        /**
         * Called when a file is found.
         *
         * @param f A file
         */
        void fileFound(Path f);

        /**
         * Called when a directory is found.
         *
         * @param d The directory
         */
        void directoryFound(Path d);
    }

    class ListFileVisitor implements FileVisitor {

        private final boolean recursiveWalking;
        private final IFilenameListener myListener;

        public ListFileVisitor(boolean recursive,IFilenameListener listener) {
            recursiveWalking = recursive;
            myListener = listener;
        }

        @Override
        public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
            if (recursiveWalking)
                return FileVisitResult.CONTINUE;
            else
                return FileVisitResult.SKIP_SUBTREE;
        }

        @Override
        public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
            Path p = (Path) file;
            if (p.toFile().isDirectory())
                myListener.directoryFound(p);
            if (p.toFile().isFile())
                myListener.fileFound(p);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
            Path p = (Path) file;
            if (p.toFile().isFile())
                logger.error("Error during listing, unable to visit file : "+p.toString(),exc);
            if (p.toFile().isDirectory())
                logger.error("Error during listing, unable to visit directory :"+p.toString(),exc);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
