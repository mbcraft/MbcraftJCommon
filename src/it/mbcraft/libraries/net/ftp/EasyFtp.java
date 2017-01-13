package it.mbcraft.libraries.net.ftp;

import it.mbcraft.libraries.command.CodeClassification;
import it.mbcraft.libraries.command.CodeType;
import it.sauronsoftware.ftp4j.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by marco on 19/05/16.
 */
@CodeClassification(CodeType.GENERIC)
public class EasyFtp {

    private final Logger logger = LogManager.getLogger(EasyFtp.class);

    private final String myHost, myUsername, myPassword;
    private FTPClient ftp;
    private IFTPOperationListener ftpOperationListener = new NullFTPOperationListener();

    /**
     * Creates an FTP client for connecting to a remote host and performing FTP operations.
     *
     * @param host The ftp host name or ip address
     * @param username The ftp username
     * @param password The ftp password
     */
    public EasyFtp(String host, String username, String password) {
        myHost = host;
        myUsername = username;
        myPassword = password;
    }

    public IFTPOperationListener getFTPOperationListener() {
        return ftpOperationListener;
    }

    public void setFTPOperationListener(IFTPOperationListener listener) {
        ftpOperationListener = listener;
    }

    /**
     * Fixes the remotePath, adding a trailing slash if needed.
     *
     * @param remotePath the remote path to fix
     * @return the fixed path
     */
    public static String asRemoteFolder(String remotePath) {
        if (!remotePath.endsWith("/")) return remotePath+"/";
        return remotePath;
    }

    public static int calculateTotalSize(File toCalc) {
        int result = 0;
        result += toCalc.length();
        if (toCalc.isDirectory()) {
            File[] content = toCalc.listFiles();
            for (File f : content) {
                if (f.isDirectory())
                    result += calculateTotalSize(f);
                else
                    result += f.length();
            }
        }
        return result;
    }

    /**
     * Actually performs login.
     *
     * @return true if the operation is successful, false otherwise.
     */
    public boolean login() {
        ftp = new FTPClient();
        try {

            ftp.setCharset("UTF-8");
            logger.debug("Connecting to FTP : " + myHost);

            ftp.setType(FTPClient.TYPE_BINARY);
            ftp.setPassive(true);
            ftp.setSecurity(FTPClient.SECURITY_FTP);
            ftp.setMLSDPolicy(FTPClient.MLSD_IF_SUPPORTED);
            logger.debug("Login - User : " + myPassword + " Pass : ******");
            ftp.connect(myHost);
            ftp.login(myUsername, myPassword);
            ftpOperationListener.loginDone();
            return true;
        } catch (IOException | FTPException | FTPIllegalReplyException e) {
            logger.catching(e);
            return false;
        }
    }

    /**
     * Returns the file information associated to a remote file or folder.
     * @param remotePath The full remote path of the file or folder
     * @param onlyLastNamePart if only the last name part should be kept as name
     * @return the file info, or null if the remotePath points to an invalid location
     */
    public FTPFile getFileInfo(String remotePath, boolean onlyLastNamePart) {
        checkConnected();
        try {
            ftpOperationListener.fetchingInfo(remotePath);

            String pathParts[] = remotePath.split("/");
            String myPath = "";
            for (int i=0;i<pathParts.length-1;i++) {
                myPath+="/"+pathParts[i];
            }

            FTPFile[] result = ftp.list(myPath);
            for (FTPFile ff : result) {
                if (ff.getName().equals(pathParts[pathParts.length-1])) {
                    if (!onlyLastNamePart) ff.setName(remotePath);
                    return ff;
                }
            }
        } catch (Exception e) {
            logger.catching(e);
        }
        return null;
    }

    /**
     * Lists the files on the remote root folder
     *
     * @return The file list or null if something bad happens
     */
    public FTPFile[] listRootFiles() {
        checkConnected();
        logger.debug("Listing root files ...");
        try {
            ftpOperationListener.listingFiles("/");
            return ftp.list();
        } catch (Exception e) {
            logger.catching(e);
        }
        return null;
    }

    /**
     * List files on a remote path
     *
     * @param remotePath The full path of the remote folder
     * @return The file list, or null if the remotePath points to an invalid location.
     */
    public FTPFile[] listFiles(String remotePath) {
        checkConnected();
        try {
            logger.debug("Listing files from : " + remotePath);
            String remoteFolder = asRemoteFolder(remotePath);
            FTPFile[] files = ftp.list(remoteFolder);

            if (files!=null) {
                List<FTPFile> realResult = new ArrayList<>();
                for (FTPFile ff : files) {
                    if (ff.getName().equals(".") || ff.getName().equals("..")) continue;
                    realResult.add(ff);
                }
                ftpOperationListener.listingFiles(remoteFolder);
                return realResult.toArray(new FTPFile[realResult.size()]);
            }
            else return null;
        } catch (Exception e) {
            logger.catching(e);
        }
        return null;
    }

    /**
     * Downloads a folder using the provided tips for downloading only the selected files and folders.
     * Root folder tips name is ignored.
     *
     * @param tips The tips to use for choosing which file to download
     * @param remotePath The path of the remote folder to download
     * @param f The local path of the folder to create
     * @return true if the operation is successful, false otherwise
     */
    public boolean downloadFolderWithTips(FolderElement tips,String remotePath, File f) {
        checkConnected();
        tips.resetSize();
        logger.debug("Downloading folder with tips : "+remotePath);
        ftpOperationListener.downloadingFolder(remotePath);

        return downloadFolderWithTipsImpl(tips,asRemoteFolder(remotePath),f);
    }

    private boolean downloadFolderWithTipsImpl(FolderElement tips,String remoteFolder, File f) {
        f.mkdir();
        for (FileElement elem : tips.getFiles()) {
            downloadFile(remoteFolder+elem.getName(),new File(f,elem.getName()));
        }
        for (FolderElement elem : tips.getFolders()) {
            downloadFolderWithTips(elem,remoteFolder+asRemoteFolder(elem.getName()),new File(f,elem.getName()));
        }
        ftpOperationListener.folderDownloaded(remoteFolder);
        return true;
    }

    /**
     * Uploads folder and its content recursively following the tips provided (uploading only those that are present inside the tips tree).
     *
     * @param tips The folder tips tips
     * @param f The local folder file
     * @param remotePath The full remote folder that is mapped to the local one
     * @return true if the operation is successful, false otherwise
     */
    public boolean uploadFolderWithTips(FolderElement tips,File f,String remotePath) {
        checkConnected();
        tips.resetSize();
        logger.debug("Uploading folder with tips : " + f.getAbsolutePath());
        ftpOperationListener.uploadingFolder(f.getAbsolutePath());
        if (f.exists() && f.isDirectory() && f.canRead()) {
            return uploadFolderWithTipsImpl(tips, f, asRemoteFolder(remotePath));
        } else
            throw new InvalidParameterException("La directory non esiste, non è una directory o non ha i permessi di lettura!");
    }

    /**
     * Actual implementation of uploadFolderWithTips
     *
     * @param tips
     * @param f
     * @param remoteFolder
     * @return
     */
    private boolean uploadFolderWithTipsImpl(FolderElement tips,File f,String remoteFolder) {
        boolean ok = true;
        createFolder(remoteFolder);
        Collection<FileElement> files = tips.getFiles();
        for (FileElement file_el : files) {
            ok &= uploadFileImpl(new File(f,file_el.getName()),remoteFolder+file_el.getName());
        }
        Collection<FolderElement> folders = tips.getFolders();
        for (FolderElement fold_el : folders) {
            ok &= uploadFolderWithTipsImpl(fold_el,new File(f,fold_el.getName()),remoteFolder+asRemoteFolder(fold_el.getName()));
        }
        ftpOperationListener.folderUploaded(remoteFolder);
        return ok;
    }

    /**
     * Uploads a folder and all its content, recursively.
     *
     * @param f The file object pointing to a local folder
     * @param remotePath The full path of the remote folder that is mapped to the local one
     * @return true is the operation is successful, false otherwise
     */
    public boolean uploadFolder(File f, String remotePath) {
        checkConnected();
        ftpOperationListener.uploadingFolder(f.getAbsolutePath());
        if (f.exists() && f.isDirectory() && f.canRead()) {
            return uploadFolderImpl(f, asRemoteFolder(remotePath));
        } else
            throw new InvalidParameterException("La directory non esiste, non è una directory o non ha i permessi di lettura!");
    }

    /**
     * Actual implementation for uploadFolder
     *
     * @param f
     * @param remoteFolder
     * @return
     */
    private boolean uploadFolderImpl(File f, String remoteFolder) {
        logger.debug("Uploading folder : " + f.getAbsolutePath());
        boolean ok = true;
        createFolder(remoteFolder);
        File[] files = f.listFiles();
        if (files != null) {
            for (File ff : files) {
                if (ff.isFile())
                    ok &= uploadFileImpl(new File(f, ff.getName()), remoteFolder + ff.getName());
                if (ff.isDirectory())
                    ok &= uploadFolderImpl(new File(f, ff.getName()), remoteFolder + asRemoteFolder(ff.getName()));
            }
        }
        ftpOperationListener.folderUploaded(remoteFolder);
        return ok;
    }

    /**
     * Uploads a file to a remote location
     *
     * @param f The local file object
     * @param remotePath The full remote path of the file to upload
     * @return true if the operation is successful, false otherwise
     */
    public boolean uploadFile(File f, String remotePath) {
        checkConnected();
        ftpOperationListener.uploadingFile(f.getAbsolutePath());
        if (f.exists() && f.isFile() && f.canRead()) {
            return uploadFileImpl(f, remotePath);
        } else
            throw new InvalidParameterException("Il file da uploadare non esiste, non è un file o non può essere letto!");
    }

    /**
     * Actual uploadFile implementation
     *
     * @param f
     * @param remotePath
     * @return
     */
    private boolean uploadFileImpl(File f, String remotePath) {
        try {
            logger.debug("Uploading file : " + f.getAbsolutePath());
            String directory = remotePath.substring(0,remotePath.lastIndexOf('/')+1);
            //System.out.println("Directory is :"+directory);
            String filename = remotePath.substring(remotePath.lastIndexOf('/')+1);
            //System.out.println("Filename is : "+filename);
            ftp.changeDirectory(directory);
            ftp.upload(filename,new FileInputStream(f),0,0,null);
            ftpOperationListener.fileUploaded(remotePath);
            return true;
        } catch (Exception e) {
            logger.catching(e);
        }
        return false;
    }

    /**
     * Downloads a folder from a remote location.
     *
     * @param remotePath The full path of the remote folder to download
     * @param f The local folder path in which download the remote one
     * @return true if the operation is successful, false otherwise
     */
    public boolean downloadFolder(String remotePath, File f) {
        checkConnected();
        try {
            String remoteFolder = asRemoteFolder(remotePath);
            ftpOperationListener.uploadingFolder(remotePath);
            logger.debug("Downloading remote folder " + remotePath + " to local " + f.getAbsolutePath());
            f.mkdir();
            if (f.exists() && f.isDirectory() && f.canWrite()) {
                FTPFile[] files = ftp.list(remoteFolder);
                for (FTPFile ff : files) {
                    if (ff.getName().equals(".") || ff.getName().equals("..")) continue;
                    if (ff.getType() == FTPFile.TYPE_FILE)
                        downloadFile(remoteFolder + ff.getName(), new File(f, ff.getName()));
                    if (ff.getType() == FTPFile.TYPE_DIRECTORY)
                        downloadFolder(remoteFolder + asRemoteFolder(ff.getName()), new File(f, ff.getName()));
                }
                ftpOperationListener.folderDownloaded(remoteFolder);
                return true;
            } else
                return false;
        } catch (Exception e) {
            logger.catching(e);
        }
        return false;
    }

    /**
     * Downloads a file from a remote location.
     *
     * @param remotePath The remote path of the file to download
     * @param f The local path of the file
     * @return true if the operation is successful, false otherwise
     */
    public boolean downloadFile(String remotePath, File f) {
        checkConnected();
        ftpOperationListener.downloadingFile(remotePath);
        try {
            logger.debug("Downloading remote file " + remotePath + " to local " + f.getAbsolutePath());

            ftp.download(remotePath,f);

            ftpOperationListener.fileDownloaded(remotePath);
            return true;
        } catch (Exception e) {
            logger.catching(e);
        }
        return false;
    }

    /**
     * Creates a folder in a remote location
     *
     * @param remotePath The full path of the remote folder to create
     * @return true if the operation is successful, false otherwise
     */
    public boolean createFolder(String remotePath) {
        checkConnected();

        String remoteFolder = asRemoteFolder(remotePath);

        try {

            String currentDirectory = ftp.currentDirectory();

            ftp.changeDirectory(remoteFolder);

            ftp.changeDirectory(currentDirectory);

            return true;
        } catch (Exception e) {
            try {
                ftp.createDirectory(remotePath);

                ftpOperationListener.folderCreated(remoteFolder);

                return true;
            } catch (Exception ex) {
                logger.catching(e);
                return false;
            }

        }

    }

    /**
     * Deletes the content of a remote folder, keeping the empty folder.
     *
     * @param remotePath The full path of the remote folder to empty.
     * @return true if the operation is successful, false otherwise
     */
    public boolean deleteFolderContent(String remotePath) {
        checkConnected();
        try {
            String remoteFolder = asRemoteFolder(remotePath);
            logger.debug("Deleting remote folder content : " + remotePath);
            FTPFile[] files = ftp.list(remoteFolder);
            ftpOperationListener.listingFiles(remoteFolder);
            for (FTPFile f : files) {
                if (f.getName().equals(".") || f.getName().equals("..")) continue;
                if (f.getType() == FTPFile.TYPE_FILE) {
                    deleteFile(remoteFolder + f.getName());
                }
                if (f.getType() == FTPFile.TYPE_DIRECTORY) {
                    deleteFolder(remoteFolder + asRemoteFolder(f.getName()));
                }
            }
            return true;
        } catch (Exception ex) {
            logger.catching(ex);
        }
        return false;
    }

    /**
     * Deletes a folder from a remote location.
     *
     * @param remotePath The full path of the remote to delete
     * @return true if the operation is successful, false otherwise
     */
    public boolean deleteFolder(String remotePath) {
        checkConnected();
        try {
            String remoteFolder = asRemoteFolder(remotePath);
            deleteFolderContent(remoteFolder);
            logger.debug("Deleting empty remote folder : " + remoteFolder);
            ftp.deleteDirectory(remoteFolder);
            ftpOperationListener.folderDeleted(remoteFolder);
            return true;
        } catch (Exception e) {
            logger.catching(e);
            return false;
        }
    }

    /**
     * Deletes a file from a remote location
     *
     * @param remotePath The full remote path of the file to delete
     * @return true if the operation is successful, false otherwise
     */
    public boolean deleteFile(String remotePath) {
        checkConnected();
        try {
            logger.debug("Deleting remote file : " + remotePath);
            String directory = remotePath.substring(0,remotePath.lastIndexOf('/'));
            ftp.changeDirectory(directory);
            ftp.deleteFile(remotePath.substring(remotePath.lastIndexOf('/')+1));
            ftpOperationListener.fileDeleted(remotePath);
            return true;
        } catch (Exception e) {
            logger.catching(e);
        }
        return false;
    }

    /**
     * Logs out from the remote ftp.
     *
     * @return true if the operation is successful, false otherwise
     */
    public boolean logout() {
        checkConnected();
        try {
            logger.debug("Logging out FTP client ...");
            ftp.noop();
            ftp.disconnect(true);
            ftp = null;
            ftpOperationListener.logoutDone();
            return true;
        } catch (Exception ex) {
            logger.catching(ex);
            return false;
        }

    }

    /**
     * Checks if the client is actually connected to the remote ftp, throwing an exception if it's not.
     */
    private void checkConnected() {
        if (ftp==null) throw new IllegalStateException("FTP client is not connected!!");
    }


}
