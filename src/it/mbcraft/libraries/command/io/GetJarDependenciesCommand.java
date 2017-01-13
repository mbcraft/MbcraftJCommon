package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class GetJarDependenciesCommand implements ICommand {

    private Logger logger = LogManager.getLogger(GetJarDependenciesCommand.class);
    private final File myJarFile;

    private String[] libs = null;

    public GetJarDependenciesCommand(File jarFile) {
        if (jarFile==null || !jarFile.exists()) throw new InvalidParameterException("The jar file can't be null and must exist!");
        if (!jarFile.isFile()) throw new InvalidParameterException("The File object is not a file.");
        if (!jarFile.getName().endsWith("jar")) throw new InvalidParameterException("The file seems not to have jar extensione "+jarFile.getName());
        if (!jarFile.canRead()) throw new InvalidParameterException("The jar file must be readable!");

        myJarFile = jarFile;
    }

    @Override
    public void execute() {
        try {
            JarFile jf = new JarFile(myJarFile);
            Manifest mf = jf.getManifest();
            String manifestLibs = mf.getMainAttributes().getValue("Class-Path");
            if (manifestLibs==null) manifestLibs = mf.getAttributes("Class-Path").getValue("Class-Path");
            libs = manifestLibs.split(" ");
        } catch (IOException e) {
            logger.error("Unable to read jar file!",e);
        }
    }

    public String[] getLibraries() {
        return libs;
    }
}
