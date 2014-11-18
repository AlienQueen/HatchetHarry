package org.alienlabs.hatchetharry.persistence.dao;

import org.alienlabs.hatchetharry.model.Side;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class SideDaoImpl implements SideDao
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	private SessionFactory factory;

	public SideDaoImpl()
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
	 * Load a {@link Side} from the DB, given it's <tt>id</tt> .
	 * 
	 * @param id
	 *            The id of the Side to load.
	 * @return Side
	 */
	@Override
	@Transactional(readOnly = true)
	public Side load(final long id)
	{
		return (Side)this.getSession().get(Side.class, Long.valueOf(id));
	}

	/**
	 * Save the Side to the DB
	 * 
	 * @param side
	 * @return persistent instance of Side
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Side save(final Side side)
	{
		return (Side)this.getSession().merge(side);
	}

	/**
	 * Delete a {@link Side} from the DB, given it's <tt>id</tt>.
	 * 
	 * @param id
	 *            The id of the Side to delete.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(final long id)
	{
		this.getSession().delete(this.load(id));
	}

}
