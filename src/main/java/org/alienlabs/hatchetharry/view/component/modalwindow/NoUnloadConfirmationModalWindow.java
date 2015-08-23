package org.alienlabs.hatchetharry.view.component.modalwindow;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

/**
 * Created by nostromo on 16/08/15.
 */
public class NoUnloadConfirmationModalWindow extends ModalWindow
{

	public NoUnloadConfirmationModalWindow(String id)
	{
		super(id);
	}

	@Override
	public boolean showUnloadConfirmation()
	{
		return false;
	}
}
