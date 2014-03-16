package smadja.homeAutomation.plugins.rules;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.server.Server;

public class GroovyRule extends Rule {

	private static Logger logger = Logger.getLogger(GroovyRule.class);
	private File groovyFile = null;
	private GroovyScriptEngine gse = null;
	
	public GroovyRule(File groovyFile, GroovyScriptEngine gse) {
		super();
		this.groovyFile = groovyFile;
		this.gse = gse;
	}

	@Override
	public void process(Message msg, Server server) throws HomeAutomationException {
		Binding binding = new Binding();
		binding.setVariable("msg", msg);
		binding.setVariable("server", server);
		try {
			gse.run(this.groovyFile.getAbsolutePath(), binding);
		} catch (ResourceException | ScriptException e) {
			logger.warn(e.getMessage(), e);
			throw new HomeAutomationException("Cannot execute rule "+this.groovyFile.getName());
		}
	}

}
