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
import org.cas.client.platform.bar.dialog.BarFrame;
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

//This class is the main entry point for OTSP running in Mule.
public class OTMuleServer {

    private static final Logger LOG = LoggerFactory.getLogger("SERVER");

    private static final int DELAY_BETWEEN_CHECKING_SERVER_STATUS = 1000;    // Time in milliseconds. Used for delaying the next poll on server status

    private static final long MAX_WAIT_FOR_RESTART_REQUEST = 60000;    // milliseconds

    private static final long MAX_DELAY_FOR_RESTART_REQUEST_POLL = 2000;    // milliseconds

    private static final String DEFAULT_CONFIG = "mule";    // Default configuration folder name.

    public static OTMuleServer instance;

    private static boolean SERVER_STOPPED;    // Flag used for tracing if the server is stopped or not

    private final AtomicBoolean isRestartRequired = new AtomicBoolean(false);    // Placeholder for restart requests

    private final AtomicBoolean isRestarted = new AtomicBoolean(false);    // Flag used for tracing if the server was restarted from the last restart request

    private MuleContext muleContext;    // Context of the server.

    private List<URL> lastUsedConfigPaths;    //List of configuration paths used in starting the current server session

    private static Options OPTIONS = new Options();

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
                        final OTMuleServer server = OTMuleServer.instance;
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
        OTMuleServer.instance = new OTMuleServer();

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
                formatter.printHelp("SERVER", OTMuleServer.OPTIONS);
            }
            String configPath = OTMuleServer.DEFAULT_CONFIG;
            if (line.hasOption("config")) {
                configPath = line.getOptionValue("config");
            }

            final List<URL> paths = OTMuleServer.instance.findConfigFiles(configPath);
            OTMuleServer.instance.setLastUsedConfigPaths(paths);
            OTMuleServer.instance.start(paths);
        } catch (final Throwable e) {
            OTMuleServer.LOG.error(null, e);
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
	public synchronized void start(final List<URL> configURLs) throws MuleException {
		if(BarFrame.instance == null || !BarFrame.instance.isVisible()) {
			Thread a = new Thread() {
				@Override
				public void run() {
		            BarFrame.main(null);
				}
			};
			a.start();
		}
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

    public synchronized boolean isDisposed() {
        return muleContext != null && muleContext.isDisposed();
    }

    public synchronized boolean isDisposing() {
        return muleContext != null && muleContext.isDisposing();
    }

    public synchronized boolean isInitialised() {
        return muleContext != null && muleContext.isInitialised();
    }

    public synchronized boolean isInitialising() {
        return muleContext != null && muleContext.isInitialising();
    }

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
    public List<URL> findConfigFiles( final String folderPath) {
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

    private void deployNewUpgrade( List<URL> configURLs) {
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

    public List<URL> getLastUsedConfigPaths() {
        return lastUsedConfigPaths;
    }

    public void setLastUsedConfigPaths( final List<URL> lastUsedConfigPaths) {
        this.lastUsedConfigPaths = lastUsedConfigPaths;
    }

    public AtomicBoolean getIsRestartRequired() {
        return isRestartRequired;
    }

    public void setIsRestartRequired( final boolean isRestartRequired) {
        this.isRestartRequired.set(isRestartRequired);
        this.isRestarted.set(false);
        if (this.isRestartRequired.get() == true) {
            OTMuleServer.LOG.info("Server restart requested.");
            synchronized (this.isRestartRequired) {
                this.isRestartRequired.notifyAll();
            }
        }
    }

    public AtomicBoolean getIsRestarted() {
        return isRestarted;
    }

    public MuleContext getMuleContext() {
        return muleContext;
    }

    public void setIsRestarted( final boolean isRestarted) {
        this.isRestarted.set(isRestarted);
    }
}
