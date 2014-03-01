package org.bgu.nlp;

import java.io.File;

import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.bgu.nlp.jetty.handler.HelloHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class Main {
	/**
	 * @param args the arguments (if any) that are passed through command line
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception{
		//  0. Loading properties from external properties file
		HebFNProperties.init();
        //	1.	Creating a Jetty Server instance
		Server server = new Server();
		
        //	2.	Creating a Jetty Server Connector
		//		Connectors are the mechanism through which Jetty accepts network
		//		connections for various protocols.
		//		Configuring a connector is a combination of configuring the 
		//		following:
		//		a)	Network parameters on the connector itself (for example:
		//			the listening port).
		//		b)	Services the connector uses (for example: executors,
		//			schedulers).
		//		c)	Connection factories that instantiate and configure the
		//			protocol for an accepted connection.
		//		Fortunately, there is a default configuration so all we specify
		//		for now in the ServerConnector's constructor, which is the Server instance
		//		it's supposed to be used with.
		ServerConnector connector = new ServerConnector(server);
		
		//	3.	Set connector's port and idle time 
		connector.setPort(HebFNProperties.JETTY_PORT);
        connector.setIdleTimeout(HebFNProperties.JETTY_TIMEOUT);
        		
		//	4.	Creating a request log handler
		RequestLogHandler loghandler = new RequestLogHandler();
		
		//	5.	Creating a Servlet Holder
		//		This is the Object that holds, and configures the WicketServlet.  
        ServletHolder servletholder_demo = new ServletHolder(new WicketServlet()); 
        ServletHolder servletholder_add = new ServletHolder(new WicketServlet());
        ServletHolder servletholder_search = new ServletHolder(new WicketServlet());
        ServletHolder servletholder_delete = new ServletHolder(new WicketServlet());
        ServletHolder servletholder_elastic_search = new ServletHolder(new WicketServlet());
        ServletHolder servletholder_upload = new ServletHolder(new WicketServlet());
        
        //	6.	Creating a Web Application Context
        //		A Handler Wrapper is a handler base class that can be used to 
        //		daisy chain handlers together in the style of aspect-oriented programming.
		//		For example, a standard web application is implemented by a chain of
		//		a context, session, security and servlet handlers.
		WebAppContext webapp = new WebAppContext();  
        
        //	7.	Create a Default Handler
        //		Default Handler. This handle will deal with unhandled requests
        //		in the server. For requests for favicon.ico, the Jetty icon is
        //		served. For reqests to '/' a 404 with a list of known contexts
        //		is served. For all other requests a normal 404 is served.
        DefaultHandler defaulthandler = new DefaultHandler();
        
        //	8.	Create a basic handler that handles every request by returning a
        //		a Response of an "hello world" html file (for testing and 
        //		troubleshooting purposes).
        HelloHandler hellohandler= new HelloHandler();
        
        //	9.	Create a Handler Collection
        //		A Handler Collection holds a collection of other handlers 
        //		and calls each handler in order. This is useful for combining 
        //		statistics and logging handlers with the handler that generates
        //		the response.
        HandlerCollection handlercollection = new HandlerCollection();
        
        //	10.	Create a Handler List
        //		A Handler List is a Handler Collection that calls each handler in
        //		turn until either an exception is thrown, the response is committed
        //		or the request.isHandled() returns true. It can be used
        //		to combine handlers that conditionally handle a request. 
        HandlerList handlerlist=new HandlerList();
        
        //	11.	Set loghandler
        //		NCSARequestLog is a RequestLog implementation which outputs
        //		logs in the pseudo-standard NCSA common log format.
        loghandler.setRequestLog(new NCSARequestLog(File.createTempFile("hebfn_server_",".log").getAbsolutePath()));


        //	12.1.	Set servletholder_demo
        servletholder_demo.setInitParameter("applicationClassName",  
                org.bgu.nlp.wicket.webapp.WebAppDemo.class.getName());  
        servletholder_demo.setInitParameter(  
                WicketFilter.FILTER_MAPPING_PARAM, "/*"); 
        servletholder_demo.setInitOrder(1); 
        
        //	12.2.	Set servletholder_add
        servletholder_add.setInitParameter("applicationClassName",  
                org.bgu.nlp.wicket.webapp.WebAppAdd.class.getName());  
        servletholder_add.setInitParameter(  
                WicketFilter.FILTER_MAPPING_PARAM, "/add/*"); 
        servletholder_add.setInitOrder(2);
        
        //  12.3.	Set servletholder_search
        servletholder_search.setInitParameter("applicationClassName",  
                org.bgu.nlp.wicket.webapp.WebAppSearch.class.getName());  
        servletholder_search.setInitParameter(  
                WicketFilter.FILTER_MAPPING_PARAM, "/search/*"); 
        servletholder_search.setInitOrder(3);
        
        //  12.3.	Set servletholder_delete
        servletholder_delete.setInitParameter("applicationClassName",  
                org.bgu.nlp.wicket.webapp.WebAppDelete.class.getName());  
        servletholder_delete.setInitParameter(  
                WicketFilter.FILTER_MAPPING_PARAM, "/delete/*"); 
        servletholder_delete.setInitOrder(4);
        
        //  12.3.	Set servletholder_elastic_search
        servletholder_elastic_search.setInitParameter("applicationClassName",  
                org.bgu.nlp.wicket.webapp.WebAppElasticSearch.class.getName());  
        servletholder_elastic_search.setInitParameter(  
                WicketFilter.FILTER_MAPPING_PARAM, "/elasticsearch/*"); 
        servletholder_elastic_search.setInitOrder(5);
        
        //  12.4.	Set servletholder_upload
        servletholder_upload.setInitParameter("applicationClassName",  
                org.bgu.nlp.wicket.webapp.WebAppUpload.class.getName());  
        servletholder_upload.setInitParameter(  
                WicketFilter.FILTER_MAPPING_PARAM, "/upload/*"); 
        servletholder_upload.setInitOrder(6);
        
        //	13.	Set webapp
        //webapp.setContextPath("/*");			// URI path
        webapp.addServlet(servletholder_demo, "/*");
        webapp.addServlet(servletholder_add, "/add/*");
        webapp.addServlet(servletholder_search, "/search/*");
        webapp.addServlet(servletholder_delete, "/delete/*");
        webapp.addServlet(servletholder_elastic_search, "/elasticsearch/*");
        webapp.addServlet(servletholder_upload, "/upload/*");
        
        webapp.setResourceBase("./resources/");		// Web root directory.
        
        //	14.	Set handlerlist
        handlerlist.setHandlers(new Handler[]{webapp, defaulthandler});
        
        //	15.	Set handlercollection
        handlercollection.setHandlers(new Handler[]{handlerlist, loghandler});
        
        //	16.	Set the server
        server.setConnectors(new Connector[]{connector});
        server.setHandler(handlercollection);
        

        
        //	17.	Start server thread and bind it	to Main thread.
        server.start();

        server.join();
	}

}
