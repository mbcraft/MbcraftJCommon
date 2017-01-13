package it.mbcraft.libraries.utils;

/**
 * Created by marco on 10/06/16.
 */
public class OSChecker {
    public static void ensureNotWindows() {
        if (System.getProperty("os.name").toLowerCase().contains("windows"))
            throw new IllegalStateException("Command not allowed on windows os.");
    }
}
