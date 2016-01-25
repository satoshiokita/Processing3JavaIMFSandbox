package org.oklab.sandbox.mycomponent;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// AWTアプリ
public class Main extends Frame {

	private static final long serialVersionUID = 1L;
	
	public Main(String title) {
		setTitle(title);
		setSize(400, 400);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				dispose();
			}
		});
		setLayout(new GridLayout(3, 1, 10, 10));
		addComponents();
		// pack();
	}

	private void addComponents() {
		add(new Button("ダミーボタン1"));
		add(new MyTextComponent());
		add(new MemoIM());
		//add(new Button("ダミーボタン2"));
	}

	public static void main(String[] args) {
		Main app = new Main("hello awt and imf");
		app.setVisible(true);
	}
}
