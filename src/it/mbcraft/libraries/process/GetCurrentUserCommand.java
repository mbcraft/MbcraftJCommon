package it.mbcraft.libraries.process;

import it.mbcraft.libraries.command.ICommand;
import it.mbcraft.libraries.utils.OSChecker;

/**
 * Created by marco on 10/06/16.
 */
public class GetCurrentUserCommand implements ICommand {

    private String user = null;

    @Override
    public void execute() {

        OSChecker.ensureNotWindows();

        user = System.getProperty("user.name");
    }

    public String getUser() {
        return user;
    }

    public boolean isRoot() {
        return "root".equals(user);
    }
}
