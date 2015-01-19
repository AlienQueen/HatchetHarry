package org.alienlabs.hatchetharry;

import org.alienlabs.hatchetharry.model.consolelogstrategy.ZoneMoveConsoleLogStrategyTest;
import org.alienlabs.hatchetharry.serversidetest.NonRegressionTest;
import org.alienlabs.hatchetharry.service.ImportDeckServiceTest;
import org.alienlabs.hatchetharry.service.PersistenceServiceTest;
import org.alienlabs.hatchetharry.view.component.card.CardMoveBehaviorTest;
import org.alienlabs.hatchetharry.view.component.card.CardPanelTest;
import org.alienlabs.hatchetharry.view.component.card.CardRotateBehaviorTest;
import org.alienlabs.hatchetharry.view.component.card.CounterTooltipTest;
import org.alienlabs.hatchetharry.view.component.gui.DataBoxTest;
import org.alienlabs.hatchetharry.view.component.zone.PutToZonePanelTest;
import org.alienlabs.hatchetharry.view.page.HomePageTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ HomePageTest.class, CounterTooltipTest.class, ImportDeckServiceTest.class,
	CardPanelTest.class, CardMoveBehaviorTest.class, CardRotateBehaviorTest.class,
	NonRegressionTest.class, DataBoxTest.class, PutToZonePanelTest.class,
	PersistenceServiceTest.class, ZoneMoveConsoleLogStrategyTest.class })
public class LaunchAllServerSideTests
{
	// Just a helper class to launch all WicketTester tests at once, in an IDE
}
