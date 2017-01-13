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
import it.mbcraft.libraries.encryption.sca0.SCA0Encrypter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.InvalidParameterException;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class MemoryFileEncrypterCommand implements ICommand {

    private static final Logger logger = LogManager.getLogger(MemoryFileEncrypterCommand.class);

    private final byte[] input;
    private final File target;
    private final CryptographicAlgorithm algorithm;

    public MemoryFileEncrypterCommand(byte[] inputBuffer, File toEncrypt, CryptographicAlgorithm alg) {
        input = inputBuffer;
        target = toEncrypt;
        algorithm = alg;
    }

    @Override
    public void execute() {
        ByteArrayInputStream bis = new ByteArrayInputStream(input);
        try {
            FileOutputStream fos = new FileOutputStream(target);
            switch (algorithm) {
                case SCA0:
                    new SCA0Encrypter(bis, fos).encrypt();
                    break;
                default:
                    throw new InvalidParameterException("Unsupported cryptographic algorithm.");
            }
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
        }
    }
}
