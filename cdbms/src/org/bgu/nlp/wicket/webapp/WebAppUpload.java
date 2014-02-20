package org.bgu.nlp.wicket.webapp;

import org.apache.wicket.Page;  
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.bgu.nlp.wicket.resource.UploadFileResource;
import org.bgu.nlp.wicket.upload.WicketUpload;

public class WebAppUpload extends WebApplication {
	@Override
	protected void init() {
		super.init();
		getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);
		getApplicationSettings().setUploadProgressUpdatesEnabled(true);
		ResourceReference resourceFileReference = new ResourceReference("add_sentences_from_file")
		{
			private static final long serialVersionUID = -4626839523832330309L;
			
			UploadFileResource uploadFileResource = new UploadFileResource();
			@Override
			public IResource getResource() {
				//Call here to the code that translate the sentence
				return uploadFileResource;
			}
		};
		mountResource("/rest", resourceFileReference);
	}

	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEPLOYMENT;
	}

	@Override
	public Class<? extends Page> getHomePage() {
		//System.out.println("Requested WebAppUpload...");
		return WicketUpload.class;
	} 
}