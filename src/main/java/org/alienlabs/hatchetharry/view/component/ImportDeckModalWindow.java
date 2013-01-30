package org.alienlabs.hatchetharry.view.component;

import java.io.UnsupportedEncodingException;

import org.alienlabs.hatchetharry.service.ImportDeckService;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
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

	public ImportDeckModalWindow(final ModalWindow _modal, final String id)
	{
		super(id);
		Injector.get().inject(this);

		final Form<String> form = new Form<String>("form")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				final FileUploadField textFile = (FileUploadField)this.get("deckFile");
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

				try
				{
					ImportDeckModalWindow.this.importDeckService.importDeck(
							new String(fupload.getBytes(), "UTF-8"),
							ImportDeckModalWindow.this.nameInput.getModelObject());
				}
				catch (final UnsupportedEncodingException e)
				{
					ImportDeckModalWindow.LOGGER.error("error parsing deck file", e);
					return;
				}
			}
		};

		final Label nameLabel = new Label("nameLabel", "Choose a name for the deck: ");
		final Model<String> nameModel = new Model<String>("");
		this.nameInput = new RequiredTextField<String>("name", nameModel);

		form.add(new FeedbackPanel("feedback"), new FileUploadField("deckFile"), nameLabel,
				this.nameInput);
		form.setMarkupId("inputForm").setOutputMarkupId(true);
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
