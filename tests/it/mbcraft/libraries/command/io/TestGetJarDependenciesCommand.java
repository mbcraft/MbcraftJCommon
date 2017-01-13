package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.io.GetJarDependenciesCommand;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class TestGetJarDependenciesCommand {

    @Test
    public void testJarDeps() {
        GetJarDependenciesCommand cmd = new GetJarDependenciesCommand(new File("tests/data/jar/RegiaPNPlayer.jar"));
        cmd.execute();

        Assert.assertNotNull("Impossibile leggere le librerie dal jar!!",cmd.getLibraries());
        Assert.assertTrue("Numero di librerie troppo basso!",cmd.getLibraries().length>5);
    }

}
