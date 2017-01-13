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

import java.io.File;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class UpdateUsbDrivesCommand implements ICommand {

    private static File myUsbDeviceListRoot = null;

    private int numDevices = Integer.MAX_VALUE;
    private File[] deviceList = null;

    public File getUsbDeviceListRoot() {
        return myUsbDeviceListRoot;
    }

    public File[] getDeviceRoots() {
        return deviceList;
    }

    public int getDeviceCount() {
        return numDevices;
    }

    public UpdateUsbDrivesCommand() {
        //uses already set device root
    }

    public UpdateUsbDrivesCommand(File usbRootDir) {
        myUsbDeviceListRoot = usbRootDir;
    }

    @Override
    public void execute() {
        if (myUsbDeviceListRoot == null) throw new IllegalStateException("Usb root not set.");

        if (!myUsbDeviceListRoot.exists()) {
            numDevices = 0;
            deviceList = new File[numDevices];
            return;
        }

        int devCount = myUsbDeviceListRoot.listFiles().length;
        if (numDevices != devCount) {
            numDevices = devCount;
            deviceList = myUsbDeviceListRoot.listFiles();
        }
    }

}
