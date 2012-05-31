/**
 * 
 */
package chat.app.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

import chat.model.User;
import chat.model.dao.UserDao;
import chat.model.dao.impl.UserDaoImpl;

/**
 * @author Azamat Turgunbaev
 *
 */
public class NewClient implements Runnable {

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private List<NewClient> clientList;
	private List<User> userList;	
	private List<User> onlineList;
	private String onlineClients = "";
	
	private UserDao userService = new UserDaoImpl();
	private User user;
	
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
	}
		
	public void run() {
			
		try {
			loginUser();
			processConnection();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeConnection();
		}
	}
	
	public void getStreams() throws IOException {
		
		outputStream = new ObjectOutputStream( connection.getOutputStream() );
		outputStream.flush();
		inputStream = new ObjectInputStream( connection.getInputStream() );
	}

	public void processConnection() throws IOException {
		
		String message = "";
		
		do {
			try {
				message = (String)inputStream.readObject();
				StringTokenizer tokens = new StringTokenizer( message, "<<>>" );
				
				String receiver = null;
					
				if( tokens.hasMoreTokens() ){
					receiver = tokens.nextToken();
					if( tokens.hasMoreTokens()) {
						message = tokens.nextToken();	
					}
				}
									
				NewClient targetClient = null;
				boolean hasTarget = false;
								
				for( NewClient count : clientList ){
					if( ( user.getName() + " " + user.getSurname()).equals( receiver) ){
						targetClient = count;
						hasTarget = true;
						break;
					}
				}
				
				if(hasTarget){
					targetClient.sendMessage( message );
				}
			}
			catch ( ClassNotFoundException e ) {
				
			}
		} while ( !message.equals( "TERMINATE" ));
	}
	
	public void sendMessage( String message ) {
		
		try {
			outputStream.writeObject( message );
			outputStream.flush();
		}
		catch (IOException e) {
			
		}
	}
	
	public void closeConnection() {
		
		try {
			outputStream.close();
			inputStream.close();
			connection.close();
		} 
		catch( IOException e ) {
			e.printStackTrace();
		}
	}
	
	public void loginUser( ) throws ClassNotFoundException, IOException {
		
		String message = "";
		
		try {
			message = (String) inputStream.readObject();
		} catch ( IOException e) {
			e.printStackTrace();
		}
		
		StringTokenizer tokens = new StringTokenizer(message, "<<>>");
		
		boolean userFound = false;
		String loginInfo[] = new String[2];
		
		for( int i = 0; i < 2; i++ ) {
			loginInfo[i] = tokens.nextToken();
		}
	
		getOnlineList();
		userList = userService.listUser();
		
		for( User user : userList ) {
						
			if( user.getUserName().equals( loginInfo[0] ) && user.getPassword().equals( loginInfo[1] ) ){ 	
				this.user = user;
				userFound = true;
				break;
			}
		}
			
		if( userFound ) {
			login( user );
		}
		else {
			sendMessage( "NOT FOUND" );
		}
	}
	
	public void login( User user ) throws IOException {
	
		if( !user.isOnline() ) {
			userService.setOnline(user, true);
			sendMessage( user.getName() + " " + user.getSurname() + "<<>>" + onlineClients );
		}
		else {
			sendMessage( "ONLINE" );
		}
	}
	
	public void getOnlineList() {
		
		int counter = 0;
		onlineList = userService.onlineList();
		
		for( User user : onlineList ) {
			if( counter < onlineList.size() - 1) {
				onlineClients += user.getName() + " " + user.getSurname() + "<<>>";
				counter++;
			}
			else {
				onlineClients += user.getName() + " " + user.getSurname();
			}
		}
	}
	
	public List<NewClient> getClientList() {
		return clientList;
	}

	public void setClientList(List<NewClient> clientList) {
		this.clientList = null;
		this.clientList = clientList;
	}	
}
