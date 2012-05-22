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
}
