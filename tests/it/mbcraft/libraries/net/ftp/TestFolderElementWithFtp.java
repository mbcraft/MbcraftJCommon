package it.mbcraft.libraries.net.ftp;

import it.sauronsoftware.ftp4j.FTPFile;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 08/07/16.
 */
public class TestFolderElementWithFtp {

    public static final String FTP_TEST_USERNAME = "ftp_testing";
    public static final String FTP_TEST_PASSWORD = "test1234";
    public static final String FTP_TEST_REMOTE_HOST = "localhost";
    public static final String FTP_TEST_REMOTE_DIR = "/files/download/";

    /*
    public static final String FTP_TEST_USERNAME = "ftpsecure";
    public static final String FTP_TEST_PASSWORD = "KXZ99457_P$";
    public static final String FTP_TEST_REMOTE_HOST = "radioandbusiness.com";
    public static final String FTP_TEST_REMOTE_DIR = "/radioandbusiness.com/files/download/";
    */

    @Test
    public void testFolderElementOnFtp() throws IOException {

        File testDist = new File("tests/data/test_dist/");
        File saveDir = new File("tests/data/download_with_tips");

        Assert.assertFalse("La directory in cui salvare il download esiste già!",saveDir.exists());

        saveDir.mkdir();

        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);

        ftp.login();

        String remotePath = FTP_TEST_REMOTE_DIR+"software/MBCRAFT/RegiaPN_Tools/dist/";
        FolderElement el = FolderElement.createFromLocalDir(null,testDist);
        FTPFile dirInfo = ftp.getFileInfo(remotePath,true);
        FolderElement remoteTips = FolderElement.createFromFTPFile(null,ftp,remotePath,dirInfo);
        remoteTips.markIfNewOrModified(el);
        remoteTips.removeUnmodifiedElements();

        saveDir.mkdir();

        ftp.downloadFolderWithTips(remoteTips,remotePath,saveDir);

        Assert.assertEquals("Sono stati scaricati dei files per l'aggiornamento!!",saveDir.listFiles().length,0);

        Files.delete(saveDir.toPath());

        ftp.logout();
    }
}
