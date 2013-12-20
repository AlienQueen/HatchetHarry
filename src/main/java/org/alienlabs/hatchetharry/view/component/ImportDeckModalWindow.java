package org.alienlabs.hatchetharry.view.component;

import java.io.UnsupportedEncodingException;

import org.alienlabs.hatchetharry.service.ImportDeckService;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ImportDeckModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	PersistenceService persistenceService;
	@SpringBean
	ImportDeckService importDeckService;

	static final Logger LOGGER = LoggerFactory.getLogger(ImportDeckModalWindow.class);

	RequiredTextField<String> nameInput;
	private final FileUploadField file;

	public ImportDeckModalWindow(final String id)
	{
		super(id);
		Injector.get().inject(this);

		final Form<Void> form = new Form<Void>("form")
		{
			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
			 */
			@Override
			protected void onSubmit()
			{
				final FileUploadField textFile = (FileUploadField)this.get("deckFile");

				ImportDeckModalWindow.LOGGER.info("trying to upload a deck");

				final FileUpload fupload = textFile.getFileUpload();
				if (fupload == null)
				{
					// No image was provided
					this.error("Please upload a .txt file.");
					return;
				}
				else if (fupload.getSize() == 0)
				{
					this.error("The text file you attempted to upload is empty.");
					return;
				}
				else if ((fupload.getClientFileName() == null)
						|| (fupload.getClientFileName().trim().equals(""))
						|| (!fupload.getClientFileName().endsWith(".txt")))
				{
					this.error("The text file name is invalid.");
					return;
				}

				ImportDeckModalWindow.LOGGER.info("uploading deck: "
						+ textFile.getFileUpload().getClientFileName());

				try
				{
					ImportDeckModalWindow.this.importDeckService.importDeck(
							new String(fupload.getBytes(), "UTF-8"),
							ImportDeckModalWindow.this.nameInput.getModelObject(), false);
				}
				catch (final UnsupportedEncodingException e)
				{
					ImportDeckModalWindow.LOGGER.error("error parsing deck file", e);
					return;
				}

				ImportDeckModalWindow.LOGGER.info("successfully added deck: "
						+ fupload.getClientFileName());
				throw new RestartResponseException(HomePage.class);
			}
		};

		form.add(new AjaxButton("submit")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> _form)
			{
				ImportDeckModalWindow.LOGGER.error("error uploading file!");
			}

		});

		final Label nameLabel = new Label("nameLabel", "Choose a name for the deck: ");
		final Model<String> nameModel = new Model<String>("");
		this.nameInput = new RequiredTextField<String>("name", nameModel);

		final Component feedback = new FeedbackPanel("feedback")
				.setOutputMarkupPlaceholderTag(true);
		form.add(feedback);

		form.add(this.file = new FileUploadField("deckFile"), nameLabel, this.nameInput);
		form.setMarkupId("inputForm").setOutputMarkupId(true);
		form.add(new UploadProgressBar("progress", form, this.file));
		form.setMaxSize(Bytes.kilobytes(5));
		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	@Required
	public void setImportDeckService(final ImportDeckService _importDeckService)
	{
		this.importDeckService = _importDeckService;
	}

}
