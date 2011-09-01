package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@SuppressWarnings("serial")
public class PlayCardFromHandBehavior extends AbstractDefaultAjaxBehavior
{
	static final Logger logger = LoggerFactory.getLogger(PlayCardFromHandBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	private final WebMarkupContainer thumbParent;
	private final WebMarkupContainer cardParent;

	private UUID uuidToLookFor;
	private int currentCard;

	public PlayCardFromHandBehavior(final WebMarkupContainer _thumbParent,
			final WebMarkupContainer _cardParent, final UUID _uuidToLookFor, final int _currentCard)
	{
		super();
		InjectorHolder.getInjector().inject(this);
		this.thumbParent = _thumbParent;
		this.cardParent = _cardParent;
		this.uuidToLookFor = _uuidToLookFor;
		this.currentCard = _currentCard;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PlayCardFromHandBehavior.logger.info("respond");

		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		final String jsessionid = request.getRequestedSessionId();
		this.uuidToLookFor = UUID.fromString(request.getParameter("card"));
		final String stop = request.getParameter("stop");
		PlayCardFromHandBehavior.logger.info("url: " + request.getQueryString());

		int _indexOfClickedCard = -1;
		try
		{
			_indexOfClickedCard = Integer.parseInt(request.getParameter("indexOfClickedCard"));
		}
		catch (final NumberFormatException e)
		{
			PlayCardFromHandBehavior.logger.info("Error which should never happen!");
		}

		this.currentCard = _indexOfClickedCard;

		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);
		final List<MagicCard> all = HatchetHarrySession.get().getFirstCardsInHand();
		all.remove(card);
		HatchetHarrySession.get().setFirstCardsInHand(all);

		if (null != card)
		{
			PlayCardFromHandBehavior.logger.info("card title: " + card.getTitle() + ", uuid: "
					+ card.getUuidObject() + ", filename: " + card.getBigImageFilename());

			if ("true".equals(stop))
			{
				PlayCardFromHandBehavior.logger.info("stopping round-trips");

				final String id = "cardPlaceholdera"
						+ HatchetHarrySession.get().getPlaceholderNumber();
				HatchetHarrySession.get().setPlaceholderNumber(
						HatchetHarrySession.get().getPlaceholderNumber() + 1);

				final CardPanel cp = new CardPanel(id, card.getSmallImageFilename(),
						card.getBigImageFilename(), card.getUuidObject());
				cp.setOutputMarkupId(true);

				this.thumbParent.addOrReplace(cp);
				target.addComponent(this.thumbParent);
			}
			else if ((null != this.uuidToLookFor) && (!"undefined".equals(this.uuidToLookFor)))
			{
				PlayCardFromHandBehavior.logger.info("card: " + this.uuidToLookFor);

				final String id = "cardPlaceholdera"
						+ HatchetHarrySession.get().getPlaceholderNumber();
				HatchetHarrySession.get().setPlaceholderNumber(
						HatchetHarrySession.get().getPlaceholderNumber() + 1);

				final CardPanel cp = new CardPanel(id, card.getSmallImageFilename(),
						card.getBigImageFilename(), this.uuidToLookFor);
				cp.setOutputMarkupId(true);

				PlayCardFromHandBehavior.logger.info("continue!");

				final String message = jsessionid + "~~~" + this.uuidToLookFor + "~~~"
						+ (_indexOfClickedCard == 6 ? 0 : _indexOfClickedCard + 1);
				PlayCardFromHandBehavior.logger.info(message);

				final Meteor meteor = Meteor
						.build(request, new LinkedList<BroadcastFilter>(), null);
				meteor.addListener((AtmosphereResourceEventListener)target.getPage());
				meteor.broadcast(message);

				final ListView<MagicCard> allCards = new ListView<MagicCard>("handCards",
						HatchetHarrySession.get().getFirstCardsInHand())
				{
					private static final long serialVersionUID = -7874661839855866875L;

					@Override
					protected void populateItem(final ListItem<MagicCard> item)
					{
						HatchetHarrySession.get().addCardIdInHand(item.getIndex(), item.getIndex());
						final MagicCard _card = item.getModelObject();

						final WebMarkupContainer wrapper = new WebMarkupContainer("wrapper");
						wrapper.setMarkupId("wrapper" + item.getIndex());
						wrapper.setOutputMarkupId(true);

						final Image handImagePlaceholder = new Image("handImagePlaceholder",
								new ResourceReference(HomePage.class, _card.getBigImageFilename()));
						handImagePlaceholder.setMarkupId("placeholder"
								+ _card.getUuid().replace("-", "_"));
						handImagePlaceholder.setOutputMarkupId(true);

						final Label titlePlaceholder = new Label("titlePlaceholder",
								_card.getTitle());
						titlePlaceholder.setMarkupId("placeholder"
								+ _card.getUuid().replace("-", "_") + "_placeholder");
						titlePlaceholder.setOutputMarkupId(true);

						wrapper.add(handImagePlaceholder, titlePlaceholder);
						item.add(wrapper);

					}
				};
				allCards.setOutputMarkupId(true);
				this.cardParent.addOrReplace(allCards);
				target.addComponent(this.cardParent);

				this.thumbParent.addOrReplace(cp);
				target.addComponent(this.thumbParent);
			}
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.uuidToLookFor.toString());
		variables.put("uuidValidForJs", this.uuidToLookFor.toString().replace("-", "_"));
		variables.put("next", (this.currentCard == 6 ? 0 : this.currentCard + 1));
		variables.put("clicked", this.currentCard);

		final TextTemplate template1 = new PackagedTextTemplate(HomePage.class,
				"script/playCard/playCard.js");
		template1.interpolate(variables);

		response.renderJavascript(template1.asString(), null);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}
}
