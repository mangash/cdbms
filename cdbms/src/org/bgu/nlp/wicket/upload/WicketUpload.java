package org.bgu.nlp.wicket.upload;

import java.io.File;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Duration;
import org.bgu.nlp.HebFNProperties;
import org.bgu.nlp.wicket.model.ProgressModel;
import org.bgu.nlp.wicket.upload.task.UploadTask;
 
public class WicketUpload extends WebPage {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -6324245348186973266L;
	private FileUploadField fileUpload;
 
	public WicketUpload(final PageParameters parameters) {
		

		Form<?> form = new Form<Void>("form");
		
		// Enable multipart mode (need for uploads file)
		form.setMultiPart(true);
 
		// set max upload size to 10 gigabytes
		form.setMaxSize(Bytes.megabytes(HebFNProperties.MAX_UPLOAD_SIZE));
		
		//	Create the upload component
		fileUpload = new FileUploadField("fileUpload");
		fileUpload.setOutputMarkupId(true);
		
        final ProgressModel pm = new ProgressModel ();
        final MultiLineLabel statusLbl=new MultiLineLabel("statusLbl", pm);
        statusLbl.setOutputMarkupId (true);
        
		//	Create the genre component
		TextField genre=new TextField("genre",new Model(HebFNProperties.DEFAULT_SENTENCE_GENRE));
		genre.setRequired(true);
		genre.setOutputMarkupId(true);
		
		
		//	Create the startLine component
		TextField startLine=new TextField("startline",new Model("1"));
		startLine.setRequired(true);
		startLine.setOutputMarkupId(true);
		
		//	Create the dump checkbox component component
		final CheckBox doDump = new CheckBox("dump", Model.of(Boolean.FALSE));
        
		//	Create the submit component
		AjaxSubmitLink form_submit = new AjaxSubmitLink("submit", form) {

			private static final long serialVersionUID = -8108665852803417754L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				//String basePath=((WebApplication)WebApplication.get()).getServletContext().getRealPath(".");
				pm.setObject("Uploading file...");
				final FileUpload uploadedFile = fileUpload.getFileUpload();
				
				if (uploadedFile != null) {
					try {
					    pm.setObject("");
					    TextField genre=(TextField)form.get("genre");
					    TextField startLine=(TextField)form.get("startline");
					    CheckBox doDump=(CheckBox)form.get("dump");
						//System.out.println("Attempting to upload file");
					    File targetFile=new File(HebFNProperties.UPLOAD_PATH, uploadedFile.getClientFileName().toLowerCase());
					    File dumpFile=new File(HebFNProperties.UPLOAD_PATH, "dump_"+uploadedFile.getClientFileName().toLowerCase());
					    uploadedFile.writeTo(targetFile);
					    UploadTask task = new UploadTask (pm,targetFile,dumpFile,new String(genre.getValue()),Long.parseLong(startLine.getValue()),doDump.getModelObject());
					    //System.out.println("Attempting to add contents to database...");
					    Thread thread = new Thread (task);
					    thread.start ();
					} catch (Exception e) {
						throw new IllegalStateException("Error processing file");
					}
				 }
			}
		};
		
		form.add(fileUpload);
		form.add(genre);
		form.add(startLine);
		form.add(doDump);
		
		//	7.	Add the submit component to the form component
		form.add(form_submit);
		
		form.add(new UploadProgressBar("progress", form,
	            fileUpload));
		
		add(statusLbl);
		statusLbl.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
		
	     // Create feedback panels
		
        final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");
        uploadFeedback.setOutputMarkupId(true);
        
       // Add uploadFeedback to the form
        add(uploadFeedback);  
        
        form.setOutputMarkupId(true);
		add(form);
		
	}
}