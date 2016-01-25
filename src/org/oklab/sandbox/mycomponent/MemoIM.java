package org.oklab.sandbox.mycomponent;
/*
 * Copyright 2002 Sun Microsystems, Inc. All  Rights Reserved.
 *  
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 * 
 * -Redistributions of source code must retain the above copyright  
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright 
 *  notice, this list of conditions and the following disclaimer in 
 *  the documentation and/or other materials provided with the 
 *  distribution.
 *  
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY 
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY 
 * DAMAGES OR LIABILITIES  SUFFERED BY LICENSEE AS A RESULT OF OR 
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR 
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE 
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, 
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER 
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF 
 * THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *  
 * You acknowledge that Software is not designed, licensed or 
 * intended for use in the design, construction, operation or 
 * maintenance of any nuclear facility. 
 */
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MemoIM extends MyTextComponent implements InputMethodListener, InputMethodRequests {

	private static final long serialVersionUID = -1599818170929963587L;

	// インプットメソッドの入力を保持する。
	private AttributedString composedTextString;
	// 多分実体はAttributedString.AttributedStringIterator.
	// 何故iteratorは別で保持する?
	private AttributedCharacterIterator composedText;

	// インプットメソッド用のキャレット
	private TextHitInfo caret;

	public MemoIM() {
		// java.awt.Component#enableInputMethods
		enableInputMethods(true);
		addInputMethodListener(this);
	}

	// インプットメソッドと通信するためは、InputMethodRequestsオブジェクトを渡してあげる必要がある。
	// on-the-spot編集で必要
	@Override
	public InputMethodRequests getInputMethodRequests() {
		return this;
	}

	// 変換テキストのフォント設定
	@Override
	public void setFontSize(int size) {
		super.setFontSize(size);
		if (composedTextString != null) {
			composedTextString.addAttribute(TextAttribute.FONT, getFont());
		}
	}

	@Override
	public AttributedCharacterIterator getDisplayText() {
		// 変換テキストがない場合は、確定テキストを返す。
		if (composedText == null) {
			return super.getDisplayText();
		}
		// コピーせず、変換テキストと確定テキストを連結したイテレーターを返す。
		return new CompositeIterator(super.getDisplayText(), composedText);
	}

	@Override
	public TextHitInfo getCaret() {
		if (composedText == null) {
			// 変換テキストがない場合は、確定テキストのキャレットを返す。
			return super.getCaret();
		} else if (caret == null) {
			return null;
		}
		// TODO: ここはデバッガーで実際に値を確認してみる。
		// 変換テキストに確定テキストの長さを加味する。
		return caret.getOffsetHit(getCommittedTextLength());
	}

	///////////////////////////////////////////////////////////////////////////////
	// InputMethod Listener
	///////////////////////////////////////////////////////////////////////////////
	private static final Attribute[] CUSTOM_IM_ATTRIBUTES = {
			TextAttribute.INPUT_METHOD_HIGHLIGHT, // インプットメソッドのハイライトスタイル。
	};

	/**
	 * インプットメソッドで文字を変更したら、このリスナーが呼ばれる。
	 * 
	 * @see InputMethodListener#inputMethodTextChanged(InputMethodEvent)
	 */
	@Override
	public void inputMethodTextChanged(InputMethodEvent event) {
		int committedCharacterCount = event.getCommittedCharacterCount();
		AttributedCharacterIterator text = event.getText();
		composedText = null; // 編集中テキスト

		// 確定テキストのコピー
		char c;
		if (text != null) {
			int toCopy = committedCharacterCount;
			c = text.first();
			while (toCopy-- > 0) {
				super.insertCharacter(c);
				c = text.next();
			}
			// whieあとでtextのイテレーターは確定テキストの文字数まで進んだことになる。
			
			// 変換テキストのコピー
			// 変換テキストがある場合
			// (text.getBeginIndex() + committedCharacterCount)で、変換テキストの先頭
			if (text.getEndIndex() - (committedCharacterCount + text.getBeginIndex()) > 0) {
				composedTextString = new AttributedString(text,
						committedCharacterCount + text.getBeginIndex(),
						text.getEndIndex(), CUSTOM_IM_ATTRIBUTES);
				composedTextString.addAttribute(TextAttribute.FONT, getFont());
				// FIXME:波線はない？
				//composedTextString.addAttribute(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
				//composedTextString.addAttribute(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_DASHED);
				// 変換テキストのスタイルを変更してみる。
				composedTextString.addAttribute(TextAttribute.FOREGROUND, Color.GREEN);
				composedTextString.addAttribute(TextAttribute.BACKGROUND, Color.MAGENTA);
				composedTextString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
				//composedTextString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
				
				composedText = composedTextString.getIterator();
			}
		}

		event.consume();
		// 再描画のためにTextLayoutを一度無効
		invalidateTextLayout();
		caret = event.getCaret();
		repaint();
	}

	/**
	 * キャレットの移動イベントで、このリスナーが呼ばれる。
	 * 
	 * @see InputMethodListener#caretPositionChanged(InputMethodEvent)
	 */
	@Override
	public void caretPositionChanged(InputMethodEvent event) {
		this.caret = event.getCaret();
		event.consume();
		repaint();
	}

	/////////////////////////////////////////////////////////////////////////////
	// InputMethodRequests
	// https://docs.oracle.com/javase/jp/6/api/java/awt/im/InputMethodRequests.html
	// Windowsのメモ帳のような、よくあるインプットメソッドの表示は、on-the-spotと呼ばれます。
	///////////////////////////////////////////////////////////////////////////////

	// 現在の変換テキスト内の指定されたオフセットの場所、
	// または確定テキスト内の選択部分の場所を取得します。
	@Override
	public Rectangle getTextLocation(TextHitInfo offset) {
		// おそらく、変換テキストの近くに候補ウィンドウを配置したい場合などにインプットメソッドから呼び出される。
		Rectangle rect;
		if (offset == null) {
			// 確定テキストの
			rect = super.getCaretRectangle();
		} else {
			// 確定テキスト+変換テキストのキャレット位置
			TextHitInfo globalOffset = offset.getOffsetHit(getCommittedTextLength());
			rect = super.getCaretRectangle(globalOffset);
		}

		Point location = getLocationOnScreen();
		rect.translate(location.x, location.y);
		return rect;
	}

	// xy座標が変換テキストにヒットしたかどうかの判定
	// マウスクリック、マウスカーソル処理に使用されます。
	// 指定された画面上の絶対座標 x と y とに対応する変換テキスト内のオフセットを取得します。
	@Override
	public TextHitInfo getLocationOffset(int x, int y) {
		// 変換テキスト上での、xyオフセット
		Point location = getLocationOnScreen(); // このコンポーネントの左上の座標
		Point textOrigin = getTextOrigin(); // テキスト領域のpadding.
		x -= location.x + textOrigin.x;
		y -= location.y + textOrigin.y;
		
		TextLayout textLayout = getTextLayout();
		if (textLayout != null && textLayout.getBounds().contains(x, y)) {
			return textLayout.hitTestChar(x, y).getOffsetHit(-getCommittedTextLength());
		}
		return null;
	}

	// 確定テキストの挿入位置を返します。
	@Override
	public int getInsertPositionOffset() {
		return super.getCommittedTextLength();
	}

	@Override
	public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, Attribute[] attributes) {
		return super.getCommittedText(beginIndex, endIndex);
	}

	// 親クラスで実装済み.
	// @Override
	// public int getCommittedTextLength() {
	// return 0;
	// }

	// Undo機能で使われます。今回は実装しません。
	@Override
	public AttributedCharacterIterator cancelLatestCommittedText(Attribute[] attributes) {
		return null;
	}

	// 編集テキストの範囲選択で使われます。今回は実装しません。
	// 空文字列を返します。
	@Override
	public AttributedCharacterIterator getSelectedText(Attribute[] attributes) {
		return new AttributedString("").getIterator();
	}
}

/**
 * Iterates over the combined text of two AttributedCharacterIterators. Assumes
 * that no annotation spans the two iterators.
 */

class CompositeIterator implements AttributedCharacterIterator {

	AttributedCharacterIterator iterator1;
	AttributedCharacterIterator iterator2;
	int begin1, end1;
	int begin2, end2;
	int endIndex;
	int currentIndex;
	AttributedCharacterIterator currentIterator;
	int currentIteratorDelta;

	/**
	 * Constructs a CompositeIterator that iterates over the concatenation of
	 * iterator1 and iterator2.
	 * 
	 * @param iterator1,
	 *            iterator2 the base iterators that this composite iterator
	 *            concatenates
	 */
	CompositeIterator(AttributedCharacterIterator iterator1, AttributedCharacterIterator iterator2) {
		this.iterator1 = iterator1;
		this.iterator2 = iterator2;
		begin1 = iterator1.getBeginIndex();
		end1 = iterator1.getEndIndex();
		begin2 = iterator2.getBeginIndex();
		end2 = iterator2.getEndIndex();
		endIndex = (end1 - begin1) + (end2 - begin2);
		internalSetIndex(0);
	}

	// CharacterIterator implementation

	public char first() {
		return internalSetIndex(0);
	}

	public char last() {
		if (endIndex == 0) {
			return internalSetIndex(endIndex);
		} else {
			return internalSetIndex(endIndex - 1);
		}
	}

	public char next() {
		if (currentIndex < endIndex) {
			return internalSetIndex(currentIndex + 1);
		} else {
			return DONE;
		}
	}

	public char previous() {
		if (currentIndex > 0) {
			return internalSetIndex(currentIndex - 1);
		} else {
			return DONE;
		}
	}

	public char current() {
		return currentIterator.setIndex(currentIndex + currentIteratorDelta);
	}

	public char setIndex(int position) {
		if (position < 0 || position > endIndex) {
			throw new IllegalArgumentException("invalid index");
		}
		return internalSetIndex(position);
	}

	private char internalSetIndex(int position) {
		currentIndex = position;
		if (currentIndex < end1 - begin1) {
			currentIterator = iterator1;
			currentIteratorDelta = begin1;
		} else {
			currentIterator = iterator2;
			currentIteratorDelta = begin2 - (end1 - begin1);
		}
		return currentIterator.setIndex(currentIndex + currentIteratorDelta);
	}

	public int getBeginIndex() {
		return 0;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getIndex() {
		return currentIndex;
	}

	// AttributedCharacterIterator implementation

	public int getRunStart() {
		return currentIterator.getRunStart() - currentIteratorDelta;
	}

	public int getRunLimit() {
		return currentIterator.getRunLimit() - currentIteratorDelta;
	}

	public int getRunStart(Attribute attribute) {
		return currentIterator.getRunStart(attribute) - currentIteratorDelta;
	}

	public int getRunLimit(Attribute attribute) {
		return currentIterator.getRunLimit(attribute) - currentIteratorDelta;
	}

	public int getRunStart(Set attributes) {
		return currentIterator.getRunStart(attributes) - currentIteratorDelta;
	}

	public int getRunLimit(Set attributes) {
		return currentIterator.getRunLimit(attributes) - currentIteratorDelta;
	}

	public Map getAttributes() {
		return currentIterator.getAttributes();
	}

	public Set getAllAttributeKeys() {
		Set keys = new HashSet(iterator1.getAllAttributeKeys());
		keys.addAll(iterator2.getAllAttributeKeys());
		return keys;
	}

	public Object getAttribute(Attribute attribute) {
		return currentIterator.getAttribute(attribute);
	}

	// Object overrides

	public Object clone() {
		try {
			CompositeIterator other = (CompositeIterator) super.clone();
			return other;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
}