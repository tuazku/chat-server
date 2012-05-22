package chat.app.client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import chat.model.User;
import chat.model.dao.UserDao;
import chat.model.dao.impl.UserDaoImpl;

/**
 * @author Azamat Turgunbaev
 *
 */
public class Login extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8226828221543501499L;
	
	private JTextField userName;
	private JPasswordField userPassword;
	private JButton button;
	
	private List<User> userList;
	
	Client client;
	
	private UserDao userDao = new UserDaoImpl();
	
	public Login(){
		
		super("Login | Chat Application");
		setLayout( new FlowLayout() );
		
		userName = new JTextField(20);
		add(userName);
		
		userPassword = new JPasswordField(20);
		add(userPassword);
		
		button = new JButton("Login");
		
		button.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				userList = userDao.listUser();
				
				for( User user : userList ) {
				
					if( user.getUserName().equals(userName.getText()) && user.getPassword().equals( new String( userPassword.getPassword() ) ) ) {
			
						try {
							JFrame a = new JFrame();
							client = new Client("127.0.0.1");
							a.add(client);
						
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
					}
					else {
						JOptionPane.showMessageDialog(null,"The user name or password is incorrect");
					}
				}		
			}
		});
		
		add(button);
		
		setSize( 300, 200);
		setVisible(true);
	}
}
