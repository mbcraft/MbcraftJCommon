package it.mbcraft.libraries.net.ftp;

/**
 * Created by marco on 23/05/16.
 */
abstract class AbstractElement implements IFileSystemElement {

    protected final FolderElement myParent;
    protected final String myName;
    private boolean modified = false;

    AbstractElement(String name, FolderElement parent) {
        myName = name;
        myParent = parent;
    }

    public FolderElement getParent() {
        return myParent;
    }

    public void markModified() {
        modified = true;
        markParentModified();
    }

    void markParentModified() {
        if (myParent!=null && !myParent.isChanged())
            myParent.markModified();
    }

    @Override
    public int hashCode() {
        return 3*myName.hashCode()+getChildCount()*101+(int)getSize()+(isFile() ? 17 : 0)+(isFolder() ? 53 : 0);
    }

    public void dump(int tabs) {
        String spacing = "";
        for (int i=0;i<tabs;i++)
            spacing+="\t";
        System.out.println(spacing+(isFile() ? "F : " : "")+(isFolder() ? "D : " : "")+myName+(isFolder() ? "/" : "")+(isFile() ? " ("+getSize()+")" : "")+(isChanged() ? " (CHANGED)" : ""));
        if (getChildCount()>0) {
            for (IFileSystemElement ch : getChildren())
                ch.dump(tabs+1);
        }
    }

    public boolean isChanged() {
        return modified;
    }

    public String getName() {
        return myName;
    }

}
