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
import java.net.Socket;
import java.util.List;

import javax.swing.DefaultListModel;
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
		
		private static final long serialVersionUID = 1L;
		
		private JTextField enterField;
		private JTextArea displayArea;
		private JList<?> userJList;
		private JPanel displayPanel;
		
		private ObjectOutputStream outputStream, output;
		private ObjectInputStream inputStream, input;
		
		private UserDao userDao = new UserDaoImpl();
		private User currentUser;
		
		private DefaultListModel<String> users = new DefaultListModel<>();
		private String message = null;
		private Socket socket;
		
		private int counter = 0;
	
		private String sendTo = "";
		private String[] onlineList;
		
		public Client( Socket socket, ObjectOutputStream output, ObjectInputStream input, final String nameSurname, String[] onlineList ) throws Exception {
			
			super("Client | " + nameSurname );
			this.socket = socket;
			this.setOnlineList(onlineList);
			this.output = output;
			this.input = input;
			
			//Enter Field
			enterField = new JTextField();
			enterField.setEditable(false);
			System.out.println(getSendTo());
			enterField.addActionListener(
			
				new ActionListener() {
					
					String message;
					
					public void actionPerformed(ActionEvent event) {
						
						if( getSendTo() == "" ){
							message = nameSurname +  " :\n   " + event.getActionCommand();
							displayMessage( "\n" + message );
						}
						else{
							message = nameSurname + " :\n   " + event.getActionCommand();
							displayMessage( "\n" + message );
						}
						
						sendMessage( getSendTo() + "<<>>" + message );
						enterField.setText("");
					} 
			});
			add(enterField, BorderLayout.SOUTH);
			
			//User List
			
			userJList = new JList<>( onlineList );
			
			userJList.addListSelectionListener( new ListSelectionListener() {
				
				public void valueChanged(ListSelectionEvent e) {
					setSendTo(userJList.getSelectedValue().toString());
				}
			});		
			
			//Display Area
			displayArea = new JTextArea();
			displayArea.setEditable(false);
			add( new JScrollPane(displayArea));
			
			displayPanel = new JPanel();
			displayPanel.setBackground(Color.white);
			displayPanel.add(userJList);
			add(displayPanel, BorderLayout.EAST );
			
			this.addWindowListener(new java.awt.event.WindowAdapter() {
				
				public void windowClosing( WindowEvent event ){
					try {
						userDao.setOnline( currentUser, false );
						sendMessage("TERMINATE");
					} catch (Exception e) {
						
					} finally{
						try {
							closeConnection();							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			setSize(400, 400);
			setVisible(true);				
		}
	
		@Override
		public void run() {
			try {	
				runClient();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
		public void runClient() throws Exception {
		
		try {
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
	
	public void getStreams() throws IOException {
		
		outputStream = output;
		outputStream.flush();
		
		inputStream = input;
		
		//sendMessage( currentUser.getUserName() );
	}
	
	public void processConnection() throws IOException {
		
		enterField.setEditable(true);
				
		do {
			try{
				message = (String) inputStream.readObject();
				if( !message.equals("redraw")){
					displayMessage( "\n" + message);
				}
				else{
					List<User> onlineList = userDao.onlineList();
					users.add(counter++, onlineList.get(counter).getUserName());
				}
			}
			catch (ClassNotFoundException e) {
				displayMessage("\nUnknown object type received");
			}
		} 
		while (!message.equals("TERMINATE") );
		
		userDao.setOnline( currentUser, false );

	}
	
	public void sendMessage( String message ) {
		
		try{
			outputStream.writeObject( message );
			outputStream.flush();
		}
		catch( IOException e ) {
			displayMessage( "\nError writing object" );
		}
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
	
	public void displayMessage( String message ){
		displayArea.append(message);
	}
	
	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String[] getOnlineList() {
		return onlineList;
	}

	public void setOnlineList(String[] onlineList) {
		this.onlineList = onlineList;
	}
}