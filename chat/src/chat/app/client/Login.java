package chat.app.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author Azamat Turgunbaev
 *
 */
public class Login extends JFrame{
	
	private static final long serialVersionUID = -8226828221543501499L;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	//GUI FIELDS
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	
	private JTextField userName;
	private JPasswordField password;

	private JPanel userNamePanel;
	private JPanel passwordPanel;
	
	private JButton loginButton;
	private JButton registrationButton;
	
	//ACTION FIELDS 
	
	private String[] clientList;
	private String server;
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	public Login( String host ){
		
		super("Login | Chat Application");
		setLayout( new FlowLayout() );
		
		server = host;
		
		try {
			connectToServer();
			getStreams();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
										
				sendMessage(  userName.getText() + "<<>>" + new String( password.getPassword() ) );
				
				try {
					String message = (String) inputStream.readObject();
					StringTokenizer tokens = new StringTokenizer( message, "<<>>");
										
					if( tokens.countTokens() > 2 ){
						
						clientList = new String[tokens.countTokens()];
						int counter = 0;
						
						while( tokens.hasMoreTokens() ) {
							clientList[ counter++ ] = tokens.nextToken();
							System.out.println( "CLIENT LIST" + clientList[counter - 1] );
							Thread.sleep(5000);
						}
					}
					else if( tokens.countTokens() == 2 ) {
						
						String name = null;
						String surname = null;
						
						while( tokens.hasMoreTokens() ) {
							name = tokens.nextToken();
							surname = tokens.nextToken();
						}
						
						setVisible(false);
						Client client = new Client( socket, name, surname, clientList );
						client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
						executorService.execute(client);
						closeConnection();
					}
					else {
						String answer = null;
					
						if( tokens.hasMoreTokens() ) {
							answer = tokens.nextToken();
						}
						
						if( answer.equals( "ONLINE" ) ) {
							JOptionPane.showMessageDialog(null, "This user is already authorized");
						}
						else if( answer.equals( "NOT FOUND") ) {
							JOptionPane.showMessageDialog(null, "The user name or password is incorrect");
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
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
	
	public void connectToServer() throws IOException{
		
		socket = new Socket( InetAddress.getByName(server), 12345 );		
	}
	
	public void getStreams() throws IOException {
		
		outputStream = new ObjectOutputStream( socket.getOutputStream() );
		outputStream.flush();
		
		inputStream = new ObjectInputStream( socket.getInputStream() );
	}
	
	public void closeConnection() throws Exception{
		
		try{
			outputStream.close();
			inputStream.close();
			socket.close();
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
		finally{
			System.exit(0);	
		}
	}

	public void sendMessage( String message ) {
		
		try {
			outputStream.writeObject( message );
			outputStream.flush();
		} catch (IOException e) {
			System.out.println( "Error writing object" );
		}
	}
}