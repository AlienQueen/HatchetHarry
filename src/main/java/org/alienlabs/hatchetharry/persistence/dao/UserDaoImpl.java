package org.alienlabs.hatchetharry.persistence.dao;

import org.alienlabs.hatchetharry.model.User;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class UserDaoImpl implements UserDao {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private SessionFactory factory;

	public UserDaoImpl() {
	}

	/**
	 * Setter for session factory. Spring will use this to inject the session
	 * factory into the dao.
	 *
	 * @param _factory hibernate session factory
	 */
	@Required
	public void setSessionFactory(final SessionFactory _factory) {
		this.factory = _factory;
	}

	/**
	 * Helper method for retrieving hibernate session
	 *
	 * @return hibernate session
	 */
	@Override
	public Session getSession() {
		return this.factory.getCurrentSession();
	}

	/**
	 * Load a {@link User} from the DB, given it's <tt>id</tt> .
	 *
	 * @param id The id of the User to load.
	 * @return User
	 */
	@Override
	@Transactional(readOnly = true)
	public User load(final String username) {
		return (User) this.getSession().get(User.class, username);
	}

	/**
	 * Save the User to the DB
	 *
	 * @param User
	 * @return persistent instance of User
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public User save(final User user) {
		return (User) this.getSession().merge(user);
	}

	/**
	 * Delete a {@link User} from the DB, given it's <tt>id</tt>.
	 *
	 * @param id The id of the User to delete.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(final String username) {
		this.getSession().delete(this.load(username));
	}

}
