package org.oklab.sandbox.antialias;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main extends JFrame {
	
	MyComponent myComponent;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4648172894076113183L;

	public Main(String title) {
		
		myComponent = new MyComponent();
		
		setTitle(title);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(600, 600));
		
		getContentPane().add(myComponent);
		
		pack();
	}

	public static void main(String[] args) {
		//System.setProperty("awt.useSystemAAFontSettings", "on");
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JFrame frame = new Main("アンチエイリアス");
					frame.setVisible(true);
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
