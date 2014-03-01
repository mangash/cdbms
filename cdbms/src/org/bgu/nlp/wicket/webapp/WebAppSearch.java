package org.bgu.nlp.wicket.webapp;

import org.apache.wicket.Page;  
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.bgu.nlp.wicket.resource.IdSearchResource;
import org.bgu.nlp.wicket.resource.SearchResource;
import org.bgu.nlp.wicket.search.WicketSearch;

public class WebAppSearch extends WebApplication {
	@Override 
	protected void init() {
		super.init();
		getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);
		ResourceReference resourceSearchReference = new ResourceReference("search_for_sentences")
		{
			private static final long serialVersionUID = 3498864742720684244L;
			
			SearchResource searchResource = new SearchResource();
			@Override
			public IResource getResource() {
				return searchResource;
			}
		};
		mountResource("/rest", resourceSearchReference);
		
		ResourceReference resourceIdSearchReference = new ResourceReference("search_by_id_for_sentences")
		{
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 9182626178094883254L;
			
			IdSearchResource idSearchResource = new IdSearchResource();
			@Override
			public IResource getResource() {
				return idSearchResource;
			}
		};
		mountResource("/id", resourceIdSearchReference);
	}

	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEPLOYMENT;
	}

	@Override  
	public Class<? extends Page> getHomePage() {  
		//System.out.println("Requested WebAppSearch...");
		return WicketSearch.class;  
	}
	
}
