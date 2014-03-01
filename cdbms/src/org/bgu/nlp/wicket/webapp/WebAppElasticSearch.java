package org.bgu.nlp.wicket.webapp;

import org.apache.wicket.Page;  
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.bgu.nlp.wicket.elasticsearch.WicketElasticSearch;
import org.bgu.nlp.wicket.resource.ElasticSearchResource;
import org.bgu.nlp.wicket.resource.SearchResource;
import org.bgu.nlp.wicket.search.WicketSearch;

public class WebAppElasticSearch extends WebApplication {
	@Override 
	protected void init() {
		super.init();
		getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);
		ResourceReference resourceElasticSearchReference = new ResourceReference("elastic_search_for_sentences")
		{
			private static final long serialVersionUID = 3498864742720684244L;
			
			ElasticSearchResource elasticSearchResource = new ElasticSearchResource();
			@Override
			public IResource getResource() {
				return elasticSearchResource;
			}
		};
		mountResource("/rest", resourceElasticSearchReference);
	}

	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEPLOYMENT;
	}

	@Override  
	public Class<? extends Page> getHomePage() {  
		//System.out.println("Requested WebAppElasticSearch...");
		return WicketElasticSearch.class;  
	}
	
}
