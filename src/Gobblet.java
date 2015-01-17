/*	Ofek Gila
	January 11th, 2014
	Gobblet.java
	This program runs the UI for the well-known game of Gobblet
*/
import javax.swing.JFrame;
import javax.swing.JApplet;

public class Gobblet extends JApplet	{
	public static void main(String[] pumpkins) {
		JFrame frame  = new JFrame("Gobblet");
		frame.setSize(1500, 1100);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new GobbletPanel());
		frame.setVisible(true);
	}
	public void init()	{
		setContentPane(new GobbletPanel());
	}
}