package org.alienlabs.hatchetharry;

import org.apache.wicket.markup.html.SecurePackageResourceGuard;

public class HatchetHarryResourceGuard extends SecurePackageResourceGuard {
	public HatchetHarryResourceGuard() {
		super(new SimpleCache(10));
		this.addPattern("+*.wav");
	}
}
