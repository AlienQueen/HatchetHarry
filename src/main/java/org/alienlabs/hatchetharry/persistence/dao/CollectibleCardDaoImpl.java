/*
 * $Id: CollectibleCardDaoImpl.java 1056 2006-10-27 22:49:28Z ivaynberg $
 * $Revision: 1056 $
 * $Date: 2006-10-27 15:49:28 -0700 (Fri, 27 Oct 2006) $
 *
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.alienlabs.hatchetharry.persistence.dao;

import java.util.Iterator;
import java.util.List;

import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * implements {@link CollectibleCardDao}.
 * 
 * @author igor
 */
public class CollectibleCardDaoImpl implements CollectibleCardDao
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	private SessionFactory factory;

	public CollectibleCardDaoImpl()
	{
	}

	/**
	 * Setter for session factory. Spring will use this to inject the session
	 * factory into the dao.
	 * 
	 * @param _factory
	 *            hibernate session factory
	 */
	@Required
	public void setSessionFactory(final SessionFactory _factory)
	{
		this.factory = _factory;
	}

	/**
	 * Helper method for retrieving hibernate session
	 * 
	 * @return hibernate session
	 */
	@Override
	public Session getSession()
	{
		return this.factory.getCurrentSession();
	}

	/**
	 * Load a {@link CollectibleCard} from the DB, given it's <tt>id</tt> .
	 * 
	 * @param id
	 *            The id of the CollectibleCard to load.
	 * @return CollectibleCard
	 */
	@Override
	@Transactional
	public CollectibleCard load(final long id)
	{
		return (CollectibleCard)this.getSession().get(CollectibleCard.class, Long.valueOf(id));
	}

	/**
	 * Save the CollectibleCard to the DB
	 * 
	 * @param CollectibleCard
	 * @return persistent instance of CollectibleCard
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public CollectibleCard save(final CollectibleCard CollectibleCard)
	{
		return (CollectibleCard)this.getSession().merge(CollectibleCard);
	}

	/**
	 * Delete a {@link CollectibleCard} from the DB, given it's <tt>id</tt>.
	 * 
	 * @param id
	 *            The id of the CollectibleCard to delete.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(final long id)
	{
		this.getSession().delete(this.load(id));
	}

	/**
	 * Query the DB, using the supplied query details.
	 * 
	 * @param qp
	 *            Query Parameters to use.
	 * @return The results of the query as an Iterator.
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public Iterator<CollectibleCard> find(final QueryParam qp, final CollectibleCard filter)
	{
		return this.buildFindQuery(qp, filter, false).list().iterator();

	}

	/**
	 * Return the number of CollectibleCards in the DB.
	 * 
	 * @return count
	 */
	@Override
	@Transactional
	public int count(final CollectibleCard filter)
	{
		return ((Long)this.buildFindQuery(null, filter, true).uniqueResult()).intValue();
	}

	/**
	 * Returns a list of unique last names
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public List<String> getUniqueLastNames()
	{
		return this
				.getSession()
				.createQuery(
						"select distinct target.lastname "
								+ " from CollectibleCard target order by target.lastname").list();
	}

	/**
	 * builds a query object to satisfy the provided parameters
	 * 
	 * @param qp
	 *            sorting and paging criteria
	 * @param filter
	 *            filter criteria
	 * @param count
	 *            true if this is a query meant to retrieve the number of rows
	 * @return query object that satisfies the provided criteria
	 */
	protected Query buildFindQuery(final QueryParam qp, final CollectibleCard filter,
			final boolean count)
	{
		final CollectibleCardFinderQueryBuilder builder = new CollectibleCardFinderQueryBuilder();
		builder.setQueryParam(qp);
		builder.setFilter(filter);
		builder.setCount(count);
		final Query query = this.getSession().createQuery(builder.buildHql());
		query.setParameters(builder.getParameters(), builder.getTypes());
		if (!count && (qp != null))
		{
			query.setFirstResult(qp.getFirst()).setMaxResults(qp.getCount());
		}
		return query;
	}

}
