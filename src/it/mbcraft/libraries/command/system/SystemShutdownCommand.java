package it.mbcraft.libraries.command.system;

import it.mbcraft.libraries.command.ICommand;
import it.mbcraft.libraries.command.io.RunAsUnixRootCommand;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class SystemShutdownCommand implements ICommand {

    @Override
    public void execute() {
        RunAsUnixRootCommand runAsRoot = new RunAsUnixRootCommand("shutdown -h now");
        runAsRoot.execute();
    }
}
