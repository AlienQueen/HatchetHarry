package org.alienlabs.hatchetharry;

import org.alienlabs.hatchetharry.serverSideTest.NonRegressionTests;
import org.alienlabs.hatchetharry.service.ImportDeckServiceTests;
import org.alienlabs.hatchetharry.service.PersistenceServiceTests;
import org.alienlabs.hatchetharry.view.component.card.CardMoveBehaviorTests;
import org.alienlabs.hatchetharry.view.component.card.CardPanelTests;
import org.alienlabs.hatchetharry.view.component.card.CardRotateBehaviorTests;
import org.alienlabs.hatchetharry.view.component.card.CounterTooltipTests;
import org.alienlabs.hatchetharry.view.component.gui.DataBoxTests;
import org.alienlabs.hatchetharry.view.component.zone.PutToZonePanelTests;
import org.alienlabs.hatchetharry.view.page.HomePageTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ HomePageTests.class, CounterTooltipTests.class, ImportDeckServiceTests.class,
		CardPanelTests.class, CardMoveBehaviorTests.class, CardRotateBehaviorTests.class,
		NonRegressionTests.class, DataBoxTests.class, PutToZonePanelTests.class,
		PersistenceServiceTests.class })
public class LaunchAllServerSideTests
{
	// Just a helper class to launch all WicketTester tests at once, in an IDE
}
