package org.bgu.nlp.wicket.demo;

//import org.apache.wicket.bootstrap.Bootstrap;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.resource.CssResourceReference;




public class WicketDemo extends WebPage {
	@Override
	public void renderHead(IHeaderResponse response) {
	    //super.renderHead(response);
	    //Bootstrap.renderHeadPlain(response);
	}
	
	public WicketDemo()
	{
		//Label label=new Label("message","hello world!");
		//add(label);
	}
}
