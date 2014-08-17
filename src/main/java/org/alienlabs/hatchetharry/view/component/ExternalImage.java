package org.alienlabs.hatchetharry.view.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.Model;

/**
 * Creates external image
 *
 * @author sergej.sizov
 */
public class ExternalImage extends WebComponent {
	private static final long serialVersionUID = 1L;
	private final String imageUrl;

	public ExternalImage(final String id, final String imageUrl) {
		super(id);
		this.imageUrl = imageUrl;
		this.add(AttributeModifier.replace("src", new Model<String>(this.imageUrl)));
		this.setVisible(!((imageUrl == null) || imageUrl.equals("")));
	}

	@Override
	protected void onComponentTag(final ComponentTag tag) {
		super.onComponentTag(tag);
		this.checkComponentTag(tag, "img");
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
	}

	public String getImageUrl() {
		return this.imageUrl;
	}
}
