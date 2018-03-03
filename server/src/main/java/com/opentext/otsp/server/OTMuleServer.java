/*
 * This file is part of OTSP.
 * (C) 2011-2011 - Open Text Corporation
 * All rights reserved.
 */
package com.opentext.otsp.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.notification.ServerNotification;
import org.mule.api.context.notification.ServerNotificationListener;
import org.mule.config.ConfigResource;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.context.notification.NotificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the main entry point for OTSP running in Mule.
 */
public class OTMuleServer {

    /**
     * Time in milliseconds. Used for delaying the next poll on server status
     */
    private static final int DELAY_BETWEEN_CHECKING_SERVER_STATUS = 1000;

    /**
     * Command line options.
     */
    private static Options OPTIONS = new Options();

    /**
     * Name of the logger.
     */
    private static final String LOG_NAME = OTMuleServer.class.getName();

    /**
     * Logger used to output information.
     */
    private static final Logger LOG = LoggerFactory.getLogger(OTMuleServer.LOG_NAME);

    /**
     * Default configuration path.
     */
    private static final String DEFAULT_CONFIG = "mule";

    // milliseconds
    private static final long MAX_WAIT_FOR_RESTART_REQUEST = 60000;

    // milliseconds
    private static final long MAX_DELAY_FOR_RESTART_REQUEST_POLL = 2000;

    /**
     * Server instance
     */
    public static OTMuleServer SERVER;

    /**
     * Flag used for tracing if the server is stopped or not
     */
    private static boolean SERVER_STOPPED;

    /**
     * Placeholder for restart requests
     */
    private final AtomicBoolean isRestartRequired = new AtomicBoolean(false);

    /**
     * Flag used for tracing if the server was restarted from the last restart request
     */
    private final AtomicBoolean isRestarted = new AtomicBoolean(false);

    static {
        OTMuleServer.OPTIONS.addOption("help", "help", false, "display this help and exit");
        final Option configFile = new Option("config", "config", true, "configuration files");
        configFile.setArgName("file");
        OTMuleServer.OPTIONS.addOption(configFile);

        final ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() {
                OTMuleServer.LOG.info("Restart server thread initialized");
                while (!OTMuleServer.SERVER_STOPPED) {
                    try {
                        OTMuleServer.LOG.debug("Checking for restart requests");
                        final OTMuleServer server = OTMuleServer.SERVER;
                        if (server == null) {
                            Thread.sleep(MAX_DELAY_FOR_RESTART_REQUEST_POLL);
                        } else {
                            AtomicBoolean restartServerFlag = server.getIsRestartRequired();
                            if (restartServerFlag.get()) {
                                server.setIsRestartRequired(false);
                                server.restart(server.getLastUsedConfigPaths());
                                server.setIsRestarted(true);
                            }
                            synchronized (restartServerFlag) {
                                restartServerFlag.wait(MAX_WAIT_FOR_RESTART_REQUEST);
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("Restart server thread encountered an error", e);
                    }
                }
                return null;
            }
        });
        executor.shutdown();
    }

    /**
     * Context of the server.
     */
    private MuleContext muleContext;

    /**
     * List of configuration paths used in starting the current server session
     */
    private List<URL> lastUsedConfigPaths;

    /**
     * Get the <code>muleContext</code> for the started server
     */
    public MuleContext getMuleContext() {
        return muleContext;
    }

    /**
     * Start the server using the specified configuration path.
     *
     * @param configURLs
     *            Configuration files.
     * @throws MuleException
     */
    public synchronized void start(
            final List<URL> configURLs) throws MuleException {
        if (muleContext == null || !muleContext.isStarted()) {
            deployNewUpgrade(configURLs);
            final DefaultMuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
            final List<ConfigResource> resources = new ArrayList<ConfigResource>();
            if (configURLs != null) {
                for (final URL url : configURLs) {
                    OTMuleServer.LOG.info("Adding url config: {}", url.getFile());
                    resources.add(new ConfigResource(url));
                }
            }
            final SpringXmlConfigurationBuilder configBuilder =
                    new SpringXmlConfigurationBuilder(resources.toArray(new ConfigResource[resources.size()]));
            muleContext = muleContextFactory.createMuleContext(configBuilder);
            muleContext.start();
        } else {
            OTMuleServer.LOG.warn("Mule context already started! Ignoring start command.");
        }
    }

    /**
     * Stop the server.
     */
    public synchronized void stop() {
        if (muleContext != null) {
            try {
                muleContext.stop();
            } catch (final MuleException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Restarts the mule server, the supplied <code>configURLs</code> list will be used as startup configuration
     * 
     * @param configURLs
     *            list of configuration paths
     * @throws MuleException
     *             thrown if mule encounters any error while initializing
     */
    public synchronized void restart(
            final List<URL> configURLs) throws MuleException {
        OTMuleServer.LOG.info("Restarting mule server");
        stop();
        while (!muleContext.isStopped() && muleContext.isStopping()) {
            OTMuleServer.LOG.info("Waiting for the server to completely shutdown ...");
            try {
                Thread.sleep(DELAY_BETWEEN_CHECKING_SERVER_STATUS);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        muleContext.dispose();
        OTMuleServer.LOG.info("Server stopped, trying to bring it up again");

        setLastUsedConfigPaths(configURLs);
        start(configURLs);
    }

    /**
     * @return Whether or not the context has been disposed of.
     *
     * @see org.mule.api.MuleContext#isDisposed()
     */
    public synchronized boolean isDisposed() {
        return muleContext != null && muleContext.isDisposed();
    }

    /**
     * @return Whether or not the context is currently disposing.
     *
     * @see org.mule.api.MuleContext#isDisposing()
     */
    public synchronized boolean isDisposing() {
        return muleContext != null && muleContext.isDisposing();
    }

    /**
     * @return Whether or not the context is initialised.
     *
     * @see org.mule.api.MuleContext#isInitialised()
     */
    public synchronized boolean isInitialised() {
        return muleContext != null && muleContext.isInitialised();
    }

    /**
     * @return Whether or not the context is currently initialising.
     *
     * @see org.mule.api.MuleContext#isInitialising()
     */
    public synchronized boolean isInitialising() {
        return muleContext != null && muleContext.isInitialising();
    }

    /**
     * @return Whether or not the context is started.
     *
     * @see org.mule.api.MuleContext#isStarted()
     */
    public synchronized boolean isStarted() {
        return muleContext != null && muleContext.isStarted();
    }

    /**
     * @param servernotificationlistener
     *            Listener to register.
     * @param resource
     *            Name of the resource the listener is interested in.
     *
     * @throws NotificationException
     *
     * @see org.mule.api.MuleContext#registerListener(org.mule.api.context.notification.ServerNotificationListener,
     *      java.lang.String)
     */
    public synchronized void registerListener(
            final ServerNotificationListener<? extends ServerNotification> servernotificationlistener,
            final String resource) throws NotificationException {
        if (muleContext != null) {
            muleContext.registerListener(servernotificationlistener, resource);
        }
    }

    /**
     * @param servernotificationlistener
     *            Listener to register.
     *
     * @throws NotificationException
     *
     * @see org.mule.api.MuleContext#registerListener(org.mule.api.context.notification.ServerNotificationListener)
     */
    public synchronized void registerListener(
            final ServerNotificationListener<? extends ServerNotification> servernotificationlistener)
            throws NotificationException {
        if (muleContext != null) {
            muleContext.registerListener(servernotificationlistener);
        }
    }

    /**
     * @param servernotificationlistener
     *            Listener to unregister.
     *
     * @see org.mule.api.MuleContext#unregisterListener(org.mule.api.context.notification.ServerNotificationListener)
     */
    public synchronized void unregisterListener(
            final ServerNotificationListener<? extends ServerNotification> servernotificationlistener) {
        if (muleContext != null) {
            muleContext.unregisterListener(servernotificationlistener);
        }
    }

    /**
     * Find configuration files in specified folder (all XML files are considered configuration).
     *
     * @param folderPath
     *            Folder to look into.
     * @return File paths, separated by semicolons.
     */
    public List<URL> findConfigFiles(
            final String folderPath) {
        final List<URL> urlList = new ArrayList<URL>();
        if (!StringUtils.isEmpty(folderPath)) {
            final File folder = new File(folderPath);
            if (folder.isDirectory()) {
                final Collection<File> paths = FileUtils.listFiles(folder, new String[] { "xml" }, true);
                for (final File path : paths) {
                    try {
                        urlList.add(path.toURI().toURL());
                    } catch (final MalformedURLException e) {
                        // Invalid path, ignore it
                    }
                }
            }
        }

        return urlList;
    }

    private void deployNewUpgrade(
            List<URL> configURLs) {
        File cacheFolder = new File(configURLs.get(0).getPath());
        File rootFolder = cacheFolder.getParentFile().getParentFile().getParentFile().getParentFile();
        String path = rootFolder.getAbsolutePath();
        path = path.replace("%20", " ");

        File cache = new File(path + "\\cache");
        OTMuleServer.LOG.warn("********************cache folder located at:" + cache.getAbsolutePath());

        if (cache.exists()) {
            OTMuleServer.LOG.warn("********************cache folder exists, to go copy sub folders");
            try {
                FileUtils.copyDirectory(cache, new File(path));
            } catch (Exception e) {
                OTMuleServer.LOG.error("Noe expected error occured when copying content under cache:" + e);
            }

            FileUtils.deleteQuietly(cache);
            OTMuleServer.LOG.warn("********************cache folder deleted!");
        }
    }

    /**
     * Main entry point.
     *
     * @param args
     *            Arguments, can specify a file, a pattern or a semicolon file list of configuration files or patterns
     *            for the mule server using -config option.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(
            final String... args) throws InterruptedException, ExecutionException {
        OTMuleServer.SERVER = new OTMuleServer();

        try {
            final HelpFormatter formatter = new HelpFormatter();
            final CommandLineParser parser = new GnuParser();
            CommandLine line = null;
            try {
                line = parser.parse(OTMuleServer.OPTIONS, args);
            } catch (final UnrecognizedOptionException e) {
                OTMuleServer.LOG.error(e.getMessage());
                System.err.println(e.getMessage());
                System.exit(1);
            }
            if (line.hasOption("help") || args.length == 0) {
                formatter.printHelp(OTMuleServer.LOG_NAME, OTMuleServer.OPTIONS);
            }
            String configPath = OTMuleServer.DEFAULT_CONFIG;
            if (line.hasOption("config")) {
                configPath = line.getOptionValue("config");
            }

            final List<URL> paths = OTMuleServer.SERVER.findConfigFiles(configPath);
            OTMuleServer.SERVER.setLastUsedConfigPaths(paths);
            OTMuleServer.SERVER.start(paths);
        } catch (final Throwable e) {
            OTMuleServer.LOG.error(null, e);
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * @return value of {@link #lastUsedConfigPaths}
     */
    public List<URL> getLastUsedConfigPaths() {
        return lastUsedConfigPaths;
    }

    /**
     * Set value for {@link #lastUsedConfigPaths}
     * 
     * @param lastUsedConfigPaths
     *            list of configuration paths used in starting the current server session
     */
    public void setLastUsedConfigPaths(
            final List<URL> lastUsedConfigPaths) {
        this.lastUsedConfigPaths = lastUsedConfigPaths;
    }

    /**
     * @return value of {@link #isRestartRequired}
     */
    public AtomicBoolean getIsRestartRequired() {
        return isRestartRequired;
    }

    /**
     * Set value for {@link #isRestartRequired}
     * 
     * @param isRestartRequired
     */
    public void setIsRestartRequired(
            final boolean isRestartRequired) {
        this.isRestartRequired.set(isRestartRequired);
        this.isRestarted.set(false);
        if (this.isRestartRequired.get() == true) {
            OTMuleServer.LOG.info("Server restart requested.");
            synchronized (this.isRestartRequired) {
                this.isRestartRequired.notifyAll();
            }
        }
    }

    /**
     * @return value of {@link #isRestarted}
     */
    public AtomicBoolean getIsRestarted() {
        return isRestarted;
    }

    /**
     * Set value for for {@link #isRestarted}
     * 
     * @param isRestarted
     */
    public void setIsRestarted(
            final boolean isRestarted) {
        this.isRestarted.set(isRestarted);
    }
}
