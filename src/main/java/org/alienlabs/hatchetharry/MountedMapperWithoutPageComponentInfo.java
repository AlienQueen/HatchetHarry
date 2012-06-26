package org.alienlabs.hatchetharry;

import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;

public class MountedMapperWithoutPageComponentInfo extends MountedMapper
{

	public MountedMapperWithoutPageComponentInfo(final String mountPath,
			final Class<? extends IRequestablePage> pageClass)
	{
		super(mountPath, pageClass, new PageParametersEncoder());
	}

	@Override
	protected void encodePageComponentInfo(final Url url, final PageComponentInfo info)
	{
		// does nothing so that component info does not get rendered in url
	}

	@Override
	public Url mapHandler(final IRequestHandler requestHandler)
	{
		if (requestHandler instanceof ListenerInterfaceRequestHandler)
		{
			return null;
		}
		return super.mapHandler(requestHandler);
	}

}
