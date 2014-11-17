package org.alienlabs.hatchetharry.view.component.gui;

import java.io.UnsupportedEncodingException;

import org.alienlabs.hatchetharry.service.ImportDeckService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class ImportDeckDialog extends Panel
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportDeckDialog.class);
	private static final long serialVersionUID = 1L;
	private final FileUploadField file;
	private final WebMarkupContainer parent;

	@SpringBean
	private ImportDeckService importDeckService;

	private final TextField<String> nameInput;

	public ImportDeckDialog(final String id)
	{
		super(id);
		Injector.get().inject(this);

		final Form<Void> form = new Form<Void>("form");
		this.file = new FileUploadField("deckFile");

		final Label nameLabel = new Label("nameLabel", "Choose a name for the deck: ");
		final Model<String> nameModel = new Model<String>("");
		this.nameInput = new TextField<String>("name", nameModel);

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
			}
		};
		close.setMarkupId("closeImportDeck").setOutputMarkupId(true);
		form.add(close);

		final Button upload = new Button("upload");
		upload.add(new AjaxFormSubmitBehavior(form, "click")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target)
			{
				ImportDeckDialog.LOGGER.info("trying to upload a deck");

				if ((ImportDeckDialog.this.nameInput.getModelObject() == null)
						|| "".equals(ImportDeckDialog.this.nameInput.getModelObject()))
				{
					ImportDeckDialog.this.setMessage(target, "Please provide a deck name");
					return;
				}

				final FileUpload fupload = ImportDeckDialog.this.file.getFileUpload();
				if (fupload == null)
				{
					// No file was provided
					ImportDeckDialog.this.setMessage(target, "Please provide a deck file");
					return;
				}
				else if (fupload.getSize() == 0)
				{
					ImportDeckDialog.this.setMessage(target, "Please provide a non-empty deck");
					return;
				}
				else if ((fupload.getClientFileName() == null)
						|| ("".equals(fupload.getClientFileName().trim()))
						|| (!fupload.getClientFileName().endsWith(".txt")))
				{
					ImportDeckDialog.this
							.setMessage(target, "Please provide a deck in .txt format");
					return;
				}

				ImportDeckDialog.LOGGER.info("uploading deck: "
						+ ImportDeckDialog.this.file.getFileUpload().getClientFileName());

				try
				{
					if (!ImportDeckDialog.this.importDeckService.importDeck(
							new String(fupload.getBytes(), "UTF-8"),
							ImportDeckDialog.this.nameInput.getModelObject(), false))
					{
						ImportDeckDialog.this.setMessage(target, "Invalid deck format");
						return;
					}
				}
				catch (final UnsupportedEncodingException e)
				{
					ImportDeckDialog.this.setMessage(target,
							"Please provide a deck encoded with UTF-8 charset");
					return;
				}

				ImportDeckDialog.LOGGER.info("successfully added deck: "
						+ fupload.getClientFileName());
				ImportDeckDialog.this
						.setMessage(target, "Your deck has been successfully uploaded");
			}
		});

		form.add(upload);

		this.parent = new WebMarkupContainer("parent");
		this.parent.setOutputMarkupId(true);
		final Label message = new Label("message", Model.of(""));
		message.setOutputMarkupId(true);
		this.parent.add(message);
		form.add(this.parent);
	}

	void setMessage(final AjaxRequestTarget target, final String message)
	{
		this.parent.addOrReplace(new Label("message", Model.of(message)).setOutputMarkupId(true));
		target.add(this.parent);
	}


	@Required
	public void setImportDeckService(final ImportDeckService _importDeckService)
	{
		this.importDeckService = _importDeckService;
	}

}
