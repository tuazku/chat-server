package chat.app.server;
import java.io.IOException;

import javax.swing.JFrame;
/**
 * @author Azamat Turgunbaev
 *
 */
public class ServerTest{

	public static void main( String [] args ) throws IOException {
		
		Server serverApplication = new Server();
		serverApplication.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		serverApplication.setVisible( true );
		serverApplication.setSize(300, 400);
		serverApplication.runServer();
	}
}
