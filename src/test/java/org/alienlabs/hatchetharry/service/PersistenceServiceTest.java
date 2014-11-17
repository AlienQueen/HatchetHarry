package org.alienlabs.hatchetharry.service;

import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class PersistenceServiceTest extends SpringContextLoaderBaseTest
{
	@Test
	@Ignore("implement some tests, men!!!")
	public void testBlah()
	{
		Assert.assertTrue(true);
	}

}
