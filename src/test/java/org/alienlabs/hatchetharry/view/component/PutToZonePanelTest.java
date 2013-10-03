package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.junit.Test;


public class PutToZonePanelTest extends SpringContextLoaderBaseTest
{

	@Test
	public void testPutToZonePanel()
	{
		// assert PutToZonePanel is present under the hand
		SpringContextLoaderBaseTest.tester.assertComponent("galleryParent:gallery:putToZonePanel",
				PutToZonePanel.class);
	}

}
