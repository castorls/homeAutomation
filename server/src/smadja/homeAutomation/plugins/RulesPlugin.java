package smadja.homeAutomation.plugins;

import groovy.util.GroovyScriptEngine;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.plugins.rules.GenericRule;
import smadja.homeAutomation.plugins.rules.GroovyRule;
import smadja.homeAutomation.plugins.rules.Rule;
import smadja.homeAutomation.server.Plugin;
import smadja.homeAutomation.server.Server;

public class RulesPlugin extends Plugin {

	private static Logger logger = Logger.getLogger(RulesPlugin.class);
	private List<Rule> rulesList = new ArrayList<Rule>();
	private GroovyScriptEngine gse = null;

	public RulesPlugin(String id, Server server) {
		super(id, server);
	}

	@Override
	public boolean onMessage(Message msg, boolean shouldAcknowledge) {
		if (msg.isTransientFlag()) {
			return false;
		}
		Server server = getServer();
		try {
			for (Rule rule : rulesList) {
				rule.process(msg, server);
			}
		} catch (HomeAutomationException e) {
			logger.warn(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public void init(File pluginDirectory, Properties confProp) throws HomeAutomationException {
		String rulesDirectory = confProp.getProperty("rulesDirectory");
		if(rulesDirectory == null || "".equals(rulesDirectory.trim())){
			throw new InvalidParameterException("rulesDirectory property must not be empty.");
		}
		File rulesDirectoryFile = new File(pluginDirectory, rulesDirectory);
		if (!rulesDirectoryFile.exists() || !rulesDirectoryFile.isDirectory()) {
			throw new InvalidParameterException("rulesDirectory '" + rulesDirectory + "' is not a valid directory.");
		}
		String rulesLibDirectory = confProp.getProperty("rulesLibDirectory");
		if(rulesLibDirectory == null || "".equals(rulesLibDirectory.trim())){
			throw new InvalidParameterException("rulesLibDirectory property must not be empty.");
		}
		File rulesLibDirectoryFile = new File(pluginDirectory, rulesLibDirectory);
		if (!rulesLibDirectoryFile.exists() || !rulesLibDirectoryFile.isDirectory()) {
			throw new InvalidParameterException("rulesDirectory '" + rulesLibDirectory + "' is not a valid directory.");
		}
		// init groovy engine
		String[] roots = new String[] { rulesDirectoryFile.getAbsolutePath(), rulesLibDirectoryFile.getAbsolutePath() };
		try {
			gse = new GroovyScriptEngine(roots);
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
			throw new HomeAutomationException("Cannot initialize the groovy script engine");
		}
		readRules(rulesDirectoryFile);
	}

	@Override
	public void start() {
		// nothing to do
	}

	@Override
	public void stop() {
		// nothing to do

	}

	private void readRules(File rulesDirectory) throws HomeAutomationException {
		File[] files = rulesDirectory.listFiles();
		if (files == null || files.length == 0) {
			logger.warn("Rules directory contains no rules ! this plugin will not be fully functionnal.");
			return;
		}
		for (File child : files) {
			logger.info("Reading rule '" + child.getName() + "'");
			Rule rule = null;
			if (child.getName().endsWith(".groovy")) {
				rule = createGroovyRule(child);
			} else if (child.getName().endsWith(".rule")) {
				rule = createGenericRule(child);
			} else {
				logger.warn("Unknown rule file '" + child.getName() + "'. It will be ignored.");
			}
			if (rule != null) {
				rulesList.add(rule);
			} else {
				logger.warn("Invalid created rule. it will be ignored.");
			}
		}
	}

	private Rule createGenericRule(File child) throws HomeAutomationException {
		return new GenericRule(child);
	}

	private Rule createGroovyRule(File child) throws HomeAutomationException {
		return new GroovyRule(child, gse);
	}

}
