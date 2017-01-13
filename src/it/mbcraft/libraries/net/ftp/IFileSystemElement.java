package it.mbcraft.libraries.net.ftp;

import java.util.List;

/**
 * Created by marco on 23/05/16.
 */
public interface IFileSystemElement {

    FolderElement getParent();

    String getName();

    boolean isFile();

    boolean isFolder();

    boolean isChanged();

    int getChildCount();

    List<IFileSystemElement> getChildren();

    long getSize();

    void dump(int tabs);

    void markModified();
}
