package org.oklab.sandbox.fastfontload;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
		main.getMonoFontList();
	}

	private static List<Font> getMonoFontList() {
		long t1_1 = System.currentTimeMillis();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = ge.getAllFonts();
		long t2_1 = System.currentTimeMillis();
		System.out.println("t2_1 - t1_1:" + (t2_1 - t1_1));
		
		boolean isAntiAliased = true;
		boolean usesFractionalMetrics = true;
		FontRenderContext frc = new FontRenderContext(new AffineTransform(), isAntiAliased, usesFractionalMetrics);
		
		System.out.println("font count = " + fonts.length);
		long t3_1 = System.currentTimeMillis();
		System.out.println("t3_1 - t2_1:" + (t3_1 - t2_1));
		List<Font> outgoing = new ArrayList<Font>();
		for (Font font : fonts) {
			//System.out.println(font);
			if (font.getStyle() == Font.PLAIN 
					&& font.canDisplay('i')
					&& font.canDisplay('M')
					&& font.canDisplay('.')
					&& font.canDisplay(' ')
					) {
				//double w = font.getStringBounds(" ", frc).getWidth();
		        //Rectangle2D iBounds = font.getStringBounds("i", frc);
		        //Rectangle2D mBounds = font.getStringBounds("M", frc);
		        //Rectangle2D dBounds = font.getStringBounds(".", frc);
/*
		        if (w == iBounds.getWidth()
		        		&& w == mBounds.getWidth()
		        		&& w == dBounds.getWidth()) {
		        		*/
		    //    if (iBounds.getWidth() == mBounds.getWidth()) {
					outgoing.add(font);
					//System.out.print(".");
		      //  }
			}
		}
		System.out.println("");
		long t4_1 = System.currentTimeMillis();
		System.out.println("t4_1 - t3_1:" + (t4_1 - t3_1));
		
		/*
		for (String name : ge.getAvailableFontFamilyNames()) {
			System.out.println("name=" + name);
		}
		*/
		List<Font> monoFonts1 = new ArrayList<Font>();
		 for (Font font : fonts) {
			 if (font.getStyle() == Font.PLAIN 
					 && font.canDisplay('i')
					 && font.canDisplay('M')
					 && font.canDisplay('.')
					 && font.canDisplay(' ')
					 ) {
				double sBoundsW = font.getStringBounds(" ", frc).getWidth();
		        Rectangle2D iBounds = font.getStringBounds("i", frc);
		        Rectangle2D mBounds = font.getStringBounds("M", frc);
		        Rectangle2D dBounds = font.getStringBounds(".", frc);
		        if (iBounds.getWidth() == sBoundsW 
		        		&& mBounds.getWidth() == sBoundsW
		        		&& dBounds.getWidth() == sBoundsW) {
		            monoFonts1.add(font);
		        }
		    }
		 }
		long t5_1 = System.currentTimeMillis();
		System.out.println("t5_1 - t4_1:" + (t5_1 - t4_1));

		
		System.out.println("outgoing.size()=" + outgoing.size());
		System.out.println("monoFonts1.size()=" + monoFonts1.size());
		return outgoing;
	}

}
