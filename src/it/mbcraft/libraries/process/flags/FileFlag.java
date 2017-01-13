/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.libraries.process.flags;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public abstract class FileFlag implements IFlag {

    private static final Logger logger = LogManager.getLogger(FileFlag.class);
    protected static IFlag _instance = null;

    private final File flagFile;

    protected FileFlag(String flagPath) {
        flagFile = new File(flagPath);
    }

    @Override
    public boolean isSet() {
        return flagFile.exists() == true;
    }

    @Override
    public boolean isReset() {
        return flagFile.exists() == false;
    }

    @Override
    public void set() {
        try {
            flagFile.createNewFile();
        } catch (IOException ex) {
            logger.error("", ex);
        }
    }

    @Override
    public void reset() {
        flagFile.delete();
    }
}
