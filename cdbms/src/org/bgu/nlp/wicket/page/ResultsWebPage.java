package org.bgu.nlp.wicket.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ResultsWebPage extends WebPage{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 773164185190732290L;

	public ResultsWebPage()
	{
		
	}
	
	public ResultsWebPage(String method, String encoding, String text)
	{
		TextRequestHandler textRequestHandler = new TextRequestHandler(method, encoding, text);
		RequestCycle.get().scheduleRequestHandlerAfterCurrent(textRequestHandler); 
	}
	
	public ResultsWebPage(PageParameters params)
	{
		TextRequestHandler textRequestHandler = new TextRequestHandler(params.get("mime").toString(), params.get("encoding").toString(), params.get("text").toString());
		RequestCycle.get().scheduleRequestHandlerAfterCurrent(textRequestHandler); 
	}
}
