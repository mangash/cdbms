package org.bgu.nlp.wicket.resource;

import org.apache.wicket.request.resource.AbstractResource;
import org.bgu.nlp.wicket.callback.SearchCallback;

public class SearchResource extends AbstractResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8208899864295572516L;

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		ResourceResponse resourceResponse = new ResourceResponse();
		resourceResponse.setContentType("application/json");
		resourceResponse.setTextEncoding("UTF-8");
		resourceResponse.setWriteCallback(new SearchCallback());
		return resourceResponse;
	}

}
