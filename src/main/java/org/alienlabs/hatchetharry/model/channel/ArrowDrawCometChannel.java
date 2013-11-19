package org.alienlabs.hatchetharry.model.channel;

public class ArrowDrawCometChannel
{

	private final String source;
	private final String target;

	public ArrowDrawCometChannel(final String _source, final String _target)
	{
		this.source = _source;
		this.target = _target;
	}

	public String getSource()
	{
		return this.source;
	}

	public String getTarget()
	{
		return this.target;
	}

}
