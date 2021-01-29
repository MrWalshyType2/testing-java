package com.qa.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qa.app.configuration.Configuration;
import com.qa.app.configuration.ConfigurationManager;
import com.qa.app.configuration.HttpConfigurationException;
import com.qa.app.core.ServerListenerThread;

public class Runner {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Runner.class);

	public static void main(String[] args) throws IOException, HttpConfigurationException, InterruptedException {
		Server server = new Server();
		server.run();
	}
}
