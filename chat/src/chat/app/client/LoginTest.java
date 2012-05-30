/**
 * 
 */
package chat.app.client;

import javax.swing.JFrame;

/**
 * @author Azamat Turgunbaev
 *
 */
public class LoginTest {

	public static void main(String [] args ){
		
		Login login = new Login( "localhost" );
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setVisible(true);
	}
}
