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

import it.mbcraft.libraries.command.CodeClassification;
import it.mbcraft.libraries.command.CodeType;
import it.mbcraft.libraries.command.ICommand;
import it.mbcraft.libraries.encryption.CryptographicAlgorithm;
import it.mbcraft.libraries.encryption.sca0.SCA0Decrypter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;


/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class MemoryFileDecrypterCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(MemoryFileDecrypterCommand.class);

    private final File target;
    private byte[] decodedBuffer = null;
    private final CryptographicAlgorithm algorithm;

    public MemoryFileDecrypterCommand(File toDecrypt, CryptographicAlgorithm alg) {
        target = toDecrypt;
        algorithm = alg;
    }

    public byte[] getMemoryBuffer() {
        return decodedBuffer;
    }

    @Override
    public void execute() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            FileInputStream fis = new FileInputStream(target);
            switch (algorithm) {
                case SCA0:
                    new SCA0Decrypter(fis, bos).decrypt();
                    break;
                default:
                    throw new InvalidParameterException("Unsupported cryptographic algorithm.");
            }
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
        }
        decodedBuffer = bos.toByteArray();
    }

}
