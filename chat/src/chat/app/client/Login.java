package chat.app.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
	
	private static final long serialVersionUID = -8226828221543501499L;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	//GUI FIELD
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	
	private JTextField userName;
	private JPasswordField password;

	private JPanel userNamePanel;
	private JPanel passwordPanel;
	
	private JButton loginButton;
	private JButton registrationButton;
	
	//ACTION FIELDS 
	private User currentUser;
	private boolean userFound;

	private List<User> userList;	
	private UserDao userService = new UserDaoImpl();
	
	public Login(){
		
		super("Login | Chat Application");
		setLayout( new FlowLayout() );
			
		userNameLabel = new JLabel( "User Name :" );
		passwordLabel = new JLabel( "Password  :" );
		
		userName = new JTextField(15);	
		password = new JPasswordField(15);
		
		userNamePanel = new JPanel();
		passwordPanel = new JPanel();
		
		userNamePanel.add(userNameLabel, BorderLayout.WEST);
		userNamePanel.add(userName, BorderLayout.EAST);
		passwordPanel.add(passwordLabel, BorderLayout.WEST);
		passwordPanel.add(password, BorderLayout.EAST);
		
		loginButton = new JButton("LOGIN");
		loginButton.addActionListener( new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				
				userList = userService.listUser();
				userFound = false;
								
				for( User user : userList ) {
				
					if( user.getUserName().equals(userName.getText()) && user.getPassword().equals( new String( password.getPassword() ) ) ){ 	
						currentUser = user;
						userFound = true;
					}
				}
				
				if( !userFound ) {
					JOptionPane.showMessageDialog(null, "The user name or password is incorrect");
				}
				else {
				
					if( !currentUser.isOnline() ) {
						
						userService.setOnline( currentUser, true );
						
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
		
		registrationButton = new JButton( "REGISTRATION" );
		registrationButton.addActionListener( new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				Register register = new Register();
				register.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
				register.setVisible(true);
			}
		});
		
		add(userNamePanel);
		add(passwordPanel);
		add(loginButton);
		add(registrationButton);
		
		setSize( 320, 200 );
		setVisible(true);
	}
};