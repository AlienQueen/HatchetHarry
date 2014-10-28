package org.alienlabs.hatchetharry.view.component.modalwindow;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Token;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutTokenOnBattlefieldCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.component.gui.ExternalImage;
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

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class CreateTokenModalWindow extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(CreateTokenModalWindow.class);
	private static final long serialVersionUID = 1L;
	final ModalWindow modal;

	final Model<String> typeModel, powerModel, toughnessModel, colorsModel, capabilitiesModel,
	creatureTypesModel, descriptionModel;

	@SpringBean
	PersistenceService persistenceService;

	public CreateTokenModalWindow(final String id, final ModalWindow _modal)
	{
		super(id);
		this.modal = _modal;

		final ExternalImage topLibraryCard = new ExternalImage("topLibraryCard",
				"cards/token_medium.jpg");

		this.typeModel = Model.of("");
		this.colorsModel = Model.of("");
		this.capabilitiesModel = Model.of("");
		this.creatureTypesModel = Model.of("");
		this.descriptionModel = Model.of("");
		this.powerModel = Model.of("");
		this.toughnessModel = Model.of("");

		final Form<String> form = new Form<String>("form");

		final Label typeLabel = new Label("typeLabel", "Type: ");
		final TextField<String> type = new TextField<String>("type", this.typeModel);

		final Label powerLabel = new Label("powerLabel", "Power: ");
		final TextField<String> power = new TextField<String>("power", this.powerModel);

		final Label thoughnessLabel = new Label("toughnessLabel", "Toughness: ");
		final TextField<String> thoughness = new TextField<String>("toughness", this.toughnessModel);

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
				final Player player = CreateTokenModalWindow.this.persistenceService
						.getPlayer(HatchetHarrySession.get().getPlayer().getId());

				final Token token = new Token(CreateTokenModalWindow.this.typeModel.getObject(),
						CreateTokenModalWindow.this.powerModel.getObject(),
						CreateTokenModalWindow.this.toughnessModel.getObject(),
						CreateTokenModalWindow.this.colorsModel.getObject(),
						CreateTokenModalWindow.this.descriptionModel.getObject(), uuid.toString(),
						gameId);

				final MagicCard card = new MagicCard("cards/token_small.jpg", "cards/token.jpg",
						"", "token", "", player.getSide().getSideName(), token, HatchetHarrySession
						.get().incrementLastBattlefieldOder());
				card.setGameId(gameId);

				final Deck deck = HatchetHarrySession.get().getPlayer().getDeck();
				card.setDeck(deck);

				card.setToken(token);
				card.setUuidObject(uuid);
				card.setZone(CardZone.BATTLEFIELD);
				card.getDeck().setPlayerId(player.getId());
				card.setX(card.getX() == -1l ? player.getSide().getX() : card.getX());
				card.setY(card.getY() == -1l ? player.getSide().getY() : card.getY());
				deck.getCards().add(card);

				token.setCapabilities(CreateTokenModalWindow.this.capabilitiesModel.getObject());
				token.setCreatureTypes(CreateTokenModalWindow.this.creatureTypesModel.getObject());
				token.setPlayer(player);

				CreateTokenModalWindow.this.persistenceService.mergeDeck(deck);
				CreateTokenModalWindow.this.persistenceService.saveToken(token);

				final PutTokenOnBattlefieldCometChannel ptobcc = new PutTokenOnBattlefieldCometChannel(
						gameId, card, player.getSide());
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.PUT_TOKEN_ON_BATTLEFIELD_ACTION, gameId, player.getId(),
						player.getName(), "", "",
						CreateTokenModalWindow.this.creatureTypesModel.getObject(), null, null);
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.TOKEN_CREATION_DESTRUCTION, null, null, true, null,
						HatchetHarrySession.get().getPlayer().getName(), token.getCreatureTypes(),
						null, null, false, gameId);

				final List<BigInteger> allPlayersInGame = CreateTokenModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);
				EventBusPostService.post(allPlayersInGame, ptobcc, ncc, new ConsoleLogCometChannel(
						logger));

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
