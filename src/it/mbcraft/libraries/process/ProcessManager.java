package it.mbcraft.libraries.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class watches a Process and fires an event when the process finishes.
 * <p>
 * Created by marco on 10/05/16.
 */
public class ProcessManager {

    private final String myName;
    private boolean isRunning = false;
    private final ProcessBuilder builder;
    private Process toWatch = null;
    private boolean daemonMode = false;

    private static final Logger logger = LogManager.getLogger(ProcessManager.class);

    /**
     * Creates a ProcessManager. The process builder must already be configured.
     *
     * @param watcherName The name of the watcher thread
     * @param pb          The pre-configured ProcessBuilder
     */
    public ProcessManager(String watcherName, ProcessBuilder pb) {
        if (watcherName == null) throw new InvalidParameterException("The watcher thread name can't be null!");
        if (pb == null)
            throw new InvalidParameterException("The ProcessBuilder for creating the process can't be null!");

        myName = watcherName;
        builder = pb;
    }

    private final List<IProcessEndListener> listeners = new ArrayList<>();

    public void addProcessEndListener(IProcessEndListener listener) {
        listeners.add(listener);
    }

    public void removeProcessEndListener(IProcessEndListener listener) {
        listeners.remove(listener);
    }

    private void fireProcessEndedEvent() {
        for (IProcessEndListener listener : listeners) {
            listener.processEnded(toWatch);
        }
    }

    public void start() {

        final StringBuilder sb = new StringBuilder();
        Iterator<String> it = builder.command().iterator();
        sb.append("Running from command line : ");
        while (it.hasNext())
            sb.append(it.next()+" ");

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    logger.info(sb.toString());
                    toWatch = builder.start();
                    isRunning = true;
                    toWatch.waitFor();
                } catch (IOException ex) {
                    logger.catching(ex);
                } catch (InterruptedException e) {
                    logger.catching(e);
                } finally {
                    isRunning = false;
                    fireProcessEndedEvent();
                }
            }
        },"Process Manager Thread ("+builder.command().get(0)+")");

        t.setDaemon(daemonMode);
        t.setName(myName);
        t.start();

    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isDaemonMode() {
        return daemonMode;
    }

    public void setDaemonMode(boolean daemon) {
        daemonMode = daemon;
    }

    public void forceStop() {
        if (isRunning) {
            toWatch.destroy();
        }
    }


}
