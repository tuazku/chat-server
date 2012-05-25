package chat.model.dao;

import java.util.List;

import chat.model.User;

/**
 * @author Azamat Turgunbaev
 *
 */
public interface UserDao{

	public void register( User user );
	public List<User> onlineList();
	public void login( User user );
	public List<User> listUser();
	public void setOnline( User user, boolean value );
}
