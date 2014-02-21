package org.alienlabs.hatchetharry;

import org.alienlabs.hatchetharry.serverSideTest.NonRegressionTest;
import org.alienlabs.hatchetharry.service.ImportDeckServiceTest;
import org.alienlabs.hatchetharry.view.component.CardMoveBehaviorTest;
import org.alienlabs.hatchetharry.view.component.CardPanelTest;
import org.alienlabs.hatchetharry.view.component.CardRotateBehaviorTest;
import org.alienlabs.hatchetharry.view.component.CounterTooltipTest;
import org.alienlabs.hatchetharry.view.page.HomePageTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ImportDeckServiceTest.class, CardPanelTest.class, CardMoveBehaviorTest.class,
		CardRotateBehaviorTest.class, NonRegressionTest.class, HomePageTest.class,
		CounterTooltipTest.class })
public class LaunchAllServerSideTests
{
	// Just a helper class to launch all WicketTester tests at once, in Eclipse
}
