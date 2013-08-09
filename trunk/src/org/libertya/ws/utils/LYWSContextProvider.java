package org.libertya.ws.utils;


import java.util.Properties;

import net.sf.cglib.proxy.Enhancer;

import org.openXpertya.util.ContextProvider;

/**
 * 
 * @author Low Heng Sin
 *
 */
public class LYWSContextProvider implements ContextProvider {

	private final static ServerContextCallback callback = new ServerContextCallback();
	private final static Properties context = (Properties) Enhancer.create(Properties.class, callback);
	
	/**
	 * Get server context proxy
	 */
	public Properties getContext() {
		return context;
	}

	/**
	 * Show url at zk desktop
	 */
	public void showURL(String url) {
//		SessionManager.getAppDesktop().showURL(url,true);
	}	 
}

