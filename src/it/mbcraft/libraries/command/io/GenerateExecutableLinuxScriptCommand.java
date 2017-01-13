package it.mbcraft.libraries.command.io;

import it.mbcraft.libraries.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This code is property of MBCRAFT di Marco Bagnaresi. All rights reserved.
 * <p>
 * Created by marco on 06/07/16.
 */
public class GenerateExecutableLinuxScriptCommand implements ICommand {

    private Logger logger = LogManager.getLogger(GenerateExecutableLinuxScriptCommand.class);

    private final File myScriptFile;
    private Shell scriptShell = Shell.SH;
    private List<String> scriptLines = new ArrayList<>();

    enum Shell {
        SH,BASH
    }

    public GenerateExecutableLinuxScriptCommand(File scriptFile) {
        myScriptFile = scriptFile;
    }

    public File getScriptFile() {
        return myScriptFile;
    }

    public void setScriptShell(Shell shell) {
        scriptShell = shell;
    }

    public Shell getScriptShell() {
        return scriptShell;
    }

    public void addScriptLine(String ss) {
        scriptLines.add(ss);
    }

    public void setScriptLines(List<String> lines) {
        scriptLines = lines;
    }

    public List<String> getScriptLines() {
        return scriptLines;
    }

    @Override
    public void execute() {

        try (PrintWriter pw = new PrintWriter(new FileWriter(myScriptFile))) {

            switch (scriptShell) {
                case SH : pw.println("#!/bin/sh");
                case BASH : pw.println("#!/bin/bash");
                default : new IllegalStateException("Unsupported shell.");
            }

            pw.println();

            for (String line : scriptLines) {
                pw.println(line);
            }

            pw.println();

        } catch (IOException ex) {
            logger.error("Unable to write script file",ex);
        }

        //setting executable permission
        myScriptFile.setExecutable(true);

    }
}
