package it.mbcraft.libraries.net.ftp;


import it.sauronsoftware.ftp4j.FTPFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * Created by marco on 20/05/16.
 */

public class TestEasyFtp {

    public static final String FTP_TEST_USERNAME = "ftp_testing";
    public static final String FTP_TEST_PASSWORD = "test1234";
    public static final String FTP_TEST_REMOTE_HOST = "localhost";
    public static final String FTP_TEST_REMOTE_DIR = "/test/env/MbcraftJCommon/";

    @Test
    public void testFtpLoginLogout() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());
        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testFtpListFiles() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());
        FTPFile[] fileList = ftp.listFiles(FTP_TEST_REMOTE_DIR+"list_files/");
        Set<String> results = new HashSet<>();
        for (int i=0;i<fileList.length;i++) {
            results.add("/"+fileList[i].getName());
        }
        assertTrue(results.contains("/file_01.txt"));
        assertTrue(results.contains("/file_02.txt"));
        assertTrue(results.contains("/other_dir"));
        assertEquals("Il numero di file nella cartella non è 3!!",3,fileList.length);
        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testFtpUploadFilesDeleteFiles() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);

        assertTrue("Il login non è andato a buon fine!!",ftp.login());
        FTPFile[] files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_file/");
        assertEquals("Il numero di files non corrisponde!!",0,files.length);
        File localFile = new File("tests/data/a_simple_file_to_upload.txt");
        assertTrue("Il file locale non esiste!!",localFile.exists());
        boolean uploadResult = ftp.uploadFile(localFile,FTP_TEST_REMOTE_DIR+"upload_file/a_simple_file.txt");
        assertTrue("L'upload non è avvenuto con successo!",uploadResult);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_file/");
        assertEquals("Il file non è stato caricato con successo!!",1,files.length);
        boolean deleteResult = ftp.deleteFile(FTP_TEST_REMOTE_DIR+"upload_file/a_simple_file.txt");
        assertTrue("L'eliminazione non è avvenuta con successo!!",deleteResult);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_file/");
        assertEquals("Il numero di files non corrisponde!!",0,files.length);
        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testFtpUploadDeleteFolder() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());

        FTPFile[] files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/");
        assertEquals("Il numero di files non corrisponde!!",0,files.length);
        File upDir = new File("tests/data/a_dir_with_content/");
        boolean uploadResult = ftp.uploadFolder(upDir,FTP_TEST_REMOTE_DIR+"upload_dir/an_uploaded_dir/");
        assertTrue("L'upload non è avvenuto con successo!",uploadResult);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/");
        assertEquals("Il file non è stato caricato con successo!!",1,files.length);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/an_uploaded_dir/");
        assertEquals("Il contenuto della cartella non corrisponde!!",4,files.length);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/an_uploaded_dir/folder_1/");
        assertEquals("Il contenuto della cartella folder_1 non corrisponde!!",1,files.length);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/an_uploaded_dir/folder_1/folder_2/");
        assertEquals("Il contenuto della cartella folder_2 non corrisponde!!",1,files.length);
        FTPFile info = ftp.getFileInfo(FTP_TEST_REMOTE_DIR+"upload_dir/an_uploaded_dir/folder_1/folder_2/text_file_inside.txt",false);
        assertEquals("La dimensione del file annidato non corrisponde!",16,info.getSize());
        boolean deleteResult = ftp.deleteFolder(FTP_TEST_REMOTE_DIR+"upload_dir/an_uploaded_dir/");
        assertTrue("L'eliminazione non è avvenuta con successo!!",deleteResult);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/");
        assertEquals("Il numero di files non corrisponde!!",0,files.length);
        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());

    }

    @Test
    public void testFtpDownloadFile() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());
        File f = new File("tests/data/download/sample_file.txt");
        boolean downloadResult = ftp.downloadFile("/test/env/MbcraftJCommon/download_file/sample_file.txt",f);
        assertTrue("Il download non è andato a buon fine!!",downloadResult);
        assertTrue("Il file non esiste!!",f.exists());
        assertEquals("La dimensione del file non corrisponde!!",13,f.length());
        f.delete();
        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testFtpDownloadFolder() throws IOException {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());
        File f = new File("tests/data/download/sample_folder");
        boolean downloadResult = ftp.downloadFolder("/test/env/MbcraftJCommon/download_dir/",f);
        assertTrue("Il download non è andato a buon fine!!",downloadResult);
        assertTrue("La cartella non esiste!!",f.exists());

        File f1 = new File("tests/data/download/sample_folder/a_file.txt");
        assertTrue("Il file 1 non è stato scaricato!",f1.exists());
        assertEquals("La dimensione del file 1 scaricato non corrisponde!",9,f1.length());

        File f2 = new File("tests/data/download/sample_folder/more_content/some_content.txt");
        assertTrue("Il file 2 non è stato scaricato!",f2.exists());
        assertEquals("La dimensione del file 2 scaricato non corrisponde!",7,f2.length());

        deleteDirectory(f);
        assertFalse("La cartella dei file scaricati non è stata eliminata!!",f.exists());
        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testFtpDownloadFolderWithTips() throws IOException {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());

        FolderElement tips = new FolderElement(null,"tips");
        FolderElement download_dir = new FolderElement(tips,"download_dir");
        tips.add(download_dir);
        FolderElement more_content = new FolderElement(download_dir,"more_content");
        download_dir.add(more_content);
        more_content.add(new FileElement(more_content,"some_content.txt",7));
        download_dir.add(new FileElement(download_dir,"a_file.txt",9));

        File f = new File("tests/data/download/download_dir/");
        assertFalse("La cartella da scaricare esiste già!!",f.exists());

        boolean result = ftp.downloadFolderWithTips(tips,"/test/env/MbcraftJCommon/",new File("tests/data/download/"));
        assertTrue("Impossibile effettuare il download della cartella con i tips!!",result);


        assertTrue("La cartella download_dir non è stata scaricata!!",f.exists());
        assertEquals("La cartella download_dir contiene un numero di elementi errato!!",2,f.listFiles().length);
        File f1 = new File(f,"a_file.txt");
        assertTrue("Il file download_dir/a_file.txt non è stato scaricato!!",f1.exists());
        File f2 = new File(f,"more_content");
        assertTrue("La cartella download_dir/more_content/ non è stata scaricata!!",f2.exists());
        File f3 = new File(f2,"some_content.txt");
        assertTrue("Il file download_dir/more_content/some_content.txt non è stato scaricato!!",f3.exists());

        deleteDirectory(f);

        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testFtpUploadFolderWithTips() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());
        FTPFile[] files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/");
        assertEquals("Il numero di files non corrisponde!!",0,files.length);
        File upDir = new File("tests/data/a_dir_with_content/");
        FolderElement el = new FolderElement(null,"a_dir_with_content");
        el.add(new FileElement(el,"last_file.txt",9));
        FolderElement f1 = new FolderElement(el,"folder_1");
        el.add(f1);
        f1.add(new FolderElement(f1,"folder_2"));

        boolean uploadResult = ftp.uploadFolderWithTips(el,upDir,FTP_TEST_REMOTE_DIR+"upload_dir/upload_with_tips/");
        assertTrue("L'upload non è avvenuto con successo!",uploadResult);

        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/upload_with_tips/");
        assertEquals("Il file non è stato caricato con successo!!",2,files.length);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/upload_with_tips/folder_1/");
        assertEquals("Il contenuto della cartella non corrisponde!!",1,files.length);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/upload_with_tips/folder_1/folder_2/");
        assertEquals("Il contenuto della cartella non corrisponde!!",0,files.length);

        boolean deleteResult = ftp.deleteFolder(FTP_TEST_REMOTE_DIR+"upload_dir/upload_with_tips/");
        assertTrue("L'eliminazione non è avvenuta con successo!!",deleteResult);
        files = ftp.listFiles(FTP_TEST_REMOTE_DIR+"upload_dir/");
        assertEquals("Il numero di files non corrisponde!!",0,files.length);
        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testFtpFileInfo() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());

        FTPFile ff = ftp.getFileInfo("/test/env/MbcraftJCommon",false);
        assertTrue("Il file remoto non è una cartella!",ff.getType() == FTPFile.TYPE_DIRECTORY);
        assertEquals("Il nome del file remoto non corrisponde!!","/test/env/MbcraftJCommon",ff.getName());

        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testCalculateFTPTotalSize() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        long result = ftp.calculateTotalSize(new File("tests/data/a_dir_with_content/"));
        assertEquals("La dimensione non coincide con quella ritornata da du.",12317,result);
    }

    @Test
    public void testLocalFileTree() {
        FolderElement el = FolderElement.createFromLocalDir(null,new File("tests/data/"));
        assertEquals("Il numero degli elementi non corrisponde!!",10,el.getChildCount());
    }

    @Test
    public void testDownloadBigFile() {

        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());
        File f = new File("tests/data/download/player__20160707.zip");
        boolean downloadResult = ftp.downloadFile("/test/env/MbcraftJCommon/big_file/player__20160707.zip",f);
        assertTrue("Il download non è andato a buon fine!!",downloadResult);
        assertTrue("Il file non esiste!!",f.exists());
        assertEquals("La dimensione del file non corrisponde!!",7546419,f.length());
        f.delete();
        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());

    }

    @Test
    public void testDoubleCreateFolderReturnsTrue() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());

        boolean createResult1 = ftp.createFolder(FTP_TEST_REMOTE_DIR+"upload_dir/create_test/");
        assertTrue("La creazione della cartella non è avvenuto con successo!",createResult1);
        boolean createResult2 = ftp.createFolder(FTP_TEST_REMOTE_DIR+"upload_dir/create_test/");
        assertTrue("La creazione della cartella non è avvenuto con successo!",createResult2);

        boolean deleteResult = ftp.deleteFolder(FTP_TEST_REMOTE_DIR+"upload_dir/create_test/");
        assertTrue("L'eliminazione non è avvenuta con successo!!",deleteResult);

        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testDoubleWriteFileReturnsTrue() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());

        File f = new File("tests/data/a_simple_file_to_upload.txt");

        boolean uploadResult1 = ftp.uploadFile(f,FTP_TEST_REMOTE_DIR+"upload_file/upload.dat");
        assertTrue("La creazione della cartella non è avvenuto con successo!",uploadResult1);
        boolean uploadResult2 = ftp.uploadFile(f,FTP_TEST_REMOTE_DIR+"upload_file/upload.dat");
        assertTrue("La creazione della cartella non è avvenuto con successo!",uploadResult2);

        FTPFile info = ftp.getFileInfo(FTP_TEST_REMOTE_DIR+"upload_file/upload.dat",false);
        assertEquals("Le dimensioni dei file non coincidono!!",info.getSize(),f.length());

        boolean deleteResult = ftp.deleteFile(FTP_TEST_REMOTE_DIR+"upload_file/upload.dat");
        assertTrue("L'eliminazione non è avvenuta con successo!!",deleteResult);

        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testRemoteFileTree() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());

        FTPFile f = ftp.getFileInfo("/test/env/MbcraftJCommon",true);
        FolderElement el = FolderElement.createFromFTPFile(null,ftp,"/test/env/MbcraftJCommon",f);
        List<IFileSystemElement> childs = el.getChildren();
        Set<String> names = new HashSet<>();
        for (IFileSystemElement ch : childs) {
            names.add(ch.getName());
        }

        assertTrue("La directory download_dir non è presente nei risultati!!",names.contains("download_dir"));
        assertTrue("La directory download_file non è presente nei risultati!!",names.contains("download_file"));
        assertTrue("La directory list_files non è presente nei risultati!!",names.contains("list_files"));
        assertTrue("La directory upload_dir non è presente nei risultati!!",names.contains("upload_dir"));
        assertTrue("La directory upload_file non è presente nei risultati!!",names.contains("upload_file"));

        assertEquals("Il numero degli elementi non corrisponde!!",6,el.getChildCount());
        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    @Test
    public void testUploadFolderWithSpaces() {
        EasyFtp ftp = new EasyFtp(FTP_TEST_REMOTE_HOST,FTP_TEST_USERNAME,FTP_TEST_PASSWORD);
        assertTrue("Il login non è andato a buon fine!!",ftp.login());

        FTPFile[] result = ftp.listFiles("/test/env/MbcraftJCommon/upload_dir/");


        assertEquals("La cartella upload_dir non è vuota!!",0,result.length);


        boolean uploadResult = ftp.uploadFolder(new File("tests/data/another_dir_with_content/"),"/test/env/MbcraftJCommon/upload_dir/another_uploaded/");
        assertTrue("L'upload non è andato a buon fine!!",uploadResult);

        assertNotNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/",true));
        assertNotNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/a dir with spaces/",true));
        assertNotNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/a dir with spaces/a file with spaces.txt",true));
        assertNotNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/a dir with spaces/subdir/",true));
        assertNotNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/a dir with spaces/subdir/some spaces.txt",true));

        boolean deleteResult = ftp.deleteFolder(FTP_TEST_REMOTE_DIR+"upload_dir/another_uploaded/");
        assertTrue("L'eliminazione non è avvenuta con successo!!",deleteResult);

        assertNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/",true));
        assertNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/a dir with spaces/",true));
        assertNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/a dir with spaces/a file with spaces.txt",true));
        assertNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/a dir with spaces/subdir/",true));
        assertNull("Le info su un file remoto sono nulle!!",ftp.getFileInfo("/test/env/MbcraftJCommon/upload_dir/another_uploaded/a dir with spaces/subdir/some spaces.txt",true));

        assertTrue("Il logout non è andato a buon fine!!",ftp.logout());
    }

    private void deleteDirectory(File f) {
        File[] files = f.listFiles();
        for (File ff : files) {
            if (ff.isFile()) {
                ff.delete();
                continue;
            }
            if (ff.isDirectory())
                deleteDirectory(ff);
        }
        f.delete();
    }

}
