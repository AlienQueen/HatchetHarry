package org.alienlabs.hatchetharry.persistence.dao;

import java.io.Serializable;

import org.alienlabs.hatchetharry.model.Side;
import org.hibernate.Session;

public interface SideDao extends Serializable
{
	Session getSession();

	/**
	 * Load a {@link Side} from the DB, given it's <tt>id</tt>.
	 * 
	 * @param id
	 *            The id of the Side to load.
	 * @return Side
	 */
	Side load(long id);

	/**
	 * Save the Side to the DB
	 * 
	 * @param Side
	 * @return persistent instance of side
	 */
	Side save(Side side);

	/**
	 * Delete a {@link Side} from the DB, given it's <tt>id</tt>.
	 * 
	 * @param id
	 *            The id of the Side to delete.
	 */
	void delete(long id);
}
