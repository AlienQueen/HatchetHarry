package org.alienlabs.hatchetharry;

import org.alienlabs.hatchetharry.serverSideTest.NonRegressionTest;
import org.alienlabs.hatchetharry.service.ImportDeckServiceTest;
import org.alienlabs.hatchetharry.service.PersistenceServiceTest;
import org.alienlabs.hatchetharry.view.component.card.CardMoveBehaviorTest;
import org.alienlabs.hatchetharry.view.component.card.CardPanelTest;
import org.alienlabs.hatchetharry.view.component.card.CardRotateBehaviorTest;
import org.alienlabs.hatchetharry.view.component.card.CounterTooltipTest;
import org.alienlabs.hatchetharry.view.page.HomePageTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ HomePageTest.class, CounterTooltipTest.class, ImportDeckServiceTest.class,
		CardPanelTest.class, CardMoveBehaviorTest.class, CardRotateBehaviorTest.class,
		NonRegressionTest.class, PersistenceServiceTest.class })
public class LaunchAllServerSideTests
{
	// Just a helper class to launch all WicketTester tests at once, in an IDE
}
