package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Token;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutTokenOnBattlefieldCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class CreateTokenModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(CreateTokenModalWindow.class);

	final ModalWindow modal;

	final Model<String> typeModel, powerModel, thoughnessModel, colorsModel, capabilitiesModel,
			creatureTypesModel, descriptionModel;

	@SpringBean
	PersistenceService persistenceService;

	public CreateTokenModalWindow(final String id, final ModalWindow _modal)
	{
		super(id);
		this.modal = _modal;

		final ExternalImage topLibraryCard = new ExternalImage("topLibraryCard", "cards/token.jpg");

		this.typeModel = Model.of("");
		this.colorsModel = Model.of("");
		this.capabilitiesModel = Model.of("");
		this.creatureTypesModel = Model.of("");
		this.descriptionModel = Model.of("");
		this.powerModel = Model.of("");
		this.thoughnessModel = Model.of("");

		final Form<String> form = new Form<String>("form");

		final Label typeLabel = new Label("typeLabel", "Type: ");
		final TextField<String> type = new TextField<String>("type", this.typeModel);

		final Label powerLabel = new Label("powerLabel", "Power: ");
		final TextField<String> power = new TextField<String>("power", this.powerModel);

		final Label thoughnessLabel = new Label("thoughnessLabel", "Thoughness: ");
		final TextField<String> thoughness = new TextField<String>("thoughness",
				this.thoughnessModel);

		final Label colorsLabel = new Label("colorsLabel", "Colors: ");
		final TextField<String> colors = new TextField<String>("colors", this.colorsModel);

		final Label capabilitiesLabel = new Label("capabilitiesLabel", "Capabilities: ");
		final TextField<String> capabilities = new TextField<String>("capabilities",
				this.capabilitiesModel);

		final Label creatureTypesLabel = new Label("creatureTypesLabel", "Creature types: ");
		final TextField<String> creatureTypes = new TextField<String>("creatureTypes",
				this.creatureTypesModel);

		final Label descriptionLabel = new Label("descriptionLabel", "Description: ");
		final TextField<String> description = new TextField<String>("description",
				this.descriptionModel);

		final IndicatingAjaxButton createTokenButton = new IndicatingAjaxButton("createToken", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				final UUID uuid = UUID.randomUUID();
				final Long gameId = HatchetHarrySession.get().getGameId();
				final Player player = HatchetHarrySession.get().getPlayer();

				final Token token = new Token(CreateTokenModalWindow.this.typeModel.getObject(),
						Long.parseLong(CreateTokenModalWindow.this.powerModel.getObject()),
						Long.parseLong(CreateTokenModalWindow.this.thoughnessModel.getObject()),
						CreateTokenModalWindow.this.colorsModel.getObject(),
						CreateTokenModalWindow.this.descriptionModel.getObject(), uuid.toString(),
						gameId);

				token.setCapabilities(CreateTokenModalWindow.this.capabilitiesModel.getObject());
				token.setCreatureTypes(CreateTokenModalWindow.this.creatureTypesModel.getObject());
				token.setPlayer(player);

				CreateTokenModalWindow.this.persistenceService.saveToken(token);

				final MagicCard card = new MagicCard("cards/token.jpg", "", "", "token", "",
						player.getSide(), token);

				card.setGameId(gameId);
				card.setDeck(player.getDeck());
				card.setUuidObject(uuid);
				card.setZone(CardZone.BATTLEFIELD);

				card.getDeck().getCards().add(card);
				CreateTokenModalWindow.this.persistenceService.mergeCard(card);

				final PutTokenOnBattlefieldCometChannel ptobcc = new PutTokenOnBattlefieldCometChannel(
						gameId, card);

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.PUT_TOKEN_ON_BATTLEFIELD, gameId, player.getId(),
						player.getName(), "", "",
						CreateTokenModalWindow.this.creatureTypesModel.getObject(), null, null);

				final List<BigInteger> allPlayersInGame = CreateTokenModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				// post a message for all players in the game
				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long _player = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources()
							.get(_player);
					CreateTokenModalWindow.LOGGER.info("pageUuid: " + pageUuid);

					HatchetHarryApplication.get().getEventBus().post(ptobcc, pageUuid);
					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
				}

				CreateTokenModalWindow.this.modal.close(target);
			}
		};
		createTokenButton.setOutputMarkupId(true).setMarkupId("createToken");

		final IndicatingAjaxButton cancelButton = new IndicatingAjaxButton("cancel", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				CreateTokenModalWindow.this.modal.close(target);
			}
		};
		cancelButton.setOutputMarkupId(true).setMarkupId("cancel");

		form.add(typeLabel, type, powerLabel, power, thoughnessLabel, thoughness, colorsLabel,
				colors, capabilitiesLabel, capabilities, creatureTypesLabel, creatureTypes,
				descriptionLabel, description);

		form.add(createTokenButton, cancelButton);
		this.add(topLibraryCard, form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}
}
