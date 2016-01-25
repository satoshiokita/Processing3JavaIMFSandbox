package org.oklab.sandbox.mycomponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 * 自作のAWTテキスト編集コンポーネント
 */
public class MyTextComponent extends Component implements KeyListener, FocusListener, MouseListener {

	private static final long serialVersionUID = -9031222830566012078L;
	private static final int LINE_OFFSET = 10;
	private static final BasicStroke FOCUS_DASH_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER, 10.0f, new float[] { 1.0f, 1.0f }, 0.0f);

	private int textOriginX;
	private int textOriginY;
	private boolean hasFocus;
	private StringBuilder committedText;
	// committedTextの変更でtrue/falseを切り替える変更フラグ。
	private boolean validTextLayout;

	// TextLayoutはコストが高い。
	private TextLayout textLayout;

	public MyTextComponent() {
		committedText = new StringBuilder();

		setSize(200, 200);
		setForeground(Color.BLACK);
		setBackground(Color.WHITE);
		setFontSize(14);
		
		setVisible(true);
		setEnabled(true);

		addFocusListener(this);
		addMouseListener(this);
		addKeyListener(this);
	}

	public void setFontSize(int size) {
		setFont(new Font("MS Gothic", Font.PLAIN, size));
		textOriginX = LINE_OFFSET;
		textOriginY = LINE_OFFSET + size;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		

		// 背景描画
		g.setColor(getBackground());
		Dimension d = getSize();
		g.fillRect(0, 0, d.width, d.height);

		// 枠を描画
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, d.width - 1, d.height - 1);

		// フォーカス時は、破線を描画
		if (hasFocus) {
			g.setColor(Color.BLUE);
			((Graphics2D) g).setStroke(FOCUS_DASH_STROKE);
			g.drawRect(2, 3, d.width - 6, d.height - 6);

			Font font = new Font("SansSerif", Font.PLAIN, 14);
			g2d.setFont(font);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.drawString("あいう", 50, 50);
		}

		// 文字の描画
		// g.setColor(getForeground());
		// Y座標はフォントのベースラインなので、0にすると文字が表示されなくなる。
		// g.drawString(this.testdata, textOriginX, testdataOriginY);

		// ユーザーが入力した文字列を描画
		g.setColor(getForeground());
		TextLayout textLayout = getTextLayout();
		if (textLayout != null) {
			textLayout.draw((Graphics2D) g, textOriginX, textOriginY);
		}

		// フォーカス時はキャレットを描画
		Rectangle rect = getCaretRectangle();
		if (hasFocus && rect != null) {
			g.setXORMode(getBackground());
			g.fillRect(rect.x, rect.y, 2, rect.height);
			g.setPaintMode();
		}
	}
	public TextHitInfo getCaret() {
		// TextLayoutが、TextHitInfoクラスでキャレットや挿入位置の指定を行っている。
		// そのため、自身でキャレット位置の管理を実装したりする必要はない。
		// トレーリングとは、英語のように左から右に文章を書く言語で右端の事.
		
		// (リーディング0)a(トレーリング0)b(トレーリング1)c(トレーリング2)
		//
		// リーディングとは、英語のように左から右に文章を書く言語で左端の事
		// "abc" で TextHitInfo.trailing(0)とした場合、aの右側になる。
		// "abc" で TextHitInfo.trailing(1)とした場合、bの右側になる。
		// "abc" で TextHitInfo.trailing(2)とした場合、cの右側になる。
		return TextHitInfo.trailing(committedText.length() - 1);
	}
	
	public Rectangle getCaretRectangle() {
		TextHitInfo caret = getCaret();
		if (caret == null) {
			return null;
		}
		return getCaretRectangle(caret);
	}
	
	public Rectangle getCaretRectangle(TextHitInfo caret) {
		// TextLayoutは、フォント情報が保持されている入力した文字列と描画コンテキストが含まれるオブジェクト
		TextLayout textLayout = getTextLayout();
		int caretLocation = 0;
		if (textLayout != null) {
			caretLocation = Math.round(textLayout.getCaretInfo(caret)[0]);
		}
		System.out.println("キャレットロケーション:" + caretLocation);
		
		// アセント、ディセント、レディング、高さ、フォントオブジェクトなどの情報が入ったメトリックス
		FontMetrics metrics = getGraphics().getFontMetrics();
		// フォントの細かい情報から正確にキャレット用の四角形を作成
		Rectangle rect = new Rectangle(textOriginX + caretLocation,
				textOriginY - metrics.getAscent(),
				0,
				metrics.getAscent() + metrics.getDescent());
		
		System.out.println(rect);
		return rect;
	}
	
    public Point getTextOrigin() {
        return new Point(textOriginX, textOriginY);
    }

	// TextLayoutは、各文字のフォントを定義する必要がある。
	public AttributedCharacterIterator getDisplayText() {
		AttributedString as = new AttributedString(committedText.toString());
		if (committedText.length() > 0) {
			as.addAttribute(TextAttribute.FONT, getFont());
		}
		return as.getIterator(); // 実体はAttributedStringIterator
	}

	// 確定されたテキストとコンテキストからTextLayoutを作成する。
	public TextLayout getTextLayout() {
		if (!validTextLayout) {
			textLayout = null;
			// 入力された文字列をiteratorで取得
			AttributedCharacterIterator text = getDisplayText();
			if (text.getEndIndex() > text.getBeginIndex()) {
				Graphics2D g2d = ((Graphics2D) getGraphics());
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				//g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				FontRenderContext ctx = g2d.getFontRenderContext();
				textLayout = new TextLayout(text, ctx);
			}
		}
		validTextLayout = true;
		return textLayout;
	}

	// 文字が変更されたら呼ばれる。
	public void invalidateTextLayout() {
		validTextLayout = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println(e.getKeyCode());
		char keyChar = e.getKeyChar();
		if (keyChar == '\b') { // backspace.
			int len = committedText.length();
			if (len > 0) {
				// バックスペースで一文字消す。
				committedText.setLength(len - 1);
				// キャッシュされたテキストレイアウトを無効化
				invalidateTextLayout();
			}
		} else {
			insertCharacter(keyChar);
		}
		e.consume(); // 伝達停止
		repaint();
	}

	public void insertCharacter(char c) {
		committedText.append(c);
		invalidateTextLayout();
	}
	
	
	// メソッド名は、InputMethodRequestからきている。
	public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex) {
		AttributedString string = new AttributedString(committedText.toString());
		return string.getIterator(null, beginIndex, endIndex);
	}
	
	// メソッド名は、InputMethodRequestからきている。
	public int getCommittedTextLength() {
		return committedText.length();
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void focusGained(FocusEvent e) {
		System.out.println("フォーカスされた。");
		hasFocus = true;
		repaint();
	}

	@Override
	public void focusLost(FocusEvent e) {
		System.out.println("フォーカスから離れた。");
		hasFocus = false;
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("マウスがクリックされた。");
		this.requestFocus(); // マウスがクリックされたら、フォーカスを合わせる。
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
