package org.alienlabs.hatchetharry.view.component.modalwindow;

import java.util.ArrayList;
import java.util.Arrays;

import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Format;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.GameService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class JoinGameWithoutIdModalWindow extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(JoinGameWithoutIdModalWindow.class);
	private static final long serialVersionUID = 1L;
	final TextField<String> nameInput;
	final DropDownChoice<String> sideInput;
	final HomePage hp;
	WebMarkupContainer deckParent;
	DropDownChoice<Deck> decks;
	final DropDownChoice<Format> formats;
	final FeedbackPanel feedback;
	final TextField<String> numberOfPlayers;

	@SpringBean
	PersistenceService persistenceService;

	// TODO remove _dataBoxParent & _modal & _player
	public JoinGameWithoutIdModalWindow(final ModalWindow _modal, final String id,
			final Player _player, final WebMarkupContainer _dataBoxParent, final HomePage _hp)
	{
		super(id);
		Injector.get().inject(this);

		this.hp = _hp;

		final Form<String> form = new Form<String>("form");

		final ArrayList<String> allSides = new ArrayList<String>();
		allSides.add("infrared");
		allSides.add("ultraviolet");
		final Model<ArrayList<String>> sidesModel = new Model<ArrayList<String>>(allSides);
		final Label sideLabel = new Label("sideLabel", "Choose your side: ");
		this.sideInput = new DropDownChoice<String>("sideInput", new Model<String>(), sidesModel);
		this.sideInput.setOutputMarkupId(true).setMarkupId("sideInput");

		final Label nameLabel = new Label("nameLabel", "Choose a name: ");
		final Model<String> nameModel = new Model<String>("");
		this.nameInput = new TextField<String>("name", nameModel);
		this.nameInput.setOutputMarkupId(true).setMarkupId("name");

		this.nameInput.add(new AjaxFormComponentUpdatingBehavior("onfocus")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target)
			{
				if ((null != target)
						&& (null == JoinGameWithoutIdModalWindow.this.decks.getModelObject()))
				{
					final ArrayList<Deck> _allDecks = JoinGameWithoutIdModalWindow.this.persistenceService
							.getAllDecksFromDeckArchives();
					final Model<ArrayList<Deck>> _decksModel = new Model<ArrayList<Deck>>(_allDecks);
					JoinGameWithoutIdModalWindow.this.decks = new DropDownChoice<Deck>("decks",
							new Model<Deck>(), _decksModel);
					JoinGameWithoutIdModalWindow.this.decks.setOutputMarkupId(true).setMarkupId(
							"decks");

					JoinGameWithoutIdModalWindow.this.deckParent
							.addOrReplace(JoinGameWithoutIdModalWindow.this.decks);
					target.add(JoinGameWithoutIdModalWindow.this.deckParent);
				}
			}
		});

		final Label chooseDeck = new Label("chooseDeck", "Choose a deck: ");

		final ArrayList<Deck> allDecks = this.persistenceService.getAllDecksFromDeckArchives();
		final Model<ArrayList<Deck>> decksModel = new Model<ArrayList<Deck>>(allDecks);

		this.deckParent = new WebMarkupContainer("deckParent");
		this.deckParent.setOutputMarkupId(true);
		this.decks = new DropDownChoice<Deck>("decks", new Model<Deck>(), decksModel);
		this.decks.setOutputMarkupId(true).setMarkupId("decks");
		this.deckParent.add(this.decks);

		final ArrayList<Format> allFormats = new ArrayList<Format>();
		allFormats.addAll(Arrays.asList(Format.values()));
		final Model<ArrayList<Format>> formatsModel = new Model<ArrayList<Format>>(allFormats);
		this.formats = new DropDownChoice<Format>("formats", new Model<Format>(), formatsModel);

		this.feedback = new FeedbackPanel("feedback");
		this.feedback.setOutputMarkupId(true);

		final Model<String> numberOfPlayersModel = new Model<String>("");
		this.numberOfPlayers = new TextField<String>("numberOfPlayers", numberOfPlayersModel);

		final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				if ((null == JoinGameWithoutIdModalWindow.this.nameInput.getModelObject())
						|| ("".equals(JoinGameWithoutIdModalWindow.this.nameInput.getModelObject()
								.trim()))
						|| (null == JoinGameWithoutIdModalWindow.this.decks.getModelObject())
						|| (null == JoinGameWithoutIdModalWindow.this.formats.getModelObject())
						|| (null == JoinGameWithoutIdModalWindow.this.sideInput
								.getDefaultModelObjectAsString())
						|| (null == JoinGameWithoutIdModalWindow.this.numberOfPlayers
								.getModelObject())
						|| ("".equals(JoinGameWithoutIdModalWindow.this.numberOfPlayers
								.getDefaultModelObjectAsString())))
				{
					return;
				}

				int desiredPlayers = 0;
				try
				{
					desiredPlayers = Integer
							.parseInt(JoinGameWithoutIdModalWindow.this.numberOfPlayers
									.getDefaultModelObjectAsString());
				}
				catch (final NumberFormatException e)
				{
					LOGGER.error(
							"invalid integer: "
									+ JoinGameWithoutIdModalWindow.this.numberOfPlayers
											.getDefaultModelObjectAsString(), e);
					return;
				}

				if ((desiredPlayers < 2) || (desiredPlayers > 10))
				{
					return;
				}

				final Long _id = JoinGameWithoutIdModalWindow.this.persistenceService
						.getPendingGame(JoinGameWithoutIdModalWindow.this.formats.getModelObject(),
								desiredPlayers);

				if (null == _id)
				{
					target.add(JoinGameWithoutIdModalWindow.this.feedback);
					this.error("No pending game for this format / number of players for the moment, please try creating a game or change the desired format / number of players.");
					return;
				}

				final Game g = JoinGameWithoutIdModalWindow.this.persistenceService.getGame(_id);
				if (g.getPlayers().size() <= (g.getDesiredNumberOfPlayers().intValue() - 1))
				{
					g.setPending(false);
				}
				else
				{
					g.setPending(true);
				}
				JoinGameWithoutIdModalWindow.this.persistenceService.updateGame(g);


				GameService
						.joinGame(JoinGameWithoutIdModalWindow.this.persistenceService, _modal,
								target, _id, JoinGameWithoutIdModalWindow.this.decks
										.getModelObject(),
								JoinGameWithoutIdModalWindow.this.sideInput
										.getDefaultModelObjectAsString(),
								JoinGameWithoutIdModalWindow.this.nameInput
										.getDefaultModelObjectAsString(),
								JoinGameWithoutIdModalWindow.this.hp);
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("joinSubmit");

		form.add(chooseDeck, this.deckParent, sideLabel, nameLabel, this.nameInput, this.sideInput,
				this.formats, this.feedback, this.numberOfPlayers, submit);

		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
