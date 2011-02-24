package org.alienlabs.hatchetharry;

import org.alienlabs.hatchetharry.view.HomePage;
import org.apache.wicket.Request;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;

import ch.qos.mistletoe.wicket.TestReportPage;

import com.ttdev.wicketpagetest.MockableSpringBeanInjector;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * 
 */
public class HatchetHarryApplication extends WebApplication
{

	/**
	 * Constructor
	 */
	public HatchetHarryApplication()
	{
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends TestReportPage> getHomePage()
	{
		return HomePage.class;
	}

	@Override
	protected void init()
	{
		super.init();

		MockableSpringBeanInjector.installInjector(this);
		this.mountSharedResource("images/logo32_32.png", new ResourceReference(HomePage.class,
				"images/logo32_32.png").getSharedResourceKey());
	}

	@Override
	public Session newSession(final Request request, final Response response)
	{
		return new HatchetHarrySession(request);
	}

}
