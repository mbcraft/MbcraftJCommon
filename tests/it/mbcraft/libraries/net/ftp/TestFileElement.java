package it.mbcraft.libraries.net.ftp;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by marco on 25/05/16.
 */
public class TestFileElement {

    @Test
    public void testCompareFileElements() {
        FileElement f1 = FileElement.createFromLocalFile(null,new File("tests/data/tips/base/changed_file.txt"));
        FileElement f2 = FileElement.createFromLocalFile(null,new File("tests/data/tips/other/changed_file.txt"));

        f1.markIfNewOrModifiedNested(f2);

        assertTrue("Il file f1 non è contrassegnato come modificato!!",f1.isChanged());
        assertFalse("Il file f2 è contrassegnato come modificato!!",f2.isChanged());

        f2.markIfNewOrModifiedNested(null);
        assertTrue("Il file f2 non è contrassegnato come modificato!!",f2.isChanged());

    }
}
