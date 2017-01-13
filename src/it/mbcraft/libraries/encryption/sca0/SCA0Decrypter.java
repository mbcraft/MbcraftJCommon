/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.libraries.encryption.sca0;

import it.mbcraft.libraries.encryption.CryptographicAlgorithm;
import it.mbcraft.libraries.encryption.IDecrypter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class SCA0Decrypter implements IDecrypter {

    private static final Logger logger = LogManager.getLogger(SCA0Decrypter.class);
    private final InputStream sourceStream;
    private final OutputStream destStream;
    
    /*
    public static void main(String[] args) throws Exception {
        if (args.length!=2)
            throw new Exception("2 arguments needed : input file and output file.");
        System.out.println("INPUT FILE : "+args[0]);
        System.out.println("OUTPUT FILE : "+args[1]);
        File input = new File(args[0]);
        File output = new File(args[1]);
        SCA0Decrypter dec = new SCA0Decrypter(new FileInputStream(input),new FileOutputStream(output));
        dec.decrypt();
        System.out.println("DECRYPT DONE.");
    }
    */

    public SCA0Decrypter(InputStream source, OutputStream dest) {
        sourceStream = new BufferedInputStream(source, 4096 * 100);
        destStream = new BufferedOutputStream(dest, 4096 * 100);
    }

    @Override
    public void decrypt() {
        try {
            byte[] headerBytes = new byte[4];
            sourceStream.read(headerBytes);

            SCA0Utils.checkHeaderBytes(headerBytes);

            int key0 = sourceStream.read();
            int key1 = sourceStream.read();

            int keyLength = key0 * 256 + key1;

            int[] key = new int[keyLength];

            for (int i = 0; i < keyLength; i++) {
                key[i] = sourceStream.read();
            }

            int readed = -1;
            byte b;
            int val;
            byte[] bufferIn = new byte[4096 * 100];
            byte[] bufferOut = new byte[4096 * 100];
            int ki = 0;

            while ((readed = sourceStream.read(bufferIn)) != -1) {
                for (int i = 0; i < readed; i++) {
                    val = bufferIn[i];
                    val = val + 256 - key[ki++];
                    bufferOut[i] = (byte) val;
                    ki %= keyLength;
                }

                destStream.write(bufferOut, 0, readed);
            }
            destStream.flush();
            sourceStream.close();
            destStream.close();
        } catch (IOException ex) {
            logger.error("Error during decode.", ex);
        }
    }

    @Override
    public CryptographicAlgorithm getCryprographicAlgorithm() {
        return CryptographicAlgorithm.SCA0;
    }
}
