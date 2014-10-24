package org.alienlabs.hatchetharry.model;

public enum Format {

	STANDARD("Standard"), MODERN("Modern"), COMMANDER("Commander"), TWO_HEADED_GIANT(
			"Two-headed giant"), LEGACY("Legacy"), VINTAGE("Vintage");

	private final String format;

	private Format(final String _format)
	{
		this.format = _format;

	}

	@Override
	public String toString()
	{
		return this.format;
	}

}
