package org.alienlabs.hatchetharry.model;

public enum CardZone {

	BATTLEFIELD("Battlefield"), GRAVEYARD("Graveyard"), HAND("Hand"), EXILE("Exile"), LIBRARY(
																									 "Library");

	private final String zoneName;

	private CardZone(final String _zoneName) {
		this.zoneName = _zoneName;

	}

	@Override
	public String toString() {
		return this.zoneName;
	}

}
