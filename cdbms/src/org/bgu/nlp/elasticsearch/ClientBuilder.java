package org.bgu.nlp.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;

import org.bgu.nlp.HebFNProperties;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class ClientBuilder {
	private static TransportClient client=null;
	public static TransportClient client()
	{
		//		1.	Create a ElasticSearch embedded node
		if (client==null)
		{
			System.out.println("initializing ES client");
			Settings clientSettings = ImmutableSettings.settingsBuilder()
					.put("cluster.name", HebFNProperties.ELASTIC_SEARCH_CLUSTER_NAME)
					.put("path.home",System.getProperty("user.home")+"/resources/elasticsearch")
					.put("number_of_shards",0)
					.put("number_of_replicas",0)
					.put("node.data", "false").build();

			client=new TransportClient(clientSettings)
			.addTransportAddress(new InetSocketTransportAddress(HebFNProperties.ELASTIC_SEARCH_MASTER_NODE_URL, HebFNProperties.ELASTIC_SEARCH_COMMUNICATION_PORT));
		}
		boolean hasIndex = client.admin().indices()
				.exists(new IndicesExistsRequest(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME))
				.actionGet()
				.isExists();
		if (!hasIndex)
		{
			try {
				Settings indexSettings = ImmutableSettings.settingsBuilder().loadFromSource(jsonBuilder()
		                .startObject()
		                    .startObject("analysis")
		                        .startObject("tokenizer")
		                        	.startObject("line_tokenizer")
			                        	.field("type", "pattern")
			                        	.field("pattern","\\r?\\n")
			                        .endObject()
		                        .endObject()
		                        .startObject("analyzer")
		                            .startObject("line_analyzer")
		                                .field("type", "custom")
		                                .field("tokenizer","line_tokenizer")
		                            .endObject()
		                        .endObject()
		                    .endObject()
		                .endObject().string()).build();

				CreateIndexRequestBuilder createIndexRequestBuilder = client.admin()
						.indices()
						.prepareCreate(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME)
						.setSettings(indexSettings);
				CreateIndexResponse response = createIndexRequestBuilder.execute().actionGet();
				System.out.println(response.isAcknowledged());
			}
			catch (IOException e)
			{
				return null;
			}
		}
		//		2.	return a ElasticSearch client
		return client;
	}
}
