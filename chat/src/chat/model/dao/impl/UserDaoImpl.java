package chat.model.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import chat.model.User;
import chat.model.dao.UserDao;

/**
 * @author Azamat Turgunbaev
 *
 */
public class UserDaoImpl implements UserDao{

	List<User> onlineList = new ArrayList<>();
		
	SessionFactory factory = new Configuration().configure().buildSessionFactory();
	Session session = factory.openSession();
	
	@Override
	public void register(User user) {
		try{
			session.beginTransaction();
			session.save(user);
			session.getTransaction().commit();
		} catch ( Exception e ){
			e.printStackTrace();
		}
		finally{
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> onlineList() {
		
		session.beginTransaction();
		
		onlineList = session.createQuery("from User where online = TRUE").list(); 
		
		return onlineList;
	}

	@Override
	public void login(User user) {
		// TODO Auto-generated method stub	
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> listUser() {
		return session.createQuery("from User").list();
	}

	@Override
	public void setOnline( User user, boolean value ) {
		
		user.setOnline(value);
		
		session.beginTransaction();
		session.update(user);
		session.getTransaction().commit();
		session.close();
	}
	
	

}
