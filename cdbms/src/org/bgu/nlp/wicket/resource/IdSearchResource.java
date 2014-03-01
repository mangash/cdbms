package org.bgu.nlp.wicket.resource;

import org.apache.wicket.request.resource.AbstractResource;
import org.bgu.nlp.wicket.callback.IdSearchCallback;

public class IdSearchResource extends AbstractResource {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3477430275014414588L;

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		ResourceResponse resourceResponse = new ResourceResponse();
		resourceResponse.setContentType("application/json");
		resourceResponse.setTextEncoding("UTF-8");
		resourceResponse.setWriteCallback(new IdSearchCallback());
		return resourceResponse;
	}

}
