
package chat.app.client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
public class Register extends JFrame{

	private static final long serialVersionUID = -4279142334613043286L;
	
	ExecutorService executorService = Executors.newCachedThreadPool();
	
	//GUI FIELDS
	private JLabel nameLabel; 
	private JLabel surnameLabel;
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	private JLabel passwordRepeatLabel;
	
	private JTextField name;
	private JTextField surname;
	private JTextField userName;
	private JPasswordField password;
	private JPasswordField passwordConfirm;
	
	private JPanel namePanel;
	private JPanel surnamePanel;
	private JPanel userNamePanel;
	private JPanel passwordPanel;
	private JPanel passwordConfirmPanel;
	
	private JButton registerButton;
	
	//ACTION FIELDS
	private User newUser;
	private List<User> userList = new ArrayList<>();
	private UserDao userService = new UserDaoImpl();
	private boolean canRegister;
	
	public Register() {
		
		super( "User Registration | Chat Application");
		setLayout( new FlowLayout() );
		
		nameLabel = new JLabel( "Name                       :" );
		surnameLabel = new JLabel( "Surname                 :" );
		userNameLabel = new JLabel( "User Name              :" );
		passwordLabel = new JLabel( "Password               :" );
		passwordRepeatLabel = new JLabel( "Confirm Password :" );
		
		name = new JTextField(15);
		surname = new JTextField(15);
		userName = new JTextField(15);
		password = new JPasswordField(15);
		passwordConfirm = new JPasswordField(15);
		
		namePanel = new JPanel();
		surnamePanel = new JPanel();
		userNamePanel = new JPanel();
		passwordPanel = new JPanel();
		passwordConfirmPanel = new JPanel();
		
		namePanel.add(nameLabel);
		namePanel.add(name);
		surnamePanel.add(surnameLabel);
		surnamePanel.add(surname);
		userNamePanel.add(userNameLabel);
		userNamePanel.add(userName);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(password);
		passwordConfirmPanel.add(passwordRepeatLabel);
		passwordConfirmPanel.add(passwordConfirm);
	
		registerButton = new JButton( "REGISTER" );
		
		registerButton.addActionListener( new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
			
				try {
					validateFormFields();
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		});
		
		add( namePanel );
		add( surnamePanel );
		add( userNamePanel );
		add( passwordPanel );
		add( passwordConfirmPanel );
		add( registerButton );
		
		setSize( 310, 300 );
		setVisible(true);
	}
	
	public void validateFormFields() throws Exception {
		
		userList = userService.listUser();
		
		if( name.getText().isEmpty() ) {
			JOptionPane.showMessageDialog(null, "\"Name\" field is not filled" );
		}
		else if( surname.getText().isEmpty() ) {
			JOptionPane.showMessageDialog(null, "\"Surname\" field is not filled" );
		}
		else if( userName.getText().isEmpty() ) {
			JOptionPane.showMessageDialog(null, "\"User Name\" field is not filled" );
		}
		else if( new String(password.getPassword()).equals("") ) {
			JOptionPane.showMessageDialog(null, "\"Password\" field is not filled" );
		}
		else if( new String(passwordConfirm.getPassword()).equals("") ) {
			JOptionPane.showMessageDialog(null, "\"Confirm Password\" field is not filled" );
		}			
		else {
			if( new String(password.getPassword()).equals( new String(passwordConfirm.getPassword()))) {	
				createNewUser();
				validateNewUser();
				if( canRegister ) {
					userService.register(newUser);
					setVisible(false);
					
					/*Client client = new Client("127.0.0.1", newUser );
					client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					client.setVisible( true );
					executorService.execute(client);*/
				}
			}
			else {
				new JOptionPane();
				JOptionPane.showMessageDialog(null, "Passwords do not match\nPlease re enter again" );
			}
		}
	}
	
	public void createNewUser() {
		
		newUser = new User();
		
		newUser.setName(name.getText().toString());
		newUser.setSurname(surname.getText());
		newUser.setUserName(userName.getText());
		newUser.setPassword(new String(password.getPassword()));
		newUser.setOnline(true);
	}
	
	public void validateNewUser() {
			
		canRegister = true;
		
		if( userList.isEmpty() ) {
			userService.register(newUser);
			JOptionPane.showMessageDialog(null, "Registration successfull. You can authorize yoursef.");
			setVisible(false);
		}
		else {
			for( User user : userList ) {
				if( user.getUserName().equals(newUser.getUserName())) {
					JOptionPane.showMessageDialog(null, "This \"User Name\" already in use.\nPlease use another \"User Name\"" );
					canRegister = false;
					break;
				} 	
			}
		}
	}
}