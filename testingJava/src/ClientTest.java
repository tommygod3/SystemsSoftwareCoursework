import javax.swing.JFrame;

public class ClientTest {
	public static void main(String[] args) {
		Client c;
		c = new Client("127.0.0.1");
		c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c.startRunning();
                Client d;
		d = new Client("127.0.0.2");
		d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		d.startRunning();
	}
}
