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

import java.io.IOException;
import java.util.Random;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class SCA0Utils {
    public static final int MIN_KEY_LENGTH = 1024;
    public static final int MAX_KEY_LENGTH = 32767;

    public static final byte[] HEADER = {83, 67, 65, 48};  //SCA0

    private static final Random rnd = new Random();

    public static void checkHeaderBytes(byte[] data) throws IOException {
        if (data.length != HEADER.length) throw new IOException("Header length is wrong.");
        for (int i = 0; i < HEADER.length; i++) {
            if (data[i] != HEADER[i]) throw new IOException("Header data invalid.");
        }
    }

    public static final int[] generateRandomKeyLengthBytes() {
        int key[] = new int[2];

        boolean validKey = false;
        while (!validKey) {
            key[0] = rnd.nextInt(256);
            key[1] = rnd.nextInt(256);

            if (key[0] > 4 && key[1] > 0) validKey = true;
        }

        return key;
    }

    public static final int generateRandomKeyByte() {
        return rnd.nextInt(256);
    }
}
