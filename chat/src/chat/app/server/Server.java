package chat.app.server;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * @author Azamat Turgunbaev
 *
 */
public class Server extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private JTextArea displayArea;
	
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private ServerSocket server;
	private Socket connection;
		
	private int counter = 0;
	
	public Server() {
		
		super("Server Application with GUI");
		
		try {
			server = new ServerSocket( 12345, 10 );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		displayArea = new JTextArea();
		displayArea.setEditable(false);
		add( new JScrollPane( displayArea ));
		
		setSize( 300, 500 );
		setVisible( true );
	}
	
	public void runServer() throws IOException {
		
		displayMessage( "Server started" );
		while( true ) {
		
			try {
				Socket connection = server.accept();
				NewClient newClient = new NewClient( connection );
				executorService.execute( newClient );
				displayMessage( "\nConnection " + ++counter + " received from " + connection.getInetAddress().getHostAddress() );
			} 
			catch ( EOFException e) {
				displayMessage("\nServer terminated connection");
			}	
		}
	}
		
	private void closeConnection() {
		
		displayMessage( "\nTerminating connection\n");
		
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