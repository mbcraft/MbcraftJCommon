package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.encryption.sca0.SCA0Decrypter;
import it.mbcraft.libraries.encryption.sca0.SCA0Encrypter;
import junit.framework.Assert;
import org.apache.logging.log4j.message.Message;
import org.junit.Test;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 09/07/16.
 */
public class TestEncryptionDecryption {

    @Test
    public void testEncryptionDecryptionSong01() throws IOException, NoSuchAlgorithmException {
        File src = new File("tests/data/encryption/src/08_tea_house_moon.mp3");
        doEncryptionDecryptionTest(src);
    }


    @Test
    public void testEncryptionDecryptionSong02() throws IOException, NoSuchAlgorithmException {
        File src = new File("tests/data/encryption/src/21___Lament_of_the_Highborne.mp3");
        doEncryptionDecryptionTest(src);
    }

    @Test
    public void testEncryptionDecryptionSong03() throws IOException, NoSuchAlgorithmException {
        File src = new File("tests/data/encryption/src/stage3-i486-20140930.tar.bz2");
        doEncryptionDecryptionTest(src);
    }

    private void parseFileForDigests(MessageDigest md5,MessageDigest sha1,FileInputStream fis) throws IOException {
        byte buffer[] = new byte[10000];
        int readed;
        while ((readed=fis.read(buffer))!=-1) {
            md5.update(buffer,0,readed);
            sha1.update(buffer,0,readed);
        }
        fis.close();
    }

    private void doEncryptionDecryptionTest(File toTest) throws NoSuchAlgorithmException, IOException {

        assertTrue("Il file dati sorgente non esiste!!",toTest.exists());

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        FileInputStream fis = new FileInputStream(toTest);
        parseFileForDigests(md5,sha1,fis);
        String md5StringOrig = md5.toString();
        String sha1StringOrig = sha1.toString();

        File encDest = new File("tests/data/encryption/result/dest_data.mp3.sca0");
        assertFalse("Il file crittato esiste già!",encDest.exists());

        SCA0Encrypter enc = new SCA0Encrypter(new FileInputStream(toTest),new FileOutputStream(encDest));
        enc.encrypt();

        File afterDecSrc = new File("tests/data/encryption/result/dec_data.mp3");
        assertFalse("Il file decrittato esiste già!",afterDecSrc.exists());

        SCA0Decrypter dec = new SCA0Decrypter(new FileInputStream(encDest),new FileOutputStream(afterDecSrc));
        dec.decrypt();

        assertEquals("La lunghezza del file decrittato non esiste!!",afterDecSrc.length(),toTest.length());

        MessageDigest md5After = MessageDigest.getInstance("MD5");
        MessageDigest sha1After = MessageDigest.getInstance("SHA-1");
        FileInputStream fisAfter = new FileInputStream(afterDecSrc);
        parseFileForDigests(md5After,sha1After,fisAfter);
        String md5StringFinal = md5.toString();
        String sha1StringFinal = sha1.toString();

        assertEquals("Il digest MD5 del file non corrisponde!!",md5StringOrig,md5StringFinal);
        assertEquals("Il digest SHA-1 del file non corrisponde!!",sha1StringOrig,sha1StringFinal);

        encDest.delete();
        afterDecSrc.delete();
    }

}
