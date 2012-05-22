package chat.app.client;
import javax.swing.JFrame;


/**
 * @author Azamat Turgunbaev
 *
 */
public class ClientTest {

	public static void main( String [] args ) throws Exception {
		
		Client clientApplication = new Client("127.0.0.1");
		
		clientApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientApplication.runClient();
	}
}
