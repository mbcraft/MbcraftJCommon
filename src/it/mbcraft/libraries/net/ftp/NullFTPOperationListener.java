package it.mbcraft.libraries.net.ftp;

/**
 * Created by marco on 26/05/16.
 */
public class NullFTPOperationListener implements IFTPOperationListener {
    @Override
    public void loginDone() {

    }

    @Override
    public void fetchingInfo(String remotePath) {

    }

    @Override
    public void listingFiles(String remoteFolder) {

    }

    @Override
    public void uploadingFile(String path) {

    }

    @Override
    public void fileUploaded(String path) {

    }

    @Override
    public void uploadingFolder(String remoteFolder) {

    }

    @Override
    public void fileDownloaded(String path) {

    }

    @Override
    public void downloadingFolder(String remoteFolder) {

    }

    @Override
    public void folderCreated(String remoteFolder) {

    }

    @Override
    public void folderUploaded(String remoteFolder) {

    }

    @Override
    public void downloadingFile(String path) {

    }

    @Override
    public void folderDownloaded(String remoteFolder) {

    }

    @Override
    public void folderDeleted(String remoteFolder) {

    }

    @Override
    public void fileDeleted(String path) {

    }

    @Override
    public void logoutDone() {

    }
}
