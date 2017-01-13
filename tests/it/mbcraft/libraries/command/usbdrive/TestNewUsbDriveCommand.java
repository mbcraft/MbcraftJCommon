package it.mbcraft.libraries.command.usbdrive;

import junit.framework.Assert;
import org.junit.Test;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 09/07/16.
 */
public class TestNewUsbDriveCommand {

    @Test
    public void testNewUsbDriveCommand() throws InterruptedException {
        FindUsbDeviceListRootCommand findRoot = new FindUsbDeviceListRootCommand();
        findRoot.execute();

        UpdateUsbDrivesCommand update1 = new UpdateUsbDrivesCommand(findRoot.getUsbDeviceListRoot());
        update1.execute();

        System.out.println("Insert usb drive ...");
        Thread.sleep(5000);

        FindNewUsbDriveCommand find1 = new FindNewUsbDriveCommand();
        find1.execute();
        Assert.assertTrue(find1.hasFoundUsbDrive());

        FindNewUsbDriveCommand find2 = new FindNewUsbDriveCommand();
        find2.execute();
        Assert.assertFalse(find2.hasFoundUsbDrive());

        FindNewUsbDriveCommand find3 = new FindNewUsbDriveCommand();
        find3.execute();
        Assert.assertFalse(find3.hasFoundUsbDrive());

        FindNewUsbDriveCommand find4 = new FindNewUsbDriveCommand();
        find4.execute();
        Assert.assertFalse(find4.hasFoundUsbDrive());

    }
}
