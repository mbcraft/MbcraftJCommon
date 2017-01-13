package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.io.CleanupUnusedJarLibrariesCommand;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 07/07/16.
 */
public class TestCleanupUnusedJarDependencies {

    private final File testAllLibs = new File("tests/data/jar/all_libs/");
    private final File destLib = new File("tests/data/jar/lib/");

    @Test
    public void testCleanUnusedJarDependencies() throws IOException {
        try {
            cleanupLibDir();
            copyAllLibraries();

            createFakeOldLibraries();

            CleanupUnusedJarLibrariesCommand cmd = new CleanupUnusedJarLibrariesCommand(new File("tests/data/jar/RegiaPNPlayer.jar"), new File("tests/data/jar/lib/"),false);
            cmd.execute();

            testLibrariesDeleted();
        } finally {
            cleanupLibDir();
        }

    }

    private void cleanupLibDir() {
        //cleanup
        for (File f : destLib.listFiles()) f.delete();
    }

    private void copyAllLibraries() throws IOException {
        for (File f : testAllLibs.listFiles()) {
            if (f.isFile()) {
                Files.copy(f.toPath(), new File(destLib, f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void createFakeOldLibraries() throws IOException {
        File realJCommon = new File(destLib, "MbcraftJCommon-1.3.7.jar");
        Assert.assertTrue("The jcommon does not exists!", realJCommon.exists());
        File oldJCommon = new File(destLib, "MbcraftJCommon-1.3.0.jar");
        Assert.assertFalse("The old jcommon already exists!", oldJCommon.exists());

        Files.copy(realJCommon.toPath(), oldJCommon.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Assert.assertTrue("The old jcommon was not created!", oldJCommon.exists());

        File realhttpcore = new File(destLib, "httpcore-4.4.3.jar");
        Assert.assertTrue("The httpcore does not exists!", realhttpcore.exists());
        File oldHttpcore = new File(destLib, "httpcore-3.5.jar");
        Assert.assertFalse("The old httpcore already exists!", oldHttpcore.exists());

        Files.copy(realhttpcore.toPath(), oldHttpcore.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Assert.assertTrue("The old httpcore was not created!", oldHttpcore.exists());
    }

    private void testLibrariesDeleted() {
        File realJCommon = new File(destLib, "MbcraftJCommon-1.3.7.jar");
        Assert.assertTrue("The jcommon was deleted!!!", realJCommon.exists());
        File oldJCommon = new File(destLib, "MbcraftJCommon-1.3.0.jar");
        Assert.assertFalse("The old jcommon was not deleted!!!", oldJCommon.exists());

        File realhttpcore = new File(destLib, "httpcore-4.4.3.jar");
        Assert.assertTrue("The httpcore was deleted!!!", realhttpcore.exists());
        File oldHttpcore = new File(destLib, "httpcore-3.5.jar");
        Assert.assertFalse("The old httpcore was not deleted!!!", oldHttpcore.exists());
    }
}
