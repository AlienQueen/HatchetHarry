/*
 * $Id: DeckArchiveDaoImpl.java 1056 2006-10-27 22:49:28Z ivaynberg $
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

import org.alienlabs.hatchetharry.model.DeckArchive;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * implements {@link DeckArchiveDao}.
 *
 * @author igor
 */
public class DeckArchiveDaoImpl implements DeckArchiveDao
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	private SessionFactory factory;

	public DeckArchiveDaoImpl()
	{
	}

	/**
	 * Setter for session factory. Spring will use this to inject the session factory into the dao.
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
	 * Load a {@link DeckArchive} from the DB, given it's <tt>id</tt> .
	 *
	 * @param id
	 *            The id of the DeckArchive to load.
	 * @return DeckArchive
	 */
	@Override
	@Transactional(readOnly = true)
	public DeckArchive load(final long id)
	{
		return (DeckArchive)this.getSession().get(DeckArchive.class, Long.valueOf(id));
	}

	/**
	 * Save the DeckArchive to the DB
	 *
	 * @param deckArchive
	 * @return persistent instance of DeckArchive
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Long save(final DeckArchive deckArchive)
	{
		return (Long)this.getSession().save(deckArchive);
	}

	/**
	 * Delete a {@link DeckArchive} from the DB, given it's <tt>id</tt>.
	 *
	 * @param id
	 *            The id of the DeckArchive to delete.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(final long id)
	{
		this.getSession().delete(this.load(id));
	}

}
