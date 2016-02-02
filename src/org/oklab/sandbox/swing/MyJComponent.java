package org.oklab.sandbox.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

public class MyJComponent extends JComponent implements InputMethodListener, InputMethodRequests {

	private static final Logger logger = Logger.getLogger(MyJComponent.class.getName());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2830614566584256739L;

	private static final int LEFT_PADDING = 1;
	
	private AttributedString composedTextString;
	private AttributedString commitedTextString;
	private StringBuilder committedText;
	private TextHitInfo caret;
	
	public MyJComponent() {
		super();
		//logger.setLevel(Level.FINE);
		System.out.println("instanciate");
		
		committedText = new StringBuilder();
		
		setVisible(true);
		setEnabled(true);
		
		enableInputMethods(true);
		addInputMethodListener(this);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		// set antialias.
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		FontRenderContext frc = g2d.getFontRenderContext();

		commitedTextString = new AttributedString("helloこんにちは");

		TextLayout layout = new TextLayout(commitedTextString.getIterator(), frc);
		
		layout.draw(g2d, LEFT_PADDING, layout.getAscent());
	}
	
	// how to use InputMethod.
	// 1. override getInputMethodRequests.
	// 2. implement InputMethodListener.
	// 3. implement  InputMethodRequest.
	///////////////////////////////////////////////////////////////////////////
	// require override for InputMethod.
	///////////////////////////////////////////////////////////////////////////
	/**
	 * this class implement ImputMethedRequest. so return own InputMethodRequest. 
	 * @return own InputMethodRequest.
	 */
	@Override
	public InputMethodRequests getInputMethodRequests() {
		System.out.println("getInputMethodRequests");
		return this;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// InputMethodRequest implements
	///////////////////////////////////////////////////////////////////////////
	/**
	 * implement InputMethodListener method.
	 */
	@Override
	public void inputMethodTextChanged(InputMethodEvent event) {
		StringBuilder sb = new StringBuilder();
	    sb.append("#Called inputMethodTextChanged");
	    sb.append("\t ID: " + event.getID());
	    sb.append("\t timestamp: " + new java.util.Date(event.getWhen()));
	    sb.append("\t parmString: " + event.paramString());
	    logger.fine(sb.toString());
	    
	    System.out.println("handle");
	    
	    caret = event.getCaret();
		event.consume();
		
		repaint();
	}
	/**
	 * implement InputMethodListener method.
	 */
	@Override
	public void caretPositionChanged(InputMethodEvent event) {
		caret = event.getCaret();
		event.consume();
	}
	
	///////////////////////////////////////////////////////////////////////////
	// InputMethodRequest implements
	///////////////////////////////////////////////////////////////////////////
	@Override
	public Rectangle getTextLocation(TextHitInfo offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextHitInfo getLocationOffset(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInsertPositionOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, Attribute[] attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCommittedTextLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AttributedCharacterIterator cancelLatestCommittedText(Attribute[] attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttributedCharacterIterator getSelectedText(Attribute[] attributes) {
		// TODO Auto-generated method stub
		return null;
	}
}
