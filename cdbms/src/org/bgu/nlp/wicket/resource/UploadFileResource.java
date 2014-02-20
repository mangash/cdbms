package org.bgu.nlp.wicket.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.upload.FileItem;
import org.apache.wicket.util.upload.FileUploadException;
import org.bgu.nlp.elasticsearch.ClientBuilder;
import org.bgu.nlp.elasticsearch.DocumentAdder;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

public class UploadFileResource extends AbstractResource {


	private static final long serialVersionUID = 3027387275963912044L;

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		ServletWebRequest webRequest = (ServletWebRequest) attributes.getRequest();
		MultipartServletWebRequest multiPartRequest;
		try {
			multiPartRequest = webRequest.newMultipartWebRequest(Bytes.MAX, "ignored");
			Map<String, List<FileItem>> files = multiPartRequest.getFiles();
			System.out.println(files.toString());
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResourceResponse resourceResponse = new ResourceResponse();
		resourceResponse.setContentType("text/html");
		resourceResponse.setTextEncoding("UTF-8");

		resourceResponse.setWriteCallback(new WriteCallback()
		{
			@Override
			public void writeData(IResource.Attributes attributes) throws IOException
			{
				//	1.	get the POST request that was sent to this resource
				HttpServletRequest httpServletRequest = (HttpServletRequest) attributes.getRequest().getContainerRequest();
				httpServletRequest.setCharacterEncoding("UTF-8");

				//	2.	initialize a Http Response Writer. any data returned callback data
				//		should be written to this writer.
				OutputStream outputStream = attributes.getResponse().getOutputStream();
				Writer writer = new OutputStreamWriter( outputStream, Charset.forName("UTF-8"));

				//	3.	In case of a POST request, the request will be processed. 
				//		Otherwise, the the callback will ignore the body of the request,
				//		and state that the request should be a POST request.
				if (httpServletRequest.getMethod().equalsIgnoreCase("POST"))
				{
					//	3.1	Read the body of the request
					InputStream inputStream=httpServletRequest.getInputStream();
					BufferedReader requestBuffer=new BufferedReader(new InputStreamReader(inputStream,Charset.forName("UTF-8")));
					String lineInput= "";
					StringBuilder sb = new StringBuilder();
					while ((lineInput=requestBuffer.readLine())!=null)
					{
						sb.append(lineInput);
						sb.append("\n");
					}
					String sentences=sb.toString();
					writer.write("ok");
				}
				else
				{
					writer.write("only POST requests are allowed");
				}
				//	4.	Close the output stream writer when done writing
				writer.close();
			}

		});
		return resourceResponse;
	}
}