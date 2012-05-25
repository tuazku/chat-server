package chat.app.client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chat.model.User;
import chat.model.dao.UserDao;
import chat.model.dao.impl.UserDaoImpl;


/**
 * @author Azamat Turgunbaev
 *
 */
	public class Client extends JFrame implements Runnable{
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private JTextField enterField;
		private JTextArea displayArea;
		private JList userList;
		private JPanel displayPanel;
		
		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;
		
		private UserDao userDao = new UserDaoImpl();
		private User currentUser;
		
		private String message = null;
		private String server;
		private Socket socket;
	
		private String sendTo = "";
		
		public Client( String host, User newUser ) throws Exception {
			
			super("Client | " + newUser.getName() + " " + newUser.getSurname() );
			
			currentUser = newUser;
			server = host;	
			
			List<User> usersList = getOnlineUsers();
			int count = usersList.size();
			String users[] = new String[count];
						
			for( User user : usersList )
				users[--count] = user.getName() + " " + user.getSurname();
				
			//Enter Field
			enterField = new JTextField();
			enterField.setEditable(false);
			System.out.println(getSendTo());
			enterField.addActionListener(
			
					
				new ActionListener() {
					
					String message;
					
					public void actionPerformed(ActionEvent event) {
						
						if( getSendTo() == "" ){
							message = (currentUser.getName() + " " + currentUser.getSurname() + " :\n   ") + event.getActionCommand();
							displayMessage( "\n" + message );
						}
						else{
							message = currentUser.getName() + " " + currentUser.getSurname() + " :\n   " + event.getActionCommand();
							displayMessage( "\n" + message );
						}
						
						sendData( getSendTo() + "<<>>" + message );
						enterField.setText("");
					} 
			});
			add(enterField, BorderLayout.SOUTH);
			
			//User List
			userList = new JList(users);
			userList.addListSelectionListener( new ListSelectionListener() {
				
				public void valueChanged(ListSelectionEvent e) {
					setSendTo(userList.getSelectedValue().toString());
				}
			});
			add(userList, BorderLayout.EAST);
			
			//Display Area
			displayArea = new JTextArea();
			displayArea.setEditable(false);
			add( new JScrollPane(displayArea));
			
			displayPanel = new JPanel();
			displayPanel.setBackground( Color.white);
			displayPanel.add(userList);
			add(displayPanel, BorderLayout.EAST );
			
			this.addWindowListener(new java.awt.event.WindowAdapter() {
				
				public void windowClosing( WindowEvent event ){
					try {
						userDao.setOnline( currentUser, false );
						sendData("TERMINATE");
						Thread.sleep(500);
						closeConnection();
					} catch (Exception e) {
					}
				}
			});
						
			setSize(400, 400);
			setVisible(true);	
		}
	
	public void runClient() throws Exception {
		
		try {
			connectToServer();
			getStreams();
			processConnection();
		} 
		catch ( EOFException e ) {
			displayMessage( "\nClient terminated connection" );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			closeConnection();
		}
		
	}
	
	public void connectToServer() throws IOException{
		
		displayMessage( "Attempting to connect to server\n" );
		
		socket = new Socket( InetAddress.getByName(server), 12345 );
		
		displayMessage( "Connected to " + socket.getInetAddress().getHostName());
	}
	
	public void getStreams() throws IOException {
		
		outputStream = new ObjectOutputStream( socket.getOutputStream() );
		outputStream.flush();
		
		inputStream = new ObjectInputStream( socket.getInputStream() );
		
	}
	
	public void processConnection() throws IOException {
		
		enterField.setEditable(true);
		
		do {
			try{
				message = (String) inputStream.readObject();
				displayMessage( "\n" + message);
			}
			catch (ClassNotFoundException e) {
				displayMessage("\nUnknown object type received");
			}
		} 
		while (!message.equals("SERVER>>> TERMINATE") );
		
		userDao.setOnline( currentUser, false );

	}
	
	public void closeConnection() throws Exception{
		
		displayMessage("\nClosing connection");
		enterField.setEditable(false);
		
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
	
	public void sendData( String data ) {
		
		try{
			outputStream.writeObject( data );
			outputStream.flush();
			
		}
		catch( IOException e ) {
			displayMessage( "\nError writing object" );
		}
	}
	
	public void displayMessage( String message ){
		displayArea.append(message);
	}
	
	public List getOnlineUsers(){
		
		UserDao userDao = new UserDaoImpl();
		List<User> userList = new ArrayList<User>();
		
		userList = userDao.onlineList();
		
		return userList;
	}

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	@Override
	public void run() {
		try {
			runClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}