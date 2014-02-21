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

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class EventBusMock extends EventBus {
	private final List<Object> events = new ArrayList<Object>();
	static AtmosphereFramework f;
	private Broadcaster broadcaster;
	private WebApplication application;
	public Map<String, PageKey> trackedPages = Maps.newHashMap();
	private Multimap<PageKey, EventSubscription> subscriptions = HashMultimap.create();
	private List<ResourceRegistrationListener> registrationListeners = new CopyOnWriteArrayList<ResourceRegistrationListener>();
	private AtmosphereResource resource;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertNotNull(EventBusMock.f.getBroadcasterFactory());
	}

	public EventBusMock(final WebApplication application)
	{
		super(application, new MyBroadcasterFactory().get());

		this.broadcaster = new MyBroadcasterFactory().get();
		Assert.assertNotNull(this.broadcaster);

		this.application = application;

		this.resource = AtmosphereResourceFactory.getDefault().create(f.getAtmosphereConfig(), Mockito.mock(Broadcaster.class), AtmosphereResponse.newInstance().request(AtmosphereRequest.newInstance()),
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
	public void post(Object event, AtmosphereResource resource)
	{
		ThreadContext oldContext = ThreadContext.get(false);
		try
		{
			postToSingleResource(event, resource);
		}
		finally
		{
			ThreadContext.restore(oldContext);
		}
	}

	private void postToSingleResource(Object payload, AtmosphereResource resource)
	{
		AtmosphereEvent event = new AtmosphereEvent(payload, resource);
		ThreadContext.detach();
		ThreadContext.setApplication(application);
		PageKey key;
		Collection<EventSubscription> subscriptionsForPage;
		synchronized (this)
		{
			key = trackedPages.get(resource.uuid());
			subscriptionsForPage = Collections2.filter(
					Collections.unmodifiableCollection(subscriptions.get(key)), new EventFilter(event));
		}
		if (key == null)
			broadcaster.removeAtmosphereResource(resource);
		else if (!subscriptionsForPage.isEmpty())
			post(resource, key, subscriptionsForPage, event);
	}

	private void post(AtmosphereResource resource, PageKey pageKey,
			Collection<EventSubscription> subscriptionsForPage, AtmosphereEvent event)
	{
		String filterPath = WebApplication.get()
				.getWicketFilter()
				.getFilterConfig()
				.getInitParameter(WicketFilter.FILTER_MAPPING_PARAM);
		filterPath = filterPath.substring(1, filterPath.length() - 1);
		HttpServletRequest httpRequest = new HttpServletRequestWrapper(resource.getRequest())
		{
			@Override
			public String getContextPath()
			{
				String ret = super.getContextPath();
				return ret == null ? "" : ret;
			}
		};
		AtmosphereWebRequest request = new AtmosphereWebRequest(
				(ServletWebRequest)application.newWebRequest(httpRequest, filterPath), pageKey,
				subscriptionsForPage, event);
		Response response = new AtmosphereWebResponse(resource.getResponse());
		if (application.createRequestCycle(request, response).processRequestAndDetach())
			broadcaster.broadcast(response.toString(), resource);
	}

	@Override
	public void addRegistrationListener(ResourceRegistrationListener listener)
	{
		registrationListeners.add(listener);
	}

	/**
	 * Removes a previously added {@link ResourceRegistrationListener}.
	 * 
	 * @param listener
	 */
	@Override
	public void removeRegistrationListener(ResourceRegistrationListener listener)
	{
		registrationListeners.add(listener);
	}

	public void fireRegistration(String uuid, Page page)
	{
		for (ResourceRegistrationListener curListener : registrationListeners)
		{
			curListener.resourceRegistered(uuid, page);
		}
	}

	public void fireUnregistration(String uuid)
	{
		for (ResourceRegistrationListener curListener : registrationListeners)
		{
			curListener.resourceUnregistered(uuid);
		}
	}

	public List<Object> getEvents()
	{
		return this.events;
	}

	public AtmosphereResource getResource() {
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

	AtmosphereEvent(Object payload, AtmosphereResource resource)
	{
		this.payload = payload;
		this.resource = resource;
	}

	/**
	 * @return The payload of the event, as posted on the {@link EventBus}.
	 */
	public Object getPayload()
	{
		return payload;
	}

	/**
	 * @return The resource this event is targeted at.
	 */
	public AtmosphereResource getResource()
	{
		return resource;
	}
}

class AtmosphereWebRequest extends ServletWebRequest
{
	private ServletWebRequest wrappedRequest;

	private PageKey pageKey;

	private Collection<EventSubscription> subscriptions;

	private AtmosphereEvent event;

	AtmosphereWebRequest(ServletWebRequest wrappedRequest, PageKey pageKey,
			Collection<EventSubscription> subscriptions, AtmosphereEvent event)
			{
		super(wrappedRequest.getContainerRequest(), wrappedRequest.getFilterPrefix());
		this.wrappedRequest = wrappedRequest;
		this.pageKey = pageKey;
		this.subscriptions = subscriptions;
		this.event = event;
			}

	public PageKey getPageKey()
	{
		return pageKey;
	}

	public Collection<EventSubscription> getSubscriptions()
	{
		return subscriptions;
	}

	public AtmosphereEvent getEvent()
	{
		return event;
	}

	@Override
	public List<Cookie> getCookies()
	{
		return wrappedRequest.getCookies();
	}

	@Override
	public List<String> getHeaders(String name)
	{
		return wrappedRequest.getHeaders(name);
	}

	@Override
	public String getHeader(String name)
	{
		return wrappedRequest.getHeader(name);
	}

	@Override
	public Time getDateHeader(String name)
	{
		return wrappedRequest.getDateHeader(name);
	}

	@Override
	public Url getUrl()
	{
		return wrappedRequest.getUrl();
	}

	@Override
	public Url getClientUrl()
	{
		return wrappedRequest.getClientUrl();
	}

	@Override
	public Locale getLocale()
	{
		return wrappedRequest.getLocale();
	}

	@Override
	public Charset getCharset()
	{
		// called from the super constructor, when wrappedRequest is still null
		if (wrappedRequest == null)
			return RequestUtils.getCharset(super.getContainerRequest());
		return wrappedRequest.getCharset();
	}

	@Override
	public Cookie getCookie(String cookieName)
	{
		return wrappedRequest.getCookie(cookieName);
	}

	@Override
	public int hashCode()
	{
		return wrappedRequest.hashCode();
	}

	@Override
	public Url getOriginalUrl()
	{
		return wrappedRequest.getOriginalUrl();
	}

	@Override
	public IRequestParameters getQueryParameters()
	{
		return wrappedRequest.getQueryParameters();
	}

	@Override
	public IRequestParameters getRequestParameters()
	{
		return wrappedRequest.getRequestParameters();
	}

	@Override
	public boolean equals(Object obj)
	{
		return wrappedRequest.equals(obj);
	}

	@Override
	public String getFilterPrefix()
	{
		return wrappedRequest.getFilterPrefix();
	}

	@Override
	public String toString()
	{
		return wrappedRequest.toString();
	}

	@Override
	public IRequestParameters getPostParameters()
	{
		return wrappedRequest.getPostParameters();
	}

	@Override
	public ServletWebRequest cloneWithUrl(Url url)
	{
		return wrappedRequest.cloneWithUrl(url);
	}

	@Override
	public MultipartServletWebRequest newMultipartWebRequest(Bytes maxSize, String upload)
			throws FileUploadException
			{
		return wrappedRequest.newMultipartWebRequest(maxSize, upload);
			}

	@Override
	public MultipartServletWebRequest newMultipartWebRequest(Bytes maxSize, String upload,
			FileItemFactory factory) throws FileUploadException
			{
		return wrappedRequest.newMultipartWebRequest(maxSize, upload, factory);
			}

	@Override
	public String getPrefixToContextPath()
	{
		return wrappedRequest.getPrefixToContextPath();
	}

	@Override
	public HttpServletRequest getContainerRequest()
	{
		return wrappedRequest.getContainerRequest();
	}

	@Override
	public String getContextPath()
	{
		return wrappedRequest.getContextPath();
	}

	@Override
	public String getFilterPath()
	{
		return wrappedRequest.getFilterPath();
	}

	@Override
	public boolean shouldPreserveClientUrl()
	{
		return wrappedRequest.shouldPreserveClientUrl();
	}

	@Override
	public boolean isAjax()
	{
		return true;
	}
}

class EventFilter implements Predicate<EventSubscription>
{
	private AtmosphereEvent event;

	/**
	 * Construct.
	 * 
	 * @param event
	 */
	public EventFilter(AtmosphereEvent event)
	{
		this.event = event;
	}

	@Override
	public boolean apply(@Nullable EventSubscription input)
	{
		return input.getFilter().apply(event);
	}

	@Override
	public boolean equals(@Nullable Object other)
	{
		return super.equals(other);
	}
}

class AtmosphereWebResponse extends WebResponse
{
	private AtmosphereResponse response;
	private final AppendingStringBuffer out;
	private boolean redirect;

	/**
	 * Construct.
	 * 
	 * @param response
	 */
	AtmosphereWebResponse(AtmosphereResponse response)
	{
		this.response = response;
		out = new AppendingStringBuffer(128);
	}

	@Override
	public void addCookie(Cookie cookie)
	{
		response.addCookie(cookie);
	}

	@Override
	public void clearCookie(Cookie cookie)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeader(String name, String value)
	{
		response.setHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value)
	{
		response.addHeader(name, value);
	}

	@Override
	public void setDateHeader(String name, Time date)
	{
		response.setDateHeader(name, date.getMilliseconds());
	}

	@Override
	public void setContentLength(long length)
	{
		response.setContentLength((int)length);
	}

	@Override
	public void setContentType(String mimeType)
	{
		response.setContentType(mimeType);
	}

	@Override
	public void setStatus(int sc)
	{
		response.setStatus(sc);
	}

	@Override
	public void sendError(int sc, String msg)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeRedirectURL(CharSequence url)
	{
		// TODO temp fix for https://github.com/Atmosphere/atmosphere/issues/949
		return url.toString();
	}

	@Override
	public void sendRedirect(String url)
	{
		out.clear();
		out.append("<ajax-response><redirect><![CDATA[" + url + "]]></redirect></ajax-response>");
		redirect = true;
	}

	@Override
	public void write(byte[] array)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(byte[] array, int offset, int length)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeURL(CharSequence url)
	{
		// TODO temp fix for https://github.com/Atmosphere/atmosphere/issues/949
		return url.toString();
	}

	@Override
	public Object getContainerResponse()
	{
		return response;
	}

	@Override
	public boolean isRedirect()
	{
		return false;
	}

	@Override
	public void reset()
	{
		out.clear();
	}

	@Override
	public void flush()
	{
	}

	@Override
	public void write(CharSequence sequence)
	{
		if (!redirect)
			out.append(sequence);
	}

	/**
	 * @return The internal buffer directly as a {@link CharSequence}
	 */
	public CharSequence getBuffer()
	{
		return out;
	}

	@Override
	public String toString()
	{
		return out.toString();
	}
}

class EventSubscription
{
	private String componentPath;

	private Integer behaviorIndex;

	private String methodName;

	private Predicate<AtmosphereEvent> filter;

	private Predicate<AtmosphereEvent> contextAwareFilter;

	/**
	 * Construct.
	 * 
	 * @param component
	 * @param behavior
	 * @param method
	 */
	public EventSubscription(Component component, Behavior behavior, Method method)
	{
		componentPath = component.getPageRelativePath();
		behaviorIndex = behavior == null ? null : component.getBehaviorId(behavior);
		Class<?> eventType = method.getParameterTypes()[1];
		Subscribe subscribe = method.getAnnotation(Subscribe.class);
		filter = Predicates.and(payloadOfType(eventType), createFilter(subscribe.filter()));
		contextAwareFilter = createFilter(subscribe.contextAwareFilter());
		methodName = method.getName();
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
	public EventSubscription(Component component, Behavior behavior, Method method,
			Predicate<AtmosphereEvent> filter, Predicate<AtmosphereEvent> contextAwareFilter)
	{
		componentPath = component.getPageRelativePath();
		behaviorIndex = behavior == null ? null : component.getBehaviorId(behavior);
		this.filter = filter == null ? new NoFilterPredicate() : filter;
		this.contextAwareFilter = contextAwareFilter == null ? new NoFilterPredicate()
		: contextAwareFilter;
		methodName = method.getName();
	}

	private static Predicate<AtmosphereEvent> payloadOfType(final Class<?> type)
	{
		return new Predicate<AtmosphereEvent>()
				{
			@Override
			public boolean apply(AtmosphereEvent input)
			{
				return type.isInstance(input.getPayload());
			}
				};
	}

	private static Predicate<AtmosphereEvent> createFilter(
			Class<? extends Predicate<AtmosphereEvent>> filterClass)
			{
		try
		{
			return filterClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new WicketRuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new WicketRuntimeException(e);
		}
			}

	/**
	 * @return The path of the subscribed component
	 */
	public String getComponentPath()
	{
		return componentPath;
	}

	/**
	 * @return The index of the subscribed behavior, or null if the subscription is for the
	 *         component itself
	 */
	public Integer getBehaviorIndex()
	{
		return behaviorIndex;
	}

	/**
	 * @return The filter on incoming events, a combination of the type and the
	 *         {@link Subscribe#filter()} parameter.
	 */
	public Predicate<AtmosphereEvent> getFilter()
	{
		return filter;
	}

	/**
	 * @return The context ware filter on incoming events, constructed from the
	 *         {@link Subscribe#contextAwareFilter()} parameter.
	 */
	public Predicate<AtmosphereEvent> getContextAwareFilter()
	{
		return contextAwareFilter;
	}

	/**
	 * @return The method that is subscribed
	 */
	public String getMethodName()
	{
		return methodName;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(componentPath, behaviorIndex, methodName);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof EventSubscription)
		{
			EventSubscription other = (EventSubscription)obj;
			return Objects.equal(componentPath, other.getComponentPath()) &&
					Objects.equal(behaviorIndex, other.getBehaviorIndex()) &&
					Objects.equal(methodName, other.getMethodName());
		}
		return false;
	}
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Subscribe {
	/**
	 * An optional filter on events to be received by the method. The filter cannot rely on any
	 * context. For example, the {@link RequestCycle} may not be available. For events filtered by
	 * this filter, Wicket-Atmosphere will not have to setup initiate the Wicket request cycle,
	 * which is quite expensive.
	 * 
	 * @return The filter on events, defaults to no filter.
	 */
	Class<? extends Predicate<AtmosphereEvent>> filter() default NoFilterPredicate.class;

	/**
	 * An optional filter on events to be received by the method. This filter has access to the
	 * Wicket context, such as the {@link Session} and the {@link RequestCycle}. If your filter does
	 * not require this context, you should use {@link #filter()} to prevent unnecessary setup of
	 * the request cycle.
	 * 
	 * @return The filter on events, defaults to no filter.
	 */
	Class<? extends Predicate<AtmosphereEvent>> contextAwareFilter() default NoFilterPredicate.class;
}

class NoFilterPredicate implements Predicate<AtmosphereEvent>
{
	@Override
	public boolean apply(@Nullable AtmosphereEvent input)
	{
		return true;
	}

	@Override
	public boolean equals(@Nullable Object other)
	{
		return super.equals(other);
	}

}
