package chat.app.client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
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
	
	ExecutorService executor = Executors.newCachedThreadPool();
	
	private JTextField userName;
	private JPasswordField userPassword;
	private JButton button;
	private User currentUser;
	private boolean userFound;
	
	private List<User> userList;
		
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
				userFound = false;
								
				for( User user : userList ) {
				
					if( user.getUserName().equals(userName.getText()) && user.getPassword().equals( new String( userPassword.getPassword() ) ) ){ 	
						currentUser = user;
						userFound = true;
					}
				}
				
				if( !userFound ) {
					JOptionPane.showMessageDialog(null, "The user name or password is incorrect");
				}
				else {
				
					if( !currentUser.isOnline() ) {
						
						userDao.setOnline( currentUser, true );
						
						try {
							setVisible(false);
							Client client = new Client("127.0.0.1", currentUser );
							client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
							executor.execute(client);
						} 
						catch (Exception e1) {
							e1.printStackTrace();
						}
						finally{
							
						}
					}
					else {
						JOptionPane.showMessageDialog(null, "This user is already authorized");
					}					
				}					
			}
					
		});
		
		add(button);
		
		setSize( 300, 200);
		setVisible(true);
	}
}