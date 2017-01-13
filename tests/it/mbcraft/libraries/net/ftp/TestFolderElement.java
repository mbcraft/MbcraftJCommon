package it.mbcraft.libraries.net.ftp;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by marco on 25/05/16.
 */
public class TestFolderElement {

    @Test
    public void testFolderElementNoOtherToCompareIsAllNewOrModified() {

        FolderElement root = new FolderElement(null,"example");
        root.add(new FileElement(root,"afile.txt",10));
        FolderElement el1 = new FolderElement(root,"folder1");
        root.add(el1);
        el1.add(new FileElement(el1,"nestedfile.txt",20));

        assertEquals("Il numero di elementi figli non corrisponde!!",2,root.getChildren().size());

        root.markIfNewOrModified(null);

        assertTrue("La root non è marcata come nuova!",root.isChanged());
        assertTrue("Il file è marcato come nuovo!",root.getFiles().iterator().next().isChanged());
        assertTrue("La cartella non è marcata come nuova!",root.getFolders().iterator().next().isChanged());

    }

    @Test
    public void testFolderElementWithSmallTree() {

        FolderElement root = new FolderElement(null,"example");
        root.add(new FileElement(root,"afile.txt",10));
        FolderElement el1 = new FolderElement(root,"folder1");
        root.add(el1);
        el1.add(new FileElement(el1,"nestedfile.txt",20));

        FolderElement root2 = new FolderElement(null,"misc");
        root2.add(new FileElement(root2,"afile.txt",10));

        root.markIfNewOrModified(root2);

        assertTrue("La root non è marcata come modificata!",root.isChanged());
        assertFalse("Il file è marcato come nuovo o modificato!",root.getFiles().iterator().next().isChanged());
        assertTrue("La cartella non è marcata come nuova o modificata!",root.getFolders().iterator().next().isChanged());

    }

    @Test
    public void testFolderTipsWithLocalFoldersMarking() {
        FolderElement base = FolderElement.createFromLocalDir(null,new File("tests/data/tips/base/"));
        FolderElement other = FolderElement.createFromLocalDir(null,new File("tests/data/tips/other/"));

        base.markIfNewOrModified(other);

        assertTrue("La root non è contrassegnata come modificata!!",base.isChanged());
        assertFalse("Il file sample_file.txt è marcato come modificato!!",base.getFileElementByName("sample_file.txt").isChanged());
        assertTrue("Il file new_file.txt non è marcato come modificato!!",base.getFileElementByName("new_file.txt").isChanged());
        assertFalse("Il risultato contiene missing_file.txt!!",base.containsFile("missing_file.txt"));
        assertTrue("Il file changed_file.txt non è marcato come modificato!!",base.getFileElementByName("changed_file.txt").isChanged());
        assertTrue("La cartella nested non è contrassegnata come modificata!!",base.getFolderElementByName("nested").isChanged());
        assertTrue("La cartella other non è contrassegnata come modificata!!",base.getFolderElementByName("other").isChanged());

    }

    @Test
    public void testFolderTipsWithLocalFoldersRemoveUnmodified() {
        FolderElement base = FolderElement.createFromLocalDir(null,new File("tests/data/tips/base/"));
        FolderElement other = FolderElement.createFromLocalDir(null,new File("tests/data/tips/other/"));

        base.markIfNewOrModified(other);

        base.removeUnmodifiedElements();

        assertTrue("Il file changed_file.txt non è presente nei risultati!!",base.containsFile("changed_file.txt"));
        assertFalse("Il file missing_file.txt è presente nei risultati!!",base.containsFile("missing_file.txt"));
        assertFalse("Il file sample_file.txt è presente nei risultati!!",base.containsFile("sample_file.txt"));

        assertTrue("La cartella other non è presente nei risultati!!",base.containsFolder("other"));
        FolderElement of = base.getFolderElementByName("other");
        assertTrue("Il file other_file.txt non è presente nella cartella other!!",of.containsFile("other_file.txt"));

        assertTrue("La cartella nested non è presente nei risultati!!",base.containsFolder("nested"));
        FolderElement ns = base.getFolderElementByName("nested");
        assertTrue("La cartella empty_folder non è presente nella cartella nested!!",ns.containsFolder("empty_folder"));
        assertTrue("La cartella more_nested non è presente nella cartella nested!!",ns.containsFolder("more_nested"));
        FolderElement mn = ns.getFolderElementByName("more_nested");
        assertTrue("Il file a_new_file.txt non è presente nella cartella more_nested!!",mn.containsFile("a_new_file.txt"));
    }
}
