package org.bgu.nlp.wicket.search;

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


public class WicketSearch extends WebPage {

	private static final long serialVersionUID = 3969940745134363827L;

	public WicketSearch()
	{
		final IModel resultsModel=new Model();
		//	1.	Create the form component
		Form searchform=new Form("searchform");

		//	2.	Create the sentence component
		TextArea query=new TextArea("query",new Model());

		//  2.	Create the sentence component
		TextField hitnum=new TextField("hitnum",new Model());

		//	3.	Add the sentence component to the form component
		searchform.add(query);
		searchform.add(hitnum);
		query.setRequired(true);
		query.setOutputMarkupId(true);

		//	4. Create the results component
		final WebMarkupContainer resultsPanel = new WebMarkupContainer("resultsPanel");
		resultsPanel.setOutputMarkupId(true);

		final MultiLineLabel resultsLbl=new MultiLineLabel("resultsLbl",resultsModel);
		resultsLbl.setOutputMarkupId(true);

		resultsPanel.add(resultsLbl);

		//	5. Add the results component to the form component
		add(resultsPanel);
		resultsPanel.setVisible(true);

		//	6.	Create the submit component
		AjaxSubmitLink searchform_submit = new AjaxSubmitLink("searchform_submit", searchform) {

			private static final long serialVersionUID = 3579886629974583452L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				TextArea queryField=(TextArea)form.get("query");
				TextField hitNumField=(TextField)form.get("hitnum");
				String queryText=queryField.getInput();
				int hitsNum=Integer.parseInt(hitNumField.getInput());
				Client client = ClientBuilder.client();


				System.out.println("Attempting to search for sentence in database...");
				
				
				SearchResponse countResponse = client.prepareSearch(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME)
						.setTypes(HebFNProperties.ELASTIC_SEARCH_DOCUMENT_TYPE)
				        .setQuery(queryText.getBytes())
				        .execute()
				        .actionGet();
											
				int preDiversityHitsSize=Math.min(HebFNProperties.PRE_DIVERSIFICATION_MAX_QUERY_RESULTS, (int)countResponse.getHits().getTotalHits());
				
				SearchResponse  response= client.prepareSearch(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME)
						.setTypes(HebFNProperties.ELASTIC_SEARCH_DOCUMENT_TYPE)
						.setQuery(queryText)
						.setSize(preDiversityHitsSize)
						.setFrom(0)
						.execute()
						.actionGet();
				
				System.out.println ("requested a Pre-Diversification set of size: " + preDiversityHitsSize);
						
				SearchHit[] hits = response.getHits().getHits();
				List<SearchHit> hitsList = new LinkedList<SearchHit>(Arrays.asList(hits));
				System.out.println ("recieved a Pre-Diversification set of size: " + hitsList.size());
				StringBuilder sb=new StringBuilder();
				Diversifyer diversifyer=new Diversifyer(hitsNum);
				try {
					diversifyer.diversify(hitsList);
					System.out.println("num of results after diversification: " + hitsList.size());
					for (SearchHit hit : hitsList)
					{
						YoavSentence sentence=DocumentAdder.searchHitToSentence(hit);
						sb.append(sentence.toJson(true));
						sb.append("\n");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally
				{
					resultsModel.setObject(sb.toString());
					target.add(resultsPanel);
				}
			}
		};
		//	7.	Add the submit component to the form component
		searchform.add(searchform_submit);

		//	8.	Add the form component to the web page
		add(searchform);
	}
}
