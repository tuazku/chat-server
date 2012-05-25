/**
 * 
 */
package chat.app.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Azamat Turgunbaev
 *
 */
public class NewClient implements Runnable {

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private Socket connection;
	
	public NewClient( Socket socket ){
		
		connection = socket;
		
		try {
			getStreams();		
		} 
		catch ( IOException e ) {
			e.printStackTrace();
			System.exit(1);
		}
		finally{
			closeConnection();
		}
	}
	
	private void getStreams() throws IOException {
		
		outputStream = new ObjectOutputStream( connection.getOutputStream() );
		outputStream.flush();
		inputStream = new ObjectInputStream( connection.getInputStream() );
	}
	
	private void closeConnection() {
		
		try {
			outputStream.close();
			inputStream.close();
			connection.close();
		} 
		catch( IOException e ) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
					
	}
	
}
