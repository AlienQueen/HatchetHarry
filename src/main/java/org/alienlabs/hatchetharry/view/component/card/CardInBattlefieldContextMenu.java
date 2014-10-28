package org.alienlabs.hatchetharry.view.component.card;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardInBattlefieldContextMenu extends Panel
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CardInBattlefieldContextMenu.class);

	private final UUID uuid;
	private final String uuidAsString;
	private MagicCard magicCard;

	public CardInBattlefieldContextMenu(final String id, final UUID _uuid, final MagicCard mc)
	{
		super(id);
		this.uuid = _uuid;
		this.uuidAsString = this.uuid.toString().replaceAll("-", "_");
		this.magicCard = mc;

		final WebMarkupContainer cardInBattlefieldContextMenu = new WebMarkupContainer(
				"cardInBattlefieldContextMenu");
		cardInBattlefieldContextMenu.setOutputMarkupId(true).setMarkupId(
				"cardInBattlefieldContextMenu" + this.uuidAsString);
		this.add(cardInBattlefieldContextMenu);

		final WebMarkupContainer putToHand = new WebMarkupContainer("putToHand");
		putToHand.setOutputMarkupId(true).setMarkupId("putToHand" + this.uuidAsString);

		final WebMarkupContainer putToGraveyard = new WebMarkupContainer("putToGraveyard");
		putToGraveyard.setOutputMarkupId(true).setMarkupId("putToGraveyard" + this.uuidAsString);

		final WebMarkupContainer putToExile = new WebMarkupContainer("putToExile");
		putToExile.setOutputMarkupId(true).setMarkupId("putToExile" + this.uuidAsString);

		final WebMarkupContainer destroyToken = new WebMarkupContainer("destroyToken");
		destroyToken.setOutputMarkupId(true).setMarkupId("destroyToken" + this.uuidAsString);

		cardInBattlefieldContextMenu.add(putToHand, putToGraveyard, putToExile, destroyToken);

		if (this.magicCard.getToken() != null) {
			putToHand.setVisible(false);
			putToGraveyard.setVisible(false);
			putToExile.setVisible(false);
		} else {
			destroyToken.setVisible(false);
		}

		this.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				super.renderHead(component, response);

				final HashMap<String, Object> variables = new HashMap<String, Object>();
				variables.put("uuidValidForJs", CardInBattlefieldContextMenu.this.uuidAsString);

				final TextTemplate template = new PackageTextTemplate(HomePage.class,
						"script/contextmenu/cardInBattlefieldContextMenu.js");
				template.interpolate(variables);

				response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
				try
				{
					template.close();
				}
				catch (final IOException e)
				{
					CardInBattlefieldContextMenu.LOGGER
					.error("unable to close template in CardInBattlefieldContextMenu#renderHead()!",
							e);
				}
			}
		});
	}

}
