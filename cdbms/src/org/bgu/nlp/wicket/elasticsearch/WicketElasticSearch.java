package org.bgu.nlp.wicket.elasticsearch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
//import org.bgu.nlp.solr.DocumentAdder;
//import org.bgu.nlp.elasticsearch.DocumentSearcher;
import org.bgu.nlp.HebFNProperties;
import org.bgu.nlp.elasticsearch.ClientBuilder;
import org.bgu.nlp.elasticsearch.DocumentAdder;
import org.bgu.nlp.internal.format.YoavSentence;
import org.bgu.nlp.utils.Diversifyer;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;


public class WicketElasticSearch extends WebPage {

	private static final long serialVersionUID = 3969940745134363827L;

	public WicketElasticSearch()
	{

	}
}
