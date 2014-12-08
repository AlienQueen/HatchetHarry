package org.alienlabs.hatchetharry.model;

public enum Format {
	STANDARD("Standard"), MODERN("Modern"), COMMANDER("Commander"), TWO_HEADED_GIANT(
			"Two-headed giant"), LEGACY("Legacy"), VINTAGE("Vintage");

	private final String formatName;

	Format(final String _formatName)
	{
		this.formatName = _formatName;

	}

	@Override
	public String toString()
	{
		return this.formatName;
	}

}
