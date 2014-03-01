package org.bgu.nlp.wicket.webapp;

import org.apache.wicket.Page;  
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.core.request.mapper.CryptoMapper;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.bgu.nlp.wicket.add.WicketAdd;
import org.bgu.nlp.wicket.resource.AddSentenceResource;

public class WebAppAdd extends WebApplication {

	@Override
	protected void init() {
		super.init();
		setRootRequestMapper(new CryptoMapper(getRootRequestMapper(), this));
		getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);
		ResourceReference resourceAddReference = new ResourceReference("post_add_sentence")
		{
			private static final long serialVersionUID = 3419740636374092775L;
			
			AddSentenceResource addSentenceResource = new AddSentenceResource();
			@Override
			public IResource getResource() {
				//Call here to the code that translate the sentence
				return addSentenceResource;
			}
		};
		mountResource("/rest", resourceAddReference);
	}

	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEPLOYMENT;
	}

	@Override
	public Class<? extends Page> getHomePage() {
		//System.out.println("Requested WebAppAdd...");
		return WicketAdd.class;
	} 
}