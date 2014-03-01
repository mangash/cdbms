package org.bgu.nlp.wicket.webapp;

import org.apache.wicket.Page;  
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.bgu.nlp.wicket.delete.WicketDelete;
import org.bgu.nlp.wicket.resource.IdDeleteResource;

public class WebAppDelete extends WebApplication {
	@Override 
	protected void init() {
		super.init();
		getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);
//		ResourceReference resourceDeleteReference = new ResourceReference("delete_sentences")
//		{
//			private static final long serialVersionUID = 3498864742720684244L;
//			
//			SearchResource deleteResource = new DeleteResource();
//			@Override
//			public IResource getResource() {
//				return deleteResource;
//			}
//		};
//		mountResource("/rest", resourceDeleteReference);
		
		ResourceReference resourceIdDeleteReference = new ResourceReference("delete_sentences_by_id")
		{
			

			/**
			 * 
			 */
			private static final long serialVersionUID = 5350268513530349373L;
			
			IdDeleteResource idDeleteResource = new IdDeleteResource();
			@Override
			public IResource getResource() {
				return idDeleteResource;
			}
		};
		mountResource("/id", resourceIdDeleteReference);
	}

	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEPLOYMENT;
	}

	@Override  
	public Class<? extends Page> getHomePage() {  
		//System.out.println("Requested WebAppSearch...");
		return WicketDelete.class;
	}
	
}
