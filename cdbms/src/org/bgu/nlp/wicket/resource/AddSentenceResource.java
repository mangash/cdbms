package org.bgu.nlp.wicket.resource;

import org.apache.wicket.request.resource.AbstractResource;
import org.bgu.nlp.wicket.callback.AddCallback;

public class AddSentenceResource extends AbstractResource {

	private static final long serialVersionUID = -6743556816558455806L;

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		ResourceResponse resourceResponse = new ResourceResponse();
		resourceResponse.setContentType("application/json");
		resourceResponse.setTextEncoding("UTF-8");
		resourceResponse.setWriteCallback(new AddCallback());
		return resourceResponse;
	}
}