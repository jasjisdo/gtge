/*
 * Copyright (c) 2008 Golden T Studios.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.golden.gamedev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.toolkit.TComponent;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;
import com.golden.gamedev.util.ImageUtil;

public class TTextField extends TComponent {
	
	private static final String NULL_STRING = new String();
	private static final GameFont f = new SystemFont(new Font("Verdana",
	        Font.PLAIN, 12));
	
	private final StringBuffer buff = new StringBuffer();
	
	private String text;
	private int caretPosition, caret;
	private int maxLength = -1;
	private boolean editable;
	protected int visiblePosition;
	
	private BufferedImage textUI;
	private GameFont font = TTextField.f;
	
	private long lastTicks;
	private boolean showCursor;
	
	public TTextField(String text, int x, int y, int w, int h) {
		super(x, y, w, h);
		
		this.text = text;
		this.buff.append(text);
		this.caret = 5;
		this.editable = true;
		
		this.createTextUI();
	}
	
	public GameFont getFont() {
		return this.font;
	}
	
	public void setFont(GameFont f) {
		this.font = f;
		this.moveCaretPosition(0);
		this.createTextUI();
	}
	
	/**
	 * Do the action as the user pressed enter when editing this textfield.
	 */
	public void doAction() {
	}
	
	public void update() {
		if (this.isSelected() && this.editable) {
			if (System.currentTimeMillis() - this.lastTicks > 500) {
				this.lastTicks = System.currentTimeMillis();
				this.showCursor = !this.showCursor;
			}
			
		}
		else if (this.showCursor) {
			this.showCursor = false;
		}
	}
	
	public void setEnabled(boolean b) {
		if (this.isEnabled() == b) {
			return;
		}
		super.setEnabled(b);
		
		this.createTextUI();
	}
	
	protected void createTextUI() {
		this.textUI = ImageUtil.createImage(this.getWidth(), this.getHeight(),
		        Transparency.BITMASK);
		
		Graphics2D g = this.textUI.createGraphics();
		
		g.setClip(5, 2, this.getWidth() - 10, this.getHeight() - 4);
		g.setColor((this.isEnabled()) ? Color.BLACK : new Color(172, 168, 153));
		this.font.drawString(g, this.getText(), 5 - this.visiblePosition, (this
		        .getHeight() / 2)
		        - (this.font.getHeight() / 2));
		
		g.dispose();
	}
	
	public void render(Graphics2D g) {
		super.render(g);
		
		this.renderText(g, this.getScreenX(), this.getScreenY(), this
		        .getWidth(), this.getHeight());
	}
	
	protected void renderText(Graphics2D g, int x, int y, int w, int h) {
		g.drawImage(this.textUI, x, y, null);
		
		if (this.editable && this.showCursor) {
			int yfont = y + (h >> 1) - (this.font.getHeight() >> 1);
			
			g.setColor(Color.BLACK);
			g.drawLine(x + this.caret, yfont, x + this.caret, yfont
			        + this.font.getHeight());
		}
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String st) {
		this.buff.setLength(0);
		this.buff.append(st);
		if (this.maxLength >= 0 && this.buff.length() > this.maxLength) {
			this.buff.setLength(this.maxLength);
		}
		this.text = this.buff.toString();
		
		this.setCaretPosition(this.buff.length());
		this.createTextUI();
	}
	
	public int getMaxLength() {
		return this.maxLength;
	}
	
	public void setMaxLength(int i) {
		this.maxLength = i;
		if (this.maxLength >= 0 && this.buff.length() > this.maxLength) {
			this.buff.setLength(this.maxLength);
			this.text = this.buff.toString();
			this.createTextUI();
		}
	}
	
	public int getCaretPosition() {
		return this.caretPosition;
	}
	
	public void setCaretPosition(int i) {
		this.caretPosition = i;
		
		if (this.caretPosition < 0) {
			this.caretPosition = 0;
		}
		if (this.caretPosition > this.buff.length()) {
			this.caretPosition = this.buff.length();
		}
		
		boolean invalidCaret = true, createText = false;
		while (invalidCaret) {
			invalidCaret = false;
			this.caret = this.font.getWidth(this.getText().substring(0,
			        this.caretPosition))
			        + 5 - this.visiblePosition;
			if (this.caret < 5) {
				int len = this.font.getWidth(this.getText().substring(
				        Math.max(this.caretPosition - 5, 0),
				        Math.max(this.caretPosition - 1, 0)));
				this.visiblePosition -= len;
				if (this.visiblePosition < 0 || this.caretPosition <= 4) {
					this.visiblePosition = 0;
				}
				invalidCaret = true;
				createText = true;
				
			}
			else if (this.caret >= this.getWidth() - 5) {
				int len = this.font.getWidth(this.getText().substring(
				        Math.min(this.caretPosition - 1, this.getText()
				                .length()),
				        Math.min(this.caretPosition + 3, this.getText()
				                .length())));
				this.visiblePosition += len;
				invalidCaret = true;
				createText = true;
			}
		}
		
		if (createText) {
			this.createTextUI();
		}
	}
	
	public void moveCaretPosition(int i) {
		this.setCaretPosition(this.caretPosition + i);
	}
	
	public boolean isEditable() {
		return this.editable;
	}
	
	public void setEditable(boolean b) {
		if (this.editable == b) {
			return;
		}
		
		this.editable = b;
		this.createTextUI();
	}
	
	public boolean insertString(int offset, String st) {
		if (this.maxLength >= 0
		        && this.buff.length() + st.length() > this.maxLength) {
			return false;
		}
		
		this.buff.insert(offset, st);
		this.text = this.buff.toString();
		this.createTextUI();
		return true;
	}
	
	public void delete(int index) {
		if (index < 0 || index > this.buff.length() - 1) {
			return;
		}
		
		this.buff.deleteCharAt(index);
		this.text = this.buff.toString();
		
		if (this.caretPosition > this.buff.length()) {
			this.setCaretPosition(this.buff.length());
		}
		this.createTextUI();
	}
	
	protected void processKeyPressed() {
		super.processKeyPressed();
		
		if (!this.editable) {
			return;
		}
		
		switch (this.bsInput.getKeyPressed()) {
			case KeyEvent.VK_LEFT:
				if (this.bsInput.isKeyDown(KeyEvent.VK_CONTROL)) {
					int pos = this.getText().lastIndexOf(' ',
					        this.caretPosition - 2);
					if (pos != -1) {
						this.setCaretPosition(pos + 1);
					}
					else {
						this.setCaretPosition(0);
					}
				}
				else {
					this.moveCaretPosition(-1);
				}
				break;
			
			case KeyEvent.VK_RIGHT:
				if (this.bsInput.isKeyDown(KeyEvent.VK_CONTROL)) {
					int pos = this.getText().indexOf(' ', this.caretPosition);
					if (pos != -1) {
						this.setCaretPosition(pos + 1);
					}
					else {
						this.setCaretPosition(this.getText().length());
					}
					
				}
				else {
					this.moveCaretPosition(1);
				}
				break;
			
			case KeyEvent.VK_HOME:
				this.setCaretPosition(0);
				break;
			
			case KeyEvent.VK_END:
				this.setCaretPosition(this.getText().length());
				break;
			
			case KeyEvent.VK_ENTER:
				this.doAction();
				break;
			
			case KeyEvent.VK_BACK_SPACE:
				if (this.caretPosition > 0) {
					this.moveCaretPosition(-1);
					this.delete(this.caretPosition);
				}
				break;
			
			case KeyEvent.VK_DELETE:
				this.delete(this.caretPosition);
				break;
			
			default:
				boolean upperCase = this.bsInput.isKeyDown(KeyEvent.VK_SHIFT);
				boolean capsLock = false;
				try {
					capsLock = Toolkit.getDefaultToolkit().getLockingKeyState(
					        KeyEvent.VK_CAPS_LOCK);
				}
				catch (Exception e) {
				}
				
				String st = TTextField.NULL_STRING;
				int keyCode = this.bsInput.getKeyPressed();
				
				switch (keyCode) {
					case KeyEvent.VK_BACK_QUOTE:
						st = (upperCase) ? "~" : "`";
						break;
					case KeyEvent.VK_1:
						st = (upperCase) ? "!" : "1";
						break;
					case KeyEvent.VK_2:
						st = (upperCase) ? "@" : "2";
						break;
					case KeyEvent.VK_3:
						st = (upperCase) ? "#" : "3";
						break;
					case KeyEvent.VK_4:
						st = (upperCase) ? "$" : "4";
						break;
					case KeyEvent.VK_5:
						st = (upperCase) ? "%" : "5";
						break;
					case KeyEvent.VK_6:
						st = (upperCase) ? "^" : "6";
						break;
					case KeyEvent.VK_7:
						st = (upperCase) ? "&" : "7";
						break;
					case KeyEvent.VK_8:
						st = (upperCase) ? "*" : "8";
						break;
					case KeyEvent.VK_9:
						st = (upperCase) ? "(" : "9";
						break;
					case KeyEvent.VK_0:
						st = (upperCase) ? ")" : "0";
						break;
					case KeyEvent.VK_MINUS:
						st = (upperCase) ? "_" : "-";
						break;
					case KeyEvent.VK_EQUALS:
						st = (upperCase) ? "+" : "=";
						break;
					case KeyEvent.VK_BACK_SLASH:
						st = (upperCase) ? "|" : "\\";
						break;
					case KeyEvent.VK_OPEN_BRACKET:
						st = (upperCase) ? "{" : "[";
						break;
					case KeyEvent.VK_CLOSE_BRACKET:
						st = (upperCase) ? "}" : "]";
						break;
					case KeyEvent.VK_SEMICOLON:
						st = (upperCase) ? ":" : ";";
						break;
					case KeyEvent.VK_QUOTE:
						st = (upperCase) ? "\"" : "'";
						break;
					case KeyEvent.VK_COMMA:
						st = (upperCase) ? "<" : ",";
						break;
					case KeyEvent.VK_PERIOD:
						st = (upperCase) ? ">" : ".";
						break;
					case KeyEvent.VK_SLASH:
						st = (upperCase) ? "?" : "/";
						break;
					case KeyEvent.VK_DIVIDE:
						st = "/";
						break;
					case KeyEvent.VK_MULTIPLY:
						st = "*";
						break;
					case KeyEvent.VK_SUBTRACT:
						st = "-";
						break;
					case KeyEvent.VK_ADD:
						st = "+";
						break;
					case KeyEvent.VK_DECIMAL:
						st = ".";
						break;
					case KeyEvent.VK_SPACE:
						st = " ";
						break;
					default:
						st = KeyEvent.getKeyText(keyCode).toLowerCase();
						if (st.startsWith("numpad")) {
							// convert numpadX -> X
							st = st.substring(7);
						}
						
						if (st.length() == 0 || st.length() > 1) {
							// invalid key
							return;
						}
						break;
				}
				
				if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
					if (upperCase && !capsLock || !upperCase && capsLock) {
						// upper case
						st = st.toUpperCase();
					}
				}
				
				if (st == TTextField.NULL_STRING) {
					return;
				}
				
				if (this.insertString(this.caretPosition, st)) {
					this.moveCaretPosition(1);
				}
				break;
		}
	}
	
	/**
	 * This Component UI Name is <b>TextField</b>.
	 */
	public String UIName() {
		return "TextField";
	}
	
}
