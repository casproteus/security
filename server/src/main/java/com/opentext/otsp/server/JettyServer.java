package com.opentext.otsp.server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.Lifecycle;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

public class JettyServer implements Runnable {
	private static final Log LOG = LogFactory.getLog(JettyServer.class);
	private static final String CONTEXT = "classpath:/server.xml";
	private static final String BEAN = "tme";
	private final Lock m_lock = new ReentrantLock();
	private final Condition m_shutdown = m_lock.newCondition();
	private final Condition m_terminated = m_lock.newCondition();
	private BeanFactoryReference m_factoryReference;
	private boolean m_active;

	public JettyServer() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				terminate();
			}
		});
	}

	@ManagedAttribute(description = "Server status.")
	public boolean isRunning() {
		return m_factoryReference != null && ((Lifecycle) m_factoryReference.getFactory()).isRunning();
	}

	@ManagedOperation(description = "Start server.")
	public void start() {
		m_lock.lock();
		try {
			if (isRunning()) {
				LOG.info("Server is already running.");
				return;
			}
			LOG.info("Server is starting...");
			m_factoryReference = ContextSingletonBeanFactoryLocator.getInstance(CONTEXT).useBeanFactory(BEAN);
			((Lifecycle) m_factoryReference.getFactory()).start();
			LOG.info("Server is started.");
		} finally {
			m_lock.unlock();
		}
	}

	@ManagedOperation(description = "Stop server.")
	public void stop() {
		m_lock.lock();
		try {
			if (!isRunning()) {
				LOG.info("Server is not running.");
				return;
			}
			LOG.info("Server is stopping...");
			((Lifecycle) m_factoryReference.getFactory()).stop();
			m_factoryReference.release();
			LOG.info("Server is stopped.");
		} finally {
			m_lock.unlock();
		}
	}

	@ManagedOperation(description = "Restart server.")
	public void restart() {
		stop();
		start();
	}

	@ManagedOperation(description = "Terminate server.")
	public void terminate() {
		m_lock.lock();
		try {
			if (m_active) {
				m_shutdown.signalAll();
				m_terminated.awaitUninterruptibly();
			}
		} finally {
			m_lock.unlock();
		}
	}

	@Override
	public void run() {
		m_lock.lock();
		try {
			m_active = true;
			start();
			try {
				m_shutdown.await();
			} catch (InterruptedException e) {
			}
			stop();
		} catch (Throwable e) {
			LOG.error("JettyServer start failed", e);
		} finally {
			LOG.info("Server is terminated.");
			m_terminated.signalAll();
			m_active = false;
			m_lock.unlock();
		}
	}
	

	public static void main(String[] args) {
		try {
			Thread server = new Thread(new FileSystemXmlApplicationContext("classpath:/com/nstein/tme/server/main.xml").getBean("server", Runnable.class), "server");
			server.start();
			server.join();
		} catch (Throwable e) {
			LOG.error("Jetty server start failed.", e);
			System.exit(1);
		}
		System.exit(0);
	}
}
