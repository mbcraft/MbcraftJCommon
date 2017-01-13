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

import java.io.File;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
@CodeClassification(CodeType.GENERIC)
public class DetectFileEncryptionAlgorithmCommand implements ICommand {

    private final File toLook;
    private boolean found;
    private CryptographicAlgorithm alg;

    public DetectFileEncryptionAlgorithmCommand(File toCheck) {
        toLook = toCheck;
    }

    public boolean hasFoundAlgorithm() {
        return found;
    }

    public CryptographicAlgorithm getCryptographicAlgorithm() {
        return alg;
    }

    @Override
    public void execute() {
        String filename = toLook.getName();
        if (filename.endsWith("sca0")) {
            found = true;
            alg = CryptographicAlgorithm.SCA0;
            return;
        }
    }

}
