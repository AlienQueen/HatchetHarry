package org.alienlabs.hatchetharry.serverSideTest.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.PageKey;
import org.apache.wicket.atmosphere.ResourceRegistrationListener;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.time.Time;
import org.apache.wicket.util.upload.FileItemFactory;
import org.apache.wicket.util.upload.FileUploadException;
import org.atmosphere.cpr.AsyncSupport;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.util.SimpleBroadcaster;
import org.junit.Assert;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class EventBusMock extends EventBus
{
	private final List<Object> events = new ArrayList<Object>();
	static AtmosphereFramework f;
	private final Broadcaster broadcaster;
	private final WebApplication application;
	public Map<String, PageKey> trackedPages = Maps.newHashMap();
	private final Multimap<PageKey, EventSubscription> subscriptions = HashMultimap.create();
	private final List<ResourceRegistrationListener> registrationListeners = new CopyOnWriteArrayList<ResourceRegistrationListener>();
	private final AtmosphereResource resource;
	private static final Logger LOGGER = LoggerFactory.getLogger(EventBusMock.class);

	static
	{
		EventBusMock.f = new AtmosphereFramework()
		{
			@Override
			public boolean isShareExecutorServices()
			{
				return true;
			}
		};

		EventBusMock.f.setBroadcasterFactory(new MyBroadcasterFactory());
		EventBusMock.f.setAsyncSupport(Mockito.mock(AsyncSupport.class));
		try
		{
			EventBusMock.f.init(new ServletConfig()
			{
				@Override
				public String getServletName()
				{
					return "void";
				}

				@Override
				public ServletContext getServletContext()
				{
					return Mockito.mock(ServletContext.class);
				}

				@Override
				public String getInitParameter(final String name)
				{
					return null;
				}

				@Override
				public Enumeration<String> getInitParameterNames()
				{
					return null;
				}
			});
		}
		catch (final ServletException e)
		{
			EventBusMock.LOGGER.error("error in static block", e);
		}
		Assert.assertNotNull(EventBusMock.f.getBroadcasterFactory());
	}

	public EventBusMock(final WebApplication _application)
	{
		super(_application, new MyBroadcasterFactory().get());

		this.broadcaster = new MyBroadcasterFactory().get();
		Assert.assertNotNull(this.broadcaster);

		this.application = _application;

		this.resource = AtmosphereResourceFactory.getDefault().create(
				EventBusMock.f.getAtmosphereConfig(), Mockito.mock(Broadcaster.class),
				AtmosphereResponse.newInstance().request(AtmosphereRequest.newInstance()),
				Mockito.mock(AsyncSupport.class), Mockito.mock(AtmosphereHandler.class));
	}

	@Override
	public void post(final Object event, final String resourceUuid)
	{
		this.events.add(event);
		Assert.assertNotNull(this.resource);
		this.post(event, this.resource);
	}

	@Override
	public void post(final Object event, final AtmosphereResource _resource)
	{
		final ThreadContext oldContext = ThreadContext.get(false);
		try
		{
			this.postToSingleResource(event, _resource);
		}
		catch (final Exception e)
		{
			EventBusMock.LOGGER.error("error in post()", e);
		}
		finally
		{
			ThreadContext.restore(oldContext);
		}
	}

	private void postToSingleResource(final Object payload, final AtmosphereResource resource)
	{
		final AtmosphereEvent event = new AtmosphereEvent(payload, resource);
		ThreadContext.detach();
		ThreadContext.setApplication(this.application);
		PageKey key;
		Collection<EventSubscription> subscriptionsForPage;
		synchronized (this)
		{
			key = this.trackedPages.get(resource.uuid());
			subscriptionsForPage = Collections2.filter(
					Collections.unmodifiableCollection(this.subscriptions.get(key)),
					new EventFilter(event));
		}
		if (key == null)
		{
			this.broadcaster.removeAtmosphereResource(resource);
		}
		else if (!subscriptionsForPage.isEmpty())
		{
			this.post(resource, key, subscriptionsForPage, event);
		}
	}

	private void post(final AtmosphereResource resource, final PageKey pageKey,
			final Collection<EventSubscription> subscriptionsForPage, final AtmosphereEvent event)
	{
		String filterPath = WebApplication.get().getWicketFilter().getFilterConfig()
				.getInitParameter(WicketFilter.FILTER_MAPPING_PARAM);
		filterPath = filterPath.substring(1, filterPath.length() - 1);
		final HttpServletRequest httpRequest = new HttpServletRequestWrapper(resource.getRequest())
		{
			@Override
			public String getContextPath()
			{
				final String ret = super.getContextPath();
				return ret == null ? "" : ret;
			}
		};
		final AtmosphereWebRequest request = new AtmosphereWebRequest(
				(ServletWebRequest)this.application.newWebRequest(httpRequest, filterPath),
				pageKey, subscriptionsForPage, event);
		final Response response = new AtmosphereWebResponse(resource.getResponse());
		if (this.application.createRequestCycle(request, response).processRequestAndDetach())
		{
			this.broadcaster.broadcast(response.toString(), resource);
		}
	}

	@Override
	public void addRegistrationListener(final ResourceRegistrationListener listener)
	{
		this.registrationListeners.add(listener);
	}

	/**
	 * Removes a previously added {@link ResourceRegistrationListener}.
	 * 
	 * @param listener
	 */
	@Override
	public void removeRegistrationListener(final ResourceRegistrationListener listener)
	{
		this.registrationListeners.add(listener);
	}

	public void fireRegistration(final String uuid, final Page page)
	{
		for (final ResourceRegistrationListener curListener : this.registrationListeners)
		{
			curListener.resourceRegistered(uuid, page);
		}
	}

	public void fireUnregistration(final String uuid)
	{
		for (final ResourceRegistrationListener curListener : this.registrationListeners)
		{
			curListener.resourceUnregistered(uuid);
		}
	}

	public List<Object> getEvents()
	{
		return this.events;
	}

	public AtmosphereResource getResource()
	{
		return this.resource;
	}

}

final class MyBroadcasterFactory extends BroadcasterFactory
{

	@Override
	public Broadcaster get()
	{
		return new SimpleBroadcaster();
	}

	@Override
	public Broadcaster get(final Object id)
	{
		return null;
	}

	@Override
	public <T extends Broadcaster> T get(final Class<T> c, final Object id)
	{
		return null;
	}

	@Override
	public void destroy()
	{

	}

	@Override
	public boolean add(final Broadcaster b, final Object id)
	{
		return false;
	}

	@Override
	public boolean remove(final Broadcaster b, final Object id)
	{
		return false;
	}

	@Override
	public <T extends Broadcaster> T lookup(final Class<T> c, final Object id)
	{
		return null;
	}

	@Override
	public <T extends Broadcaster> T lookup(final Class<T> c, final Object id,
			final boolean createIfNull)
	{
		return null;
	}

	@Override
	public <T extends Broadcaster> T lookup(final Object id)
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Broadcaster> T lookup(final Object id, final boolean createIfNull)
	{
		final T sb = (T)new SimpleBroadcaster();
		final AtmosphereConfig conf = new AtmosphereConfig(EventBusMock.f);
		Assert.assertNotNull(conf.framework());
		Assert.assertTrue(conf.framework().isShareExecutorServices());
		try
		{
			sb.initialize("/*", new URI("/"), conf);
		}
		catch (final URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
	}

	@Override
	public void removeAllAtmosphereResource(final AtmosphereResource r)
	{

	}

	@Override
	public boolean remove(final Object id)
	{
		return false;
	}

	@Override
	public Collection<Broadcaster> lookupAll()
	{
		final SimpleBroadcaster sb = new SimpleBroadcaster();
		final Collection<Broadcaster> all = new ArrayList<Broadcaster>();
		all.add(sb);
		return all;
	}

}

class AtmosphereEvent
{
	private final Object payload;

	private final AtmosphereResource resource;

	AtmosphereEvent(final Object payload, final AtmosphereResource resource)
	{
		this.payload = payload;
		this.resource = resource;
	}

	/**
	 * @return The payload of the event, as posted on the {@link EventBus}.
	 */
	public Object getPayload()
	{
		return this.payload;
	}

	/**
	 * @return The resource this event is targeted at.
	 */
	public AtmosphereResource getResource()
	{
		return this.resource;
	}
}

class AtmosphereWebRequest extends ServletWebRequest
{
	private final ServletWebRequest wrappedRequest;

	private final PageKey pageKey;

	private final Collection<EventSubscription> subscriptions;

	private final AtmosphereEvent event;

	AtmosphereWebRequest(final ServletWebRequest wrappedRequest, final PageKey pageKey,
			final Collection<EventSubscription> subscriptions, final AtmosphereEvent event)
	{
		super(wrappedRequest.getContainerRequest(), wrappedRequest.getFilterPrefix());
		this.wrappedRequest = wrappedRequest;
		this.pageKey = pageKey;
		this.subscriptions = subscriptions;
		this.event = event;
	}

	public PageKey getPageKey()
	{
		return this.pageKey;
	}

	public Collection<EventSubscription> getSubscriptions()
	{
		return this.subscriptions;
	}

	public AtmosphereEvent getEvent()
	{
		return this.event;
	}

	@Override
	public List<Cookie> getCookies()
	{
		return this.wrappedRequest.getCookies();
	}

	@Override
	public List<String> getHeaders(final String name)
	{
		return this.wrappedRequest.getHeaders(name);
	}

	@Override
	public String getHeader(final String name)
	{
		return this.wrappedRequest.getHeader(name);
	}

	@Override
	public Time getDateHeader(final String name)
	{
		return this.wrappedRequest.getDateHeader(name);
	}

	@Override
	public Url getUrl()
	{
		return this.wrappedRequest.getUrl();
	}

	@Override
	public Url getClientUrl()
	{
		return this.wrappedRequest.getClientUrl();
	}

	@Override
	public Locale getLocale()
	{
		return this.wrappedRequest.getLocale();
	}

	@Override
	public Charset getCharset()
	{
		// called from the super constructor, when wrappedRequest is still null
		if (this.wrappedRequest == null)
		{
			return RequestUtils.getCharset(super.getContainerRequest());
		}
		return this.wrappedRequest.getCharset();
	}

	@Override
	public Cookie getCookie(final String cookieName)
	{
		return this.wrappedRequest.getCookie(cookieName);
	}

	@Override
	public int hashCode()
	{
		return this.wrappedRequest.hashCode();
	}

	@Override
	public Url getOriginalUrl()
	{
		return this.wrappedRequest.getOriginalUrl();
	}

	@Override
	public IRequestParameters getQueryParameters()
	{
		return this.wrappedRequest.getQueryParameters();
	}

	@Override
	public IRequestParameters getRequestParameters()
	{
		return this.wrappedRequest.getRequestParameters();
	}

	@Override
	public boolean equals(final Object obj)
	{
		return this.wrappedRequest.equals(obj);
	}

	@Override
	public String getFilterPrefix()
	{
		return this.wrappedRequest.getFilterPrefix();
	}

	@Override
	public String toString()
	{
		return this.wrappedRequest.toString();
	}

	@Override
	public IRequestParameters getPostParameters()
	{
		return this.wrappedRequest.getPostParameters();
	}

	@Override
	public ServletWebRequest cloneWithUrl(final Url url)
	{
		return this.wrappedRequest.cloneWithUrl(url);
	}

	@Override
	public MultipartServletWebRequest newMultipartWebRequest(final Bytes maxSize,
			final String upload) throws FileUploadException
	{
		return this.wrappedRequest.newMultipartWebRequest(maxSize, upload);
	}

	@Override
	public MultipartServletWebRequest newMultipartWebRequest(final Bytes maxSize,
			final String upload, final FileItemFactory factory) throws FileUploadException
	{
		return this.wrappedRequest.newMultipartWebRequest(maxSize, upload, factory);
	}

	@Override
	public String getPrefixToContextPath()
	{
		return this.wrappedRequest.getPrefixToContextPath();
	}

	@Override
	public HttpServletRequest getContainerRequest()
	{
		return this.wrappedRequest.getContainerRequest();
	}

	@Override
	public String getContextPath()
	{
		return this.wrappedRequest.getContextPath();
	}

	@Override
	public String getFilterPath()
	{
		return this.wrappedRequest.getFilterPath();
	}

	@Override
	public boolean shouldPreserveClientUrl()
	{
		return this.wrappedRequest.shouldPreserveClientUrl();
	}

	@Override
	public boolean isAjax()
	{
		return true;
	}
}

class EventFilter implements Predicate<EventSubscription>
{
	private final AtmosphereEvent event;

	/**
	 * Construct.
	 * 
	 * @param event
	 */
	public EventFilter(final AtmosphereEvent event)
	{
		this.event = event;
	}

	@Override
	public boolean apply(@Nullable final EventSubscription input)
	{
		return input.getFilter().apply(this.event);
	}

	@Override
	public boolean equals(@Nullable final Object other)
	{
		return super.equals(other);
	}
}

class AtmosphereWebResponse extends WebResponse
{
	private final AtmosphereResponse response;
	private final AppendingStringBuffer out;
	private boolean redirect;

	/**
	 * Construct.
	 * 
	 * @param response
	 */
	AtmosphereWebResponse(final AtmosphereResponse response)
	{
		this.response = response;
		this.out = new AppendingStringBuffer(128);
	}

	@Override
	public void addCookie(final Cookie cookie)
	{
		this.response.addCookie(cookie);
	}

	@Override
	public void clearCookie(final Cookie cookie)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeader(final String name, final String value)
	{
		this.response.setHeader(name, value);
	}

	@Override
	public void addHeader(final String name, final String value)
	{
		this.response.addHeader(name, value);
	}

	@Override
	public void setDateHeader(final String name, final Time date)
	{
		this.response.setDateHeader(name, date.getMilliseconds());
	}

	@Override
	public void setContentLength(final long length)
	{
		this.response.setContentLength((int)length);
	}

	@Override
	public void setContentType(final String mimeType)
	{
		this.response.setContentType(mimeType);
	}

	@Override
	public void setStatus(final int sc)
	{
		this.response.setStatus(sc);
	}

	@Override
	public void sendError(final int sc, final String msg)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeRedirectURL(final CharSequence url)
	{
		// TODO temp fix for https://github.com/Atmosphere/atmosphere/issues/949
		return url.toString();
	}

	@Override
	public void sendRedirect(final String url)
	{
		this.out.clear();
		this.out.append("<ajax-response><redirect><![CDATA[" + url
				+ "]]></redirect></ajax-response>");
		this.redirect = true;
	}

	@Override
	public void write(final byte[] array)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(final byte[] array, final int offset, final int length)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeURL(final CharSequence url)
	{
		// TODO temp fix for https://github.com/Atmosphere/atmosphere/issues/949
		return url.toString();
	}

	@Override
	public Object getContainerResponse()
	{
		return this.response;
	}

	@Override
	public boolean isRedirect()
	{
		return false;
	}

	@Override
	public void reset()
	{
		this.out.clear();
	}

	@Override
	public void flush()
	{
	}

	@Override
	public void write(final CharSequence sequence)
	{
		if (!this.redirect)
		{
			this.out.append(sequence);
		}
	}

	/**
	 * @return The internal buffer directly as a {@link CharSequence}
	 */
	public CharSequence getBuffer()
	{
		return this.out;
	}

	@Override
	public String toString()
	{
		return this.out.toString();
	}
}

class EventSubscription
{
	private final String componentPath;

	private final Integer behaviorIndex;

	private final String methodName;

	private final Predicate<AtmosphereEvent> filter;

	private final Predicate<AtmosphereEvent> contextAwareFilter;

	/**
	 * Construct.
	 * 
	 * @param component
	 * @param behavior
	 * @param method
	 */
	public EventSubscription(final Component component, final Behavior behavior, final Method method)
	{
		this.componentPath = component.getPageRelativePath();
		this.behaviorIndex = behavior == null ? null : component.getBehaviorId(behavior);
		final Class<?> eventType = method.getParameterTypes()[1];
		final Subscribe subscribe = method.getAnnotation(Subscribe.class);
		this.filter = Predicates.and(EventSubscription.payloadOfType(eventType),
				EventSubscription.createFilter(subscribe.filter()));
		this.contextAwareFilter = EventSubscription.createFilter(subscribe.contextAwareFilter());
		this.methodName = method.getName();
	}

	/**
	 * Construct.
	 * 
	 * @param component
	 * @param behavior
	 * @param method
	 * @param filter
	 * @param contextAwareFilter
	 */
	public EventSubscription(final Component component, final Behavior behavior,
			final Method method, final Predicate<AtmosphereEvent> filter,
			final Predicate<AtmosphereEvent> contextAwareFilter)
	{
		this.componentPath = component.getPageRelativePath();
		this.behaviorIndex = behavior == null ? null : component.getBehaviorId(behavior);
		this.filter = filter == null ? new NoFilterPredicate() : filter;
		this.contextAwareFilter = contextAwareFilter == null
				? new NoFilterPredicate()
				: contextAwareFilter;
		this.methodName = method.getName();
	}

	private static Predicate<AtmosphereEvent> payloadOfType(final Class<?> type)
	{
		return new Predicate<AtmosphereEvent>()
		{
			@Override
			public boolean apply(final AtmosphereEvent input)
			{
				return type.isInstance(input.getPayload());
			}
		};
	}

	private static Predicate<AtmosphereEvent> createFilter(
			final Class<? extends Predicate<AtmosphereEvent>> filterClass)
	{
		try
		{
			return filterClass.newInstance();
		}
		catch (final InstantiationException e)
		{
			throw new WicketRuntimeException(e);
		}
		catch (final IllegalAccessException e)
		{
			throw new WicketRuntimeException(e);
		}
	}

	/**
	 * @return The path of the subscribed component
	 */
	public String getComponentPath()
	{
		return this.componentPath;
	}

	/**
	 * @return The index of the subscribed behavior, or null if the subscription
	 *         is for the component itself
	 */
	public Integer getBehaviorIndex()
	{
		return this.behaviorIndex;
	}

	/**
	 * @return The filter on incoming events, a combination of the type and the
	 *         {@link Subscribe#filter()} parameter.
	 */
	public Predicate<AtmosphereEvent> getFilter()
	{
		return this.filter;
	}

	/**
	 * @return The context ware filter on incoming events, constructed from the
	 *         {@link Subscribe#contextAwareFilter()} parameter.
	 */
	public Predicate<AtmosphereEvent> getContextAwareFilter()
	{
		return this.contextAwareFilter;
	}

	/**
	 * @return The method that is subscribed
	 */
	public String getMethodName()
	{
		return this.methodName;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.componentPath, this.behaviorIndex, this.methodName);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof EventSubscription)
		{
			final EventSubscription other = (EventSubscription)obj;
			return Objects.equal(this.componentPath, other.getComponentPath())
					&& Objects.equal(this.behaviorIndex, other.getBehaviorIndex())
					&& Objects.equal(this.methodName, other.getMethodName());
		}
		return false;
	}
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Subscribe {
	/**
	 * An optional filter on events to be received by the method. The filter
	 * cannot rely on any context. For example, the {@link RequestCycle} may not
	 * be available. For events filtered by this filter, Wicket-Atmosphere will
	 * not have to setup initiate the Wicket request cycle, which is quite
	 * expensive.
	 * 
	 * @return The filter on events, defaults to no filter.
	 */
	Class<? extends Predicate<AtmosphereEvent>> filter() default NoFilterPredicate.class;

	/**
	 * An optional filter on events to be received by the method. This filter
	 * has access to the Wicket context, such as the {@link Session} and the
	 * {@link RequestCycle}. If your filter does not require this context, you
	 * should use {@link #filter()} to prevent unnecessary setup of the request
	 * cycle.
	 * 
	 * @return The filter on events, defaults to no filter.
	 */
	Class<? extends Predicate<AtmosphereEvent>> contextAwareFilter() default NoFilterPredicate.class;
}

class NoFilterPredicate implements Predicate<AtmosphereEvent>
{
	@Override
	public boolean apply(@Nullable final AtmosphereEvent input)
	{
		return true;
	}

	@Override
	public boolean equals(@Nullable final Object other)
	{
		return super.equals(other);
	}

}
