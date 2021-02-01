package com.qa.app;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qa.app.configuration.Configuration;
import com.qa.app.configuration.ConfigurationManager;
import com.qa.app.configuration.HttpConfigurationException;
import com.qa.app.core.ServerListenerThread;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

public class Server implements Runnable {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);

	private ConfigurationManager configurationManager;
	private Configuration configuration;
	private ServerListenerThread serverListenerThread;
	private String configurationFileLocation;
	private final String RESOURCE_DIR = "src/main/resources";
	
	public Server() {
		super();
	}
	
	public Server(String configurationFileLocation) {
		super();
		this.configurationFileLocation = configurationFileLocation;
	}

	@Override
	public void run() {
		try {
			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			StatusPrinter.print(lc);
			
			LOGGER.info("Server starting...");
			loadConfiguration();
			
			LOGGER.info("USING PORT: " + configuration.getPort());
			LOGGER.info("USING VIEWS LOCATION: " + configuration.getViewsLocation());
			
			loadServer();
			
		} catch (HttpConfigurationException | IOException e) {
			LOGGER.error("Server failed to start.");
			e.printStackTrace();
		}
	}

	private void loadServer() throws IOException {
		serverListenerThread = new ServerListenerThread(configuration.getPort(),
										 				configuration.getViewsLocation());
		//serverListenerThread.setDaemon(true);
		serverListenerThread.start();
//		serverListenerThread.join();
	}

	private void loadConfiguration() throws HttpConfigurationException {
		configurationManager = ConfigurationManager.getInstance();
		
		// Load and get config file
		if (configurationFileLocation == null || configurationFileLocation.isEmpty()) {
			configurationManager.loadConfigurationFile(RESOURCE_DIR + "/http.json");
			LOGGER.info("Loaded configuration from: " + RESOURCE_DIR + configurationFileLocation);
		} else {
			configurationManager.loadConfigurationFile(RESOURCE_DIR + configurationFileLocation);
			LOGGER.info("Loaded configuration from: " + RESOURCE_DIR + configurationFileLocation);
		}
		
		configuration = configurationManager.getCurrentConfiguration();
	}
}
