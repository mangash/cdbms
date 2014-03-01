package org.bgu.nlp.wicket.webapp;

import org.apache.wicket.Page;  
//import org.apache.wicket.bootstrap.Bootstrap;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.bgu.nlp.wicket.demo.WicketDemo;

public class WebAppDemo extends WebApplication {
	@Override 
	protected void init() {
		getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);
	}
	@Override  
	public Class<? extends Page> getHomePage() {  
		//System.out.println("Requested WebAppDemo...");
		return WicketDemo.class;  
	} 
}
