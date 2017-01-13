/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */
package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.ICommand;
import it.mbcraft.libraries.encryption.CryptographicAlgorithm;
import it.mbcraft.libraries.encryption.IEncrypter;
import it.mbcraft.libraries.encryption.sca0.SCA0Encrypter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class EncryptFileCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(EncryptFileCommand.class);
    private final File in;
    private final File out;
    private final CryptographicAlgorithm algorithm;

    private FileInputStream fis = null;
    private FileOutputStream fos = null;

    public EncryptFileCommand(File source, File dest, CryptographicAlgorithm alg, Properties pt) {
        in = source;
        out = dest;
        algorithm = alg;
        Properties params = pt;
    }

    @Override
    public void execute() {

        try {
            fis = new FileInputStream(in);
            fos = new FileOutputStream(out);
            IEncrypter enc = null;
            switch (algorithm) {
                case SCA0: {
                    enc = new SCA0Encrypter(fis, fos);
                    enc.encrypt();
                    break;
                }
                default:
                    throw new IllegalStateException("Cryptographic algorithm not found.");
            }

        } catch (FileNotFoundException ex) {
            logger.error("", ex);
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException ex) {
                logger.error("", ex);
            }
        }

    }

}
