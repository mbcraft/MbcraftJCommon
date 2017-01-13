package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.ICommand;

import java.io.File;
import java.security.InvalidParameterException;

/**
 * Created by marco on 11/06/16.
 */
public class MarkDirScriptsAsExecutablesCommand implements ICommand {

    private final File myDir;

    public MarkDirScriptsAsExecutablesCommand(File dir) {
        if (dir==null || !dir.isDirectory()) throw new InvalidParameterException("The folder is null or is not a folder!");

        myDir = dir;
    }

    @Override
    public void execute() {
        File[] files = myDir.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".sh"))
                f.setExecutable(true);
        }
    }
}
