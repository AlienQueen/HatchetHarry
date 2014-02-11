package org.alienlabs.hatchetharry.view.component;

import java.io.UnsupportedEncodingException;

import org.alienlabs.hatchetharry.service.ImportDeckService;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ImportDeckDialog extends Panel
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	PersistenceService persistenceService;
	@SpringBean
	ImportDeckService importDeckService;

	static final Logger LOGGER = LoggerFactory.getLogger(ImportDeckDialog.class);

	RequiredTextField<String> nameInput;
	final FileUploadField file;

	public ImportDeckDialog(final String id)
	{
		super(id);
		Injector.get().inject(this);

		final Form<Void> form = new Form<Void>("form");
		this.file = new FileUploadField("deckFile");

		this.file.add(new AjaxFormSubmitBehavior(form, "onchange")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target)
			{
				ImportDeckDialog.LOGGER.info("trying to upload a deck");

				if ((ImportDeckDialog.this.nameInput.getModelObject() == null)
						|| "".equals(ImportDeckDialog.this.nameInput.getModelObject()))
				{
					return;
				}

				final FileUpload fupload = ImportDeckDialog.this.file.getFileUpload();
				if (fupload == null)
				{
					// No image was provided
					return;
				}
				else if (fupload.getSize() == 0)
				{
					return;
				}
				else if ((fupload.getClientFileName() == null)
						|| (fupload.getClientFileName().trim().equals(""))
						|| (!fupload.getClientFileName().endsWith(".txt")))
				{
					return;
				}

				ImportDeckDialog.LOGGER.info("uploading deck: "
						+ ImportDeckDialog.this.file.getFileUpload().getClientFileName());

				try
				{
					ImportDeckDialog.this.importDeckService.importDeck(
							new String(fupload.getBytes(), "UTF-8"),
							ImportDeckDialog.this.nameInput.getModelObject(), false);
				}
				catch (final UnsupportedEncodingException e)
				{
					return;
				}

				ImportDeckDialog.LOGGER.info("successfully added deck: "
						+ fupload.getClientFileName());
			}
		});

		final Label nameLabel = new Label("nameLabel", "Choose a name for the deck: ");
		final Model<String> nameModel = new Model<String>("");
		this.nameInput = new RequiredTextField<String>("name", nameModel);

		form.setMarkupId("inputForm").setOutputMarkupId(true);
		form.setMaxSize(Bytes.kilobytes(5));
		form.setMultiPart(true);

		form.add(this.file, nameLabel, this.nameInput);
		this.add(form);

		final IndicatingAjaxButton close = new IndicatingAjaxButton("close")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				super.onSubmit(target, _form);
				target.appendJavaScript("jQuery('#importDeck').dialog('close');");
			}
		};

		form.add(close);
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
