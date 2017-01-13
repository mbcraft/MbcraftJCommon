package it.mbcraft.libraries.net.ftp;

import it.sauronsoftware.ftp4j.FTPFile;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by marco on 23/05/16.
 */
public class FolderElement extends AbstractElement {

    private final List<IFileSystemElement> elements = new ArrayList<>();
    private final HashMap<String,FileElement> files = new HashMap<>();
    private final HashMap<String,FolderElement> folders = new HashMap<>();

    private long mySize = -1;

    public FolderElement(FolderElement parent, String name) {
        super(name, parent);
    }

    public void addAll(Collection<IFileSystemElement> elements) {
        for (IFileSystemElement el : elements)
            add(el);
    }

    public void add(IFileSystemElement element) {
        if (element.isFolder())
            folders.put(element.getName(),(FolderElement) element);
        if (element.isFile())
            files.put(element.getName(),(FileElement) element);

        elements.add(element);
    }

    private void removeAll() {
        folders.clear();
        files.clear();
        elements.clear();
    }

    public Collection<FolderElement> getFolders() {
        return folders.values();
    }

    public Collection<FileElement> getFiles() {
        return files.values();
    }

    public void resetSize() {
        mySize = -1;
        for (FolderElement el : folders.values()) {
            el.resetSize();
        }
    }

    public long getSize() {
        if (mySize == -1) {
            long totalSize = 0;
            for (IFileSystemElement el : elements) {
                totalSize += el.getSize();
            }
            mySize = totalSize;
        }
        return mySize;
    }

    public int getChildCount() {
        return elements.size();
    }

    public List<IFileSystemElement> getChildren() {
        return elements;
    }

    public boolean isFile() {
        return false;
    }

    public boolean isFolder() {
        return true;
    }

    /**
     * Creates a representation tree of the filesystem starting from a remote ftp folder.
     *
     * @param client     The ftp client
     * @param remotePath The full remote path of the remote folder
     * @param f          The FTPFile pointing to the same remotePath location
     * @return The tree
     */
    public static FolderElement createFromFTPFile(FolderElement parent, EasyFtp client, String remotePath, FTPFile f) {
        if (client == null || f == null)
            throw new InvalidParameterException("Uno o più parametri non sono validi.");
        FolderElement result = new FolderElement(parent, f.getName());
        FTPFile[] content = client.listFiles(remotePath);
        for (FTPFile ff : content) {
            if (ff.getType() == FTPFile.TYPE_FILE) {
                result.add(FileElement.createFromFTPFile(result, ff));
            }
            if (ff.getType() == FTPFile.TYPE_DIRECTORY) {
                result.add(FolderElement.createFromFTPFile(result, client, EasyFtp.asRemoteFolder(remotePath) + ff.getName(), ff));
            }
        }

        return result;
    }

    /**
     * Creates a representation tree of the filesystem starting from a specified local folder.
     *
     * @param f The File object pointing to a folder
     * @return The tree
     */
    public static FolderElement createFromLocalDir(FolderElement parent, File f) {
        if (f == null || !f.exists() || !f.isDirectory())
            throw new InvalidParameterException("Il parametro non è una directory valida.");

        FolderElement result = new FolderElement(parent, f.getName());

        File[] elements = f.listFiles();
        for (File el : elements) {
            if (el.isFile()) {
                result.add(FileElement.createFromLocalFile(result, el));
            }
            if (el.isDirectory()) {
                result.add(FolderElement.createFromLocalDir(result, el));
            }
        }
        return result;
    }

    /**
     * Prints a dump of this tips tree, starting from this FolderElement.
     */
    public void dump() {
        dump(0);
    }

    /**
     * Actual equals implementation for FolderElement.
     *
     * @param o The other object to check
     * @return true if the objects are considered equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof FolderElement) {
            FolderElement other = (FolderElement) o;
            boolean objEq = myName.equals(other.myName) && getSize() == other.getSize() && getChildCount() == other.getChildCount();
            if (!objEq) return false;
            for (IFileSystemElement el : elements) {
                if (!other.contains(el))
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Marks this element and all its subtree as modified.
     */
    private void markTreeModified() {
        markModified();
        for (FileElement el : getFiles()) {
            el.markModified();
        }
        for (FolderElement el : getFolders()) {
            el.markTreeModified();
        }
    }

    /**
     * Compares two trees and marks the elements that has been changed. The root folder name is not considered.
     *
     * @param otherRoot The other tree to compare with
     */
    public void markIfNewOrModified(IFileSystemElement otherRoot) {
        //special case : only mark if other is null, not for different names
        if (otherRoot==null)
            markTreeModified();
        else
            markIfNewOrModifiedCompareImpl(otherRoot);
    }

    private void markIfNewOrModifiedCompareImpl(IFileSystemElement otherRoot) {
        FolderElement otherFolderRoot = (FolderElement) otherRoot;
        for (IFileSystemElement el : getFiles()) {
            FileElement currentFile = (FileElement) el;
            FileElement otherFile = null;
            if (otherFolderRoot.containsFile(currentFile.getName())) {
                otherFile = otherFolderRoot.getFileElementByName(currentFile.getName());
            }
            //if the other element is null el is marked as modified
            currentFile.markIfNewOrModifiedNested(otherFile);

        }
        for (IFileSystemElement el : getFolders()) {
            FolderElement currentFolder = (FolderElement) el;
            FolderElement otherFolder = null;

            if (otherFolderRoot.containsFolder(currentFolder.getName())) {
                otherFolder = otherFolderRoot.getFolderElementByName(currentFolder.getName());
            }
            //if the other element is null el is marked as modified
            currentFolder.markIfNewOrModifiedNestedImpl(otherFolder);

        }
    }

    private void markIfNewOrModifiedNestedImpl(FolderElement other) {
        if (!equals(other))
            markTreeModified();
        else
            markIfNewOrModifiedCompareImpl(other);
    }

    /**
     * Removes the unmodified elements from this FolderElement and all its nested elements.
     */
    public void removeUnmodifiedElements() {
        List<IFileSystemElement> newElements = new ArrayList<>();
        for (IFileSystemElement el : elements) {
            if (el.isChanged()) {
                newElements.add(el);
            }
        }
        removeAll();
        addAll(newElements);
        for (IFileSystemElement el : elements) {
            if (el instanceof FolderElement) {
                FolderElement de = (FolderElement) el;
                de.removeUnmodifiedElements();
            }
        }
    }

    /**
     * Checks if a IFileSystemElement with the same type, name, size, etc .. is contained inside this one.
     * @param el The element to check
     * @return true if it is contained inside this one, false otherwise
     */
    private boolean contains(IFileSystemElement el) {
        return elements.contains(el);
    }

    /**
     * Checks if this element contains a FileElement with the specified name.
     *
     * @param name The name of the file
     * @return true if the element exists, false otherwise
     */
    public boolean containsFile(String name) {
        return files.containsKey(name);
    }

    /**
     * Gets a FileElement contained in this FolderElement, by name
     * @param name The name of the element
     * @return The FileElement
     */
    FileElement getFileElementByName(String name) {
        return files.get(name);
    }

    /**
     * Checks if this element contains a folder with the given name
     *
     * @param name The name of the folder
     * @return true if the element exists, false otherwise
     */
    public boolean containsFolder(String name) {
        return folders.containsKey(name);
    }

    /**
     * Returns the FolderElement inside this one with the specified name
     * @param name The name of the folder
     * @return The FolderElement
     */
    FolderElement getFolderElementByName(String name) {
        return folders.get(name);
    }

}
