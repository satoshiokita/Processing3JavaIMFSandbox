package org.oklab.sandbox.antialias;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class MyComponent extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2975256927913508442L;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		//test1(g);
		//test2(g);
		test3(g);
	}
	
	// understand baseline, decent, accent.
	private void test3(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		Font font = new Font("MS　ゴシック", Font.PLAIN, 12); // MS Gothic
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout layout = new TextLayout("helloこんにちは", font, frc);
		layout.draw(g2d, 0, 0);
	}

	// print font info
	private void test2(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		int line = 1;
		for (int i = 10; i <=24; i +=2) {
			Font font = new Font("MS　ゴシック", Font.PLAIN, i);
			FontMetrics fm = super.getFontMetrics(font);
			System.out.println("*****1");
			System.out.println(fm);
			System.out.println("Leading(標準レディング (行間の間隔)):" + fm.getLeading());
			
			g2d.setFont(font);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			FontRenderContext frc = g2d.getFontRenderContext();
			TextLayout layout = new TextLayout("アンチエイリアスフォントテスト", font, frc);
			layout.draw(g2d, 0, line * 50);
			
			 Rectangle2D bounds = layout.getBounds();
			   bounds.setRect(bounds.getX()+ 0,
			                  bounds.getY()+ (line * 50),
			                  bounds.getWidth(),
			                  bounds.getHeight());
			   g2d.draw(bounds);
			
			System.out.println("*****2" + font);
			System.out.println("有効幅　　:" + layout.getAdvance());
			System.out.println("有効幅(v):" + layout.getVisibleAdvance());
			System.out.println("アセント　　:" + layout.getAscent());
			System.out.println("ディセント　:" + layout.getDescent());
			System.out.println("ベースライン:" + layout.getBaseline());
			System.out.println("境界　　　:" + layout.getBounds());
			line++;
		}
	}
	// how to use TextLayout#draw
	private void test1(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		Font font = new Font("SansSerif", Font.PLAIN, 14);
		g2d.setFont(font);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.BLACK);
		g.drawString("drawStringこんにちはabc文字列アンチエイリアスあり", 50, 50);

		//g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.setColor(Color.BLACK);
		g.drawString("drawStringこんにちはabc文字列アンチエイリアスなし", 50, 100);

		//g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		System.out.println(System.getProperty("awt.useSystemAAFontSettings")); // Java6
		
		g.setColor(Color.BLACK);
		g.drawString("drawStringこんにちはabc文字列アンチエイリアスあり", 50, 150);
		
		// RenderingHints.VALUE_TEXT_ANTIALIAS_ONにすれば、TextLayoutの描画はにはアンチエイリアスがかかる。
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout layout = new TextLayout("TextLayoutで文字列を描画", font, frc);
		//Rectangle2D bounds = layout.getBounds();
		//bounds.setRect(50, 200, 50 + 400, 200 + 50);
		//g2d.draw(bounds);
		layout.draw(g2d, 50, 200);
	}
}
