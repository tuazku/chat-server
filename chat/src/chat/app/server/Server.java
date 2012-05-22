package chat.app.server;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 * @author Azamat Turgunbaev
 *
 */
public class Server extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField enterField;
	private JTextArea displayArea;
	
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private ServerSocket server;
	private Socket connection;
	
	private int counter = 1;
	
	public Server() {
		
		super("Server Application with GUI");
		
		enterField = new JTextField();
		enterField.setEditable(false);
		enterField.addActionListener(
			new ActionListener() {
				
				public void actionPerformed( ActionEvent event ) {
					
					sendMessage( event.getActionCommand() );
					enterField.setText("");
				}
			}
		);
		
		add( enterField, BorderLayout.NORTH);
		
		displayArea = new JTextArea();
		
		add( new JScrollPane( displayArea ));
		
		setSize( 300, 500 );
		setVisible( true );
	}
	
	public void runServer() {
		
		try {
			
			server = new ServerSocket(12345, 10);
		
			while( true ) {
			
				try {
					waitConnection();
					getStreams();
					processConnection();
				} 
				catch ( EOFException e) {
					displayMessage("\nServer terminated connection");
				}
				finally {
					closeConnection();
					++counter;
				}
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void waitConnection() throws IOException {
		displayMessage("Waiting for connection\n");
		connection = server.accept();
		displayMessage("Connection " + counter + " received from host " + connection.getInetAddress().getHostAddress());
	}
	
	private void getStreams() throws IOException {
		outputStream = new ObjectOutputStream( connection.getOutputStream() );
		outputStream.flush();
		
		inputStream = new ObjectInputStream( connection.getInputStream() );
	}
	
	private void processConnection() throws IOException {
		String message = "Connection Successful";
		sendMessage( message );
		
		enterField.setEditable( true );
		
		do {
			try {
				message = (String)inputStream.readObject();
				displayMessage( "\n" + message );
			}
			catch ( ClassNotFoundException e ) {
				displayMessage("\nUnknown object type received");
			}
		} while ( !message.equals( "CLIENT>>>> TERMINATE" ));
	}
	
	private void closeConnection() {
		displayMessage( "\nTerminating connection\n");
		enterField.setEditable(false);
		
		try {
			outputStream.close();
			inputStream.close();
			connection.close();
		} 
		catch( IOException e ) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage( String message ) {
		
		try {
			outputStream.writeObject( "SERVER>>> " + message );
			outputStream.flush();
			displayMessage( "\nSERVER>>> " + message );
		}
		catch (IOException e) {
			displayArea.append( "Error writing Object" );
		}
	}
	
	private void displayMessage( String message ) {
		displayArea.append( message );	
	}
}
