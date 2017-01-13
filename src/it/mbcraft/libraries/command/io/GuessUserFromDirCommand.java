package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.ICommand;
import it.mbcraft.libraries.utils.OSChecker;

import java.io.File;

/**
 * Created by marco on 10/06/16.
 */
public class GuessUserFromDirCommand implements ICommand {

    private String user = null;

    private final File myPath;

    public GuessUserFromDirCommand() {
        myPath = new File(".");
    }

    public GuessUserFromDirCommand(File path) {
        myPath = path;
    }

    public boolean userFound() {
        return user!=null;
    }

    public String getUser() {
        return user;
    }

    @Override
    public void execute() {

        OSChecker.ensureNotWindows();

        String absolutePath = myPath.getAbsolutePath();

        if (absolutePath.startsWith("/root")) user = "root";
        else {
            String tokens[] = absolutePath.split("/");
            int index = 0;
            for (String tk : tokens) {
                if (tk.equals("home") && tokens.length>index+1) {
                    user = tokens[index+1];
                    break;
                }
                index++;
            }
        }

    }
}
