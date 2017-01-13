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
import it.mbcraft.libraries.encryption.IEncrypter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * SCA-0 : Header : 4 bytes "SCA0" Key length : 2 bytes (unsigned integer) = x,
 * constraints : greater than 1024, less then 65535 x bytes : unsigned integers
 * (actual 'key' data) Crypted stream data follows.
 * <p>
 * During encryption the integer value is added to the corresponding round robin
 * value, mod 256, and then written to the stream. During decryption the integer
 * value is subtracted from the corresponding round robin value, mod 256 and
 * then readed from the stream.
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class SCA0Encrypter implements IEncrypter {

    private static final Logger logger = LogManager.getLogger(SCA0Encrypter.class);
    private final InputStream sourceStream;
    private final OutputStream destStream;

    /*
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("2 arguments needed : input file and output file.");
        }
        System.out.println("INPUT FILE : " + args[0]);
        System.out.println("OUTPUT FILE : " + args[1]);
        File input = new File(args[0]);
        File output = new File(args[1]);
        SCA0Encrypter dec = new SCA0Encrypter(new FileInputStream(input), new FileOutputStream(output));
        dec.encrypt();
        System.out.println("ENCRYPT DONE.");
    }
    */

    public SCA0Encrypter(InputStream source, OutputStream dest) {
        sourceStream = new BufferedInputStream(source, 4096 * 100);
        destStream = new BufferedOutputStream(dest, 4096 * 100);
    }

    @Override
    public void encrypt() {
        try {
            int[] keyBytes = SCA0Utils.generateRandomKeyLengthBytes();

            destStream.write(SCA0Utils.HEADER);
            destStream.write(keyBytes[0]);
            destStream.write(keyBytes[1]);

            int keyLength = keyBytes[0] * 256 + keyBytes[1];

            int[] key = new int[keyLength];

            for (int i = 0; i < keyLength; i++) {
                key[i] = SCA0Utils.generateRandomKeyByte();
                destStream.write(key[i]);
            }

            int ki = 0;
            int val;
            byte b;
            byte[] bufferIn = new byte[4096 * 100];
            byte[] bufferOut = new byte[4096 * 100];
            int readed = -1;

            while ((readed = sourceStream.read(bufferIn)) != -1) {
                for (int i = 0; i < readed; i++) {
                    val = bufferIn[i];
                    val = val + 256 + key[ki++];
                    bufferOut[i] = (byte) val;
                    ki %= keyLength;
                }
                destStream.write(bufferOut, 0, readed);
            }
            destStream.flush();
            sourceStream.close();
            destStream.close();
        } catch (IOException ex) {
            logger.error("Error during encoding.", ex);
        }
    }

    @Override
    public CryptographicAlgorithm getCryprographicAlgorithm() {
        return CryptographicAlgorithm.SCA0;
    }
}
