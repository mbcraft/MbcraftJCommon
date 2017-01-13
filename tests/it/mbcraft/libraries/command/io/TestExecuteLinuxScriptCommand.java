package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.io.ExecuteLinuxScriptCommand;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class TestExecuteLinuxScriptCommand {

    @Test
    public void testExecutionPath() {

        File myScript = new File("tests/data/script/myscript.sh");

        Assert.assertTrue("Lo script non esiste!!",myScript.exists());

        ExecuteLinuxScriptCommand ex = new ExecuteLinuxScriptCommand(myScript);
        Assert.assertEquals("The command path is not valid!","./tests/data/script/myscript.sh",ex.getLaunchCommand());

    }
}
