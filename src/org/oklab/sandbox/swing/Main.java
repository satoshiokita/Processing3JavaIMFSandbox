package org.oklab.sandbox.swing;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main extends JFrame {

	/**
	 * Swing-based Component. extend java.swing.JComponent.
	 */
	MyJComponent myJComponent;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7095934656069201739L;

	public Main(String title) {
		myJComponent = new MyJComponent();

		setTitle(title);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(400, 400));

		getContentPane().add(myJComponent);

		pack();
	}

	public static void main(String[] args) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JFrame frame = new Main("the Swing-Based Component with InputMehtod");
					frame.setVisible(true);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
