/*
 * $Id: TokenDaoImpl.java 1056 2006-10-27 22:49:28Z ivaynberg $
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

import org.alienlabs.hatchetharry.model.Token;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * implements {@link TokenDao}.
 * 
 * @author igor
 */
public class TokenDaoImpl implements TokenDao
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	private SessionFactory factory;

	public TokenDaoImpl()
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
	 * Load a {@link Token} from the DB, given it's <tt>id</tt> .
	 * 
	 * @param id
	 *            The id of the Token to load.
	 * @return Token
	 */
	@Override
	@Transactional
	public Token load(final long id)
	{
		return (Token)this.getSession().get(Token.class, Long.valueOf(id));
	}

	/**
	 * Save the Token to the DB
	 * 
	 * @param Token
	 * @return persistent instance of Token
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void save(final Token Token)
	{
		this.getSession().save(Token);
	}

	/**
	 * Delete a {@link Token} from the DB, given it's <tt>id</tt>.
	 * 
	 * @param id
	 *            The id of the Token to delete.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(final long id)
	{
		this.getSession().delete(this.load(id));
	}

}
