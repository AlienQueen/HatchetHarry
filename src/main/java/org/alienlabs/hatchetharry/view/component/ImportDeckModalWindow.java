package org.alienlabs.hatchetharry.view.component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.DeckArchive;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
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

				String fileContent;

				final DeckArchive deckArchive = new DeckArchive();
				deckArchive.setDeckName(ImportDeckModalWindow.this.nameInput.getModelObject());
				ImportDeckModalWindow.this.persistenceService.saveDeckArchive(deckArchive);

				try
				{
					fileContent = new String(fupload.getBytes(), "UTF-8");

					final Deck deck = new Deck();
					deck.setPlayerId(1l);
					deck.setDeckArchive(deckArchive);
					final List<MagicCard> allMagicCards = deck.getCards();

					for (final String line : fileContent.split("\n"))
					{
						ImportDeckModalWindow.LOGGER.info("line: " + line);

						if ("".equals(line.trim()))
						{
							break;
						}

						final String numberOfItemsAsString = line.split(" ")[0];
						final int numberOfItems = Integer.parseInt(numberOfItemsAsString);
						final int indexOfSpace = line.indexOf(" ");
						final String cardName = line.substring(indexOfSpace + 1, line.length());


						ImportDeckModalWindow.LOGGER.info(numberOfItems + " x " + cardName);

						for (int i = 0; i < numberOfItems; i++)
						{
							final CollectibleCard cc = new CollectibleCard();
							cc.setTitle(cardName);
							cc.setDeckArchiveId(deckArchive.getDeckArchiveId());

							// TODO needed?
							// ImportDeckModalWindow.this.persistenceService
							// .saveDeckArchive(deckArchive);
							ImportDeckModalWindow.this.persistenceService.saveCollectibleCard(cc);

							final MagicCard card = new MagicCard(
									"cards/" + cardName + "_small.jpg", "cards/" + cardName
											+ ".jpg", "cards/" + cardName + "Thumb.jpg", cardName,
									"");
							card.setGameId(1l);
							card.setDeck(deck);
							card.setUuidObject(UUID.randomUUID());

							allMagicCards.add(card);

							ImportDeckModalWindow.this.persistenceService.saveOrUpdateDeck(deck);
							ImportDeckModalWindow.this.persistenceService.saveOrUpdateCard(card);
						}
					}
					// TODO needed?
					ImportDeckModalWindow.this.persistenceService.saveDeckArchive(deckArchive);
				}
				catch (final UnsupportedEncodingException e)
				{
					ImportDeckModalWindow.LOGGER.error("error parsing deck file", e);
				}

				final HomePage hp = (HomePage)ImportDeckModalWindow.this.getPage();
				hp.regenarateJoinGameWindowContent(HatchetHarrySession.get().getPlayer());
				hp.regenarateCreateGameWindowContent(HatchetHarrySession.get().getPlayer(),
						hp.getFirstSidePlaceholderParent());
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

}
