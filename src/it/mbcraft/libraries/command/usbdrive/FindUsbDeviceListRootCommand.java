package it.mbcraft.libraries.command.usbdrive;

import it.mbcraft.libraries.command.ICommand;
import it.mbcraft.libraries.utils.Polish;

import java.io.File;

/**
 * Created by marco on 11/05/16.
 */
@Polish("Add support for windows")
public class FindUsbDeviceListRootCommand implements ICommand {

    private File usbRoot = null;

    public File getUsbDeviceListRoot() {
        return usbRoot;
    }

    @Override
    public void execute() {

        usbRoot = new File("/var/run/usbmount/");

        File root = new File("/media/");

        String userName = System.getProperty("user.name");

        File userUsbRootDir = new File(root, userName);
        if (userUsbRootDir.exists() && userUsbRootDir.isDirectory() && root.listFiles().length==1)
            usbRoot = userUsbRootDir;

    }
}
