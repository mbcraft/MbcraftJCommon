package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.io.TryDeleteRootOwnedFileCommand;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class TestRootDeleteCommand {

    @Test
    public void testRootDelete() {
        File toDelete = new File("tests/data/test root delete/myfile.txt");
        try {
            toDelete.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertTrue("Il file da cancellare non esiste!!",toDelete.exists());

        TryDeleteRootOwnedFileCommand cmd = new TryDeleteRootOwnedFileCommand(toDelete);

        cmd.execute();

        Assert.assertFalse("Il file da cancellare non è stato cancellato!!",toDelete.exists());

    }
}
