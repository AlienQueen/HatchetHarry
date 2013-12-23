package org.alienlabs.hatchetharry.persistence.dao;

import java.io.Serializable;

import org.alienlabs.hatchetharry.model.User;
import org.hibernate.Session;

public interface UserDao extends Serializable
{
	Session getSession();

	/**
	 * Load a {@link User} from the DB, given it's <tt>id</tt>.
	 * 
	 * @param id
	 *            The id of the User to load.
	 * @return User
	 */
	User load(long id);

	/**
	 * Save the User to the DB
	 * 
	 * @param User
	 * @return persistent instance of User
	 */
	User save(User user);

	/**
	 * Delete a {@link User} from the DB, given it's <tt>id</tt>.
	 * 
	 * @param id
	 *            The id of the User to delete.
	 */
	void delete(long id);
}
