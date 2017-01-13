/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.libraries.command.usbdrive;

import it.mbcraft.libraries.command.CodeClassification;
import it.mbcraft.libraries.command.CodeType;
import it.mbcraft.libraries.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class FindNewUsbDriveCommand implements ICommand {

    private Logger logger = LogManager.getLogger(FindNewUsbDriveCommand.class);

    private boolean hasFoundUsbDrive = false;
    private File usbDriveRoot = null;

    private static File[] previousCheckDevices = null;
    private static File[] devices = null;

    @Override
    public void execute() {

        UpdateUsbDrivesCommand updateUsbDrives = new UpdateUsbDrivesCommand();
        updateUsbDrives.execute();

        devices = updateUsbDrives.getDeviceRoots();

        boolean hasNewDevices = (previousCheckDevices==null && devices!=null && devices.length>0) || (previousCheckDevices!=null && devices!=null && previousCheckDevices.length<devices.length);

        if (hasNewDevices) {

            Set<File> devSet = new HashSet<>();
            devSet.addAll(Arrays.asList(devices));
            if (previousCheckDevices!=null)
                devSet.removeAll(Arrays.asList(previousCheckDevices));

            for (File f : devSet) {
                if (f.isDirectory() && f.canRead()) {
                    logger.info("New devices found!");
                    hasFoundUsbDrive = true;
                    usbDriveRoot = f;
                    break;
                }
            }
        }

        previousCheckDevices = devices;

    }

    public File getUsbDriveRoot() {
        return usbDriveRoot;
    }

    public boolean hasFoundUsbDrive() {
        return hasFoundUsbDrive;
    }

}