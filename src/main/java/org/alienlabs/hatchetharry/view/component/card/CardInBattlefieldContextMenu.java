package org.alienlabs.hatchetharry.view.component.card;

import java.io.IOException;
import java.util.HashMap;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket.")
public class CardInBattlefieldContextMenu extends Panel {
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(CardInBattlefieldContextMenu.class);

	public CardInBattlefieldContextMenu(final String id, final Model<MagicCard> mc) {
		super(id, mc);
		final String uuidAsString = mc.getObject().getUuidObject().toString().replaceAll("-", "_");

		final WebMarkupContainer cardInBattlefieldContextMenu = new WebMarkupContainer(
				"cardInBattlefieldContextMenu");
		cardInBattlefieldContextMenu.setOutputMarkupId(true).setMarkupId(
				"cardInBattlefieldContextMenu" + uuidAsString);
		this.add(cardInBattlefieldContextMenu);

		final WebMarkupContainer putToHand = new WebMarkupContainer("putToHand");
		putToHand.setOutputMarkupId(true).setMarkupId("putToHand" + uuidAsString);

		final WebMarkupContainer putToGraveyard = new WebMarkupContainer("putToGraveyard");
		putToGraveyard.setOutputMarkupId(true).setMarkupId("putToGraveyard" + uuidAsString);

		final WebMarkupContainer putToExile = new WebMarkupContainer("putToExile");
		putToExile.setOutputMarkupId(true).setMarkupId("putToExile" + uuidAsString);

		final WebMarkupContainer destroyToken = new WebMarkupContainer("destroyToken");
		destroyToken.setOutputMarkupId(true).setMarkupId("destroyToken" + uuidAsString);

		cardInBattlefieldContextMenu.add(putToHand, putToGraveyard, putToExile, destroyToken);

		if (mc.getObject().getToken() != null) {
			putToHand.setVisible(false);
			putToGraveyard.setVisible(false);
			putToExile.setVisible(false);
		} else {
			destroyToken.setVisible(false);
		}

		this.add(new CardInBattlefieldContextMenuHeaderBehavior(uuidAsString));
	}

	static class CardInBattlefieldContextMenuHeaderBehavior extends Behavior {
		private static final long serialVersionUID = 1L;
		private final String uuidAsString;

		public CardInBattlefieldContextMenuHeaderBehavior(String _uuidAsString)
		{
			this.uuidAsString = _uuidAsString;
		}

		@Override
		public void renderHead(final Component component, final IHeaderResponse response)
		{
			super.renderHead(component, response);

			final HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("uuidValidForJs", this.uuidAsString);

			final TextTemplate template = new PackageTextTemplate(HomePage.class,
					"script/contextmenu/cardInBattlefieldContextMenu.js");
			template.interpolate(variables);

			response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
			try
			{
				template.close();
			} catch (final IOException e)
			{
				CardInBattlefieldContextMenu.LOGGER
						.error("unable to close template in CardInBattlefieldContextMenu.CardInBattlefieldContextMenuHeaderBehavior#renderHead()!",
								e);
			}
		}
	}

}
