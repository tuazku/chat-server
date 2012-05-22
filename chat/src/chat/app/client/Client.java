package chat.app.client;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * @author Azamat Turgunbaev
 *
 */
public class Client extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField enterField;
	private JTextArea displayArea;
	
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private String message = null;
	private String server;
	private Socket socket;
	
	public Client( String host ) throws Exception {
		
		super("Client");
		server = host;
		
		enterField = new JTextField();
		enterField.setEditable(false);
		enterField.addActionListener(
			new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					sendData( event.getActionCommand() );
					enterField.setText("");
				} 
		});
		
		add(enterField, BorderLayout.NORTH);
		
		displayArea = new JTextArea();
		add( new JScrollPane(displayArea));
		
		setSize(300, 400);
		setVisible(true);	
		
		runClient();
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
		
		displayMessage( "Connecter to " + socket.getInetAddress().getHostName());
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
		while (!message.equals("SERVER>>> TERMINATE"));
		
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
			for( int i = 5; i > 0; i-- ) {
				displayMessage("\nApllication will be closed afert " + i + " seconds");
				Thread.sleep(1000);
			}
			System.exit(0);
				
		}
	}
	
	public void sendData( String data ) {
		
		try{
			outputStream.writeObject( "CLIENT>>>> " + data);
			outputStream.flush();
			displayMessage( "\nCLIENT>>>> " + data );
		}
		catch( IOException e ) {
			displayMessage( "\nError writing object" );
		}
	}
	
	public void displayMessage( String message ){
		displayArea.append(message);
	}
}