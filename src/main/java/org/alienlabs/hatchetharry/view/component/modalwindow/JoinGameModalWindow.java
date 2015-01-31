package org.alienlabs.hatchetharry.view.component.modalwindow;

import java.util.ArrayList;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
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
import org.springframework.beans.factory.annotation.Required;


@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class JoinGameModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;
	final TextField<Long> gameIdInput;
	final TextField<String> nameInput;
	final DropDownChoice<String> sideInput;
	final HomePage hp;
	WebMarkupContainer deckParent;
	DropDownChoice<Deck> decks;
	final FeedbackPanel feedback;

	@SpringBean
	PersistenceService persistenceService;

	// TODO remove _dataBoxParent & _player
	public JoinGameModalWindow(final ModalWindow _modal, final String id, final Player _player,
			final WebMarkupContainer _dataBoxParent, final HomePage _hp)
	{
		super(id);
		Injector.get().inject(this);

		this.hp = _hp;

		final Form<String> form = new Form<>("form");

		final ArrayList<String> allSides = new ArrayList<>();
		allSides.add("infrared");
		allSides.add("ultraviolet");
		final Model<ArrayList<String>> sidesModel = new Model<>(allSides);
		final Label sideLabel = new Label("sideLabel", "Choose your side: ");
		this.sideInput = new DropDownChoice<>("sideInput", new Model<String>(), sidesModel);
		this.sideInput.setOutputMarkupId(true).setMarkupId("sideInput");

		final Label nameLabel = new Label("nameLabel", "Choose a name: ");
		final Model<String> nameModel = new Model<>("");
		this.nameInput = new TextField<>("name", nameModel);
		this.nameInput.setOutputMarkupId(true).setMarkupId("name");

		this.nameInput.add(new AjaxFormComponentUpdatingBehavior("onfocus")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target)
			{
				if ((null != target) && (null == JoinGameModalWindow.this.decks.getModelObject()))
				{
					final ArrayList<Deck> _allDecks = JoinGameModalWindow.this.persistenceService
							.getAllDecksFromDeckArchives();
					final Model<ArrayList<Deck>> _decksModel = new Model<>(_allDecks);
					JoinGameModalWindow.this.decks = new DropDownChoice<>("decks",
							new Model<Deck>(), _decksModel);
					JoinGameModalWindow.this.decks.setOutputMarkupId(true).setMarkupId("decks");

					JoinGameModalWindow.this.deckParent
					.addOrReplace(JoinGameModalWindow.this.decks);
					target.add(JoinGameModalWindow.this.deckParent);
				}
			}
		});

		final Label chooseDeck = new Label("chooseDeck", "Choose a deck: ");

		final ArrayList<Deck> allDecks = this.persistenceService.getAllDecksFromDeckArchives();
		final Model<ArrayList<Deck>> decksModel = new Model<>(allDecks);

		this.deckParent = new WebMarkupContainer("deckParent");
		this.deckParent.setOutputMarkupId(true);
		this.decks = new DropDownChoice<>("decks", new Model<Deck>(), decksModel);
		this.decks.setOutputMarkupId(true).setMarkupId("decks");
		this.deckParent.add(this.decks);

		final Label gameIdLabel = new Label("gameIdLabel",
                "Please provide the match id given by your opponent: ");
		final Model<Long> gameId = new Model<>(null);
		this.gameIdInput = new TextField<>("gameIdInput", gameId);
		this.gameIdInput.setRequired(true).setOutputMarkupId(true).setMarkupId("gameIdInput");

		this.feedback = new FeedbackPanel("feedback");
        this.feedback.setOutputMarkupId(true);

		final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				if ((null == JoinGameModalWindow.this.nameInput.getModelObject())
						|| ("".equals(JoinGameModalWindow.this.nameInput.getModelObject().trim()))
						|| (null == JoinGameModalWindow.this.decks.getModelObject())
						|| (null == JoinGameModalWindow.this.sideInput
						.getDefaultModelObjectAsString()))
				{
					return;
				}

				final Long _id = Long.valueOf(JoinGameModalWindow.this.gameIdInput
						.getDefaultModelObjectAsString());
				final Game g = JoinGameModalWindow.this.persistenceService.getGame(_id);

				if ((null == g))
				{
					target.add(JoinGameModalWindow.this.feedback);
					this.error("No pending game with this ID.");
					return;
				}

				if (_id.longValue() == HatchetHarrySession.get().getGameId().longValue())
				{
					target.add(JoinGameModalWindow.this.feedback);
					this.error("You can not join your own game! If you want to play alone, just create a game with one player.");
					return;
				}

				if (!g.isPending())
				{
					target.add(JoinGameModalWindow.this.feedback);
					this.error("No pending game with this ID.");
					return;
				}

				if (g.getPlayers().size() <= (g.getDesiredNumberOfPlayers().intValue() - 1))
				{
					g.setPending(false);
				}
				else
				{
					g.setPending(true);
				}
				JoinGameModalWindow.this.persistenceService.updateGame(g);

				Label _gameIdLabel = new Label("gameId", "Game id: " + g.getId().longValue());
				_gameIdLabel.setOutputMarkupId(true);
				final WebMarkupContainer gameIdParent = ((HomePage)target.getPage())
						.getGameIdParent();
				gameIdParent.addOrReplace(_gameIdLabel);
				target.add(gameIdParent);

				GameService.joinGame(JoinGameModalWindow.this.persistenceService, _modal, target,
						_id, JoinGameModalWindow.this.decks.getModelObject(),
						JoinGameModalWindow.this.sideInput.getDefaultModelObjectAsString(),
						JoinGameModalWindow.this.nameInput.getDefaultModelObjectAsString(),
						JoinGameModalWindow.this.hp);
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("joinSubmit");

		form.add(chooseDeck, this.deckParent, sideLabel, nameLabel, this.nameInput, this.sideInput,
				gameIdLabel, this.gameIdInput, this.feedback, submit);

		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
