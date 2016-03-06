package org.oklab.sandbox.xor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main extends JFrame {
	
	private static final long serialVersionUID = 3976866741723129789L;

	public Main() {
		setTitle("Swing XOR Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(400, 400));
		pack();
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// 全て黒くする
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.GREEN);
		g.fillRect(50, 50, 100, 100);
		
		//g.setColor(Color.WHITE);
		g.setXORMode(Color.BLACK);
		g.fillRect(50 + 25, 50 + 25, 100, 100);
	}

	public static void main(String[] args) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				new Main();
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
