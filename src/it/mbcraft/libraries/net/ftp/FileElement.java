package it.mbcraft.libraries.net.ftp;

import it.sauronsoftware.ftp4j.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by marco on 23/05/16.
 */
public class FileElement extends AbstractElement {

    private static Logger logger = LogManager.getLogger(FileElement.class);
    private final long mySize;

    public FileElement(FolderElement parent, String name, long size) {
        super(name, parent);
        mySize = size;
        logger.debug("FileElement : "+name+" size="+size);
    }

    public long getSize() {
        return mySize;
    }

    public int getChildCount() {
        return 0;
    }

    public List<IFileSystemElement> getChildren() {
        throw new IllegalStateException("FileElement have no children.");
    }

    public boolean isFile() {
        return true;
    }

    public boolean isFolder() {
        return false;
    }


    /**
     * Crea un elemento da utilizzare per l'albero di rappresentazione del filesystem
     *
     * @param f Un file ftp remoto
     * @return L'elemento per l'albero
     */
    static FileElement createFromFTPFile(FolderElement parent, FTPFile f) {
        if (f==null)
            throw new InvalidParameterException("Uno o più parametri non sono validi.");
        FileElement result = new FileElement(parent, f.getName(),f.getSize());
        return result;
    }

    /**
     * Crea un elemento da utilizzare per la creazione di un albero di rappresentazione del filesystem.
     *
     * @param f Il file
     * @return L'elemento per l'albero
     */
    static FileElement createFromLocalFile(FolderElement parent, File f) {
        if (f==null || !f.exists() || !f.isFile())
            throw new InvalidParameterException("Il parametro non è un file valido.");
        return new FileElement(parent, f.getName(),f.length());
    }

    public boolean equals(Object o) {
        if (o!=null && o instanceof FileElement) {
            FileElement other = (FileElement) o;
            logger.debug("COMPARING FileElement : "+getName()+"("+getSize()+") <> "+other.getName()+"("+other.getSize()+")");
            boolean result = myName.equals(other.myName) && getSize()==other.getSize();
            return result;
        }
        return false;
    }

    void markIfNewOrModifiedNested(FileElement other) {
        if (!equals(other)) markModified();
    }

}
