package it.mbcraft.libraries.net.ftp;

/**
 * This interface collects the events fired by the ftp client.
 * It is useful for displaying a textual log of what is happening.
 *
 * Created by marco on 26/05/16.
 */
public interface IFTPOperationListener {

    /**
     * Called when ftp login is done.
     */
    void loginDone();

    /**
     * Called when info are being fetched for a remote file
     *
     * @param remotePath The remote file or folder path
     */
    void fetchingInfo(String remotePath);

    /**
     * Called when a file listing is being done
     *
     * @param remoteFolder The remote folder path
     */
    void listingFiles(String remoteFolder);

    /**
     * Called before a file is being uploaded.
     *
     * @param path The path of the file
     */
    void uploadingFile(String path);

    /**
     * Called when a file is uploaded
     *
     * @param path The remote file path
     */
    void fileUploaded(String path);

    /**
     * Called when a folder is being uploaded
     *
     * @param remoteFolder Called when the folder is being uploaded
     */
    void uploadingFolder(String remoteFolder);

    /**
     * Called when a folder is uploaded
     *
     * @param remoteFolder The remote folder path
     */
    void folderUploaded(String remoteFolder);

    /**
     * Called when a file is being downloaded
     *
     * @param path The path of the file
     */
    void downloadingFile(String path);

    /**
     * Called when a remote file is downloaded
     *
     * @param path The remote file path
     */
    void fileDownloaded(String path);

    /**
     * Called when a folder is being downloaded
     *
     * @param remoteFolder The remote folder being downloaded
     */
    void downloadingFolder(String remoteFolder);

    /**
     * Called when a remote folder is downloaded
     *
     * @param remoteFolder The remote folder path
     */
    void folderDownloaded(String remoteFolder);

    /**
     * Called when a remote folder is created
     *
     * @param remoteFolder The remote folder path
     */
    void folderCreated(String remoteFolder);

    /**
     * Called when a remote folder is deleted
     *
     * @param remoteFolder The remote folder path
     */
    void folderDeleted(String remoteFolder);

    /**
     * Called when a remote file is delete
     *
     * @param path The remote file path
     */
    void fileDeleted(String path);

    /**
     * Called when ftp logout is done.
     */
    void logoutDone();


}
