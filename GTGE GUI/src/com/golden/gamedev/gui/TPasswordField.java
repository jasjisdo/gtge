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

public class TPasswordField extends TTextField {
	
	private final StringBuffer buff = new StringBuffer();
	
	private String password = "";
	private char echoChar = '*';
	protected boolean allowTextCreation;
	
	public TPasswordField(String text, int x, int y, int w, int h) {
		super(text, x, y, w, h);
		
		this.allowTextCreation = true;
		this.createPassword();
		this.createTextUI();
	}
	
	protected void createPassword() {
		if (this.echoChar != 0) {
			int len = super.getText().length();
			this.buff.setLength(0);
			for (int i = 0; i < len; i++) {
				this.buff.append(this.echoChar);
			}
			
			this.password = this.buff.toString();
		}
		else {
			this.password = super.getText();
		}
	}
	
	protected void createTextUI() {
		if (!this.allowTextCreation) {
			return;
		}
		super.createTextUI();
	}
	
	public void setText(String st) {
		this.allowTextCreation = false;
		super.setText(st);
		this.allowTextCreation = true;
		this.createPassword();
		this.createTextUI();
	}
	
	public String getText() {
		return this.password;
	}
	
	public String getPasswordText() {
		return super.getText();
	}
	
	public char getEchoChar() {
		return this.echoChar;
	}
	
	public void setEchoChar(char c) {
		this.echoChar = c;
		this.createPassword();
		this.createTextUI();
		this.moveCaretPosition(0);
	}
	
	public void setMaxLength(int i) {
		this.allowTextCreation = false;
		super.setMaxLength(i);
		this.allowTextCreation = true;
		this.createPassword();
		this.createTextUI();
	}
	
	public boolean insertString(int offset, String st) {
		this.allowTextCreation = false;
		boolean retval = super.insertString(offset, st);
		this.allowTextCreation = true;
		if (retval) {
			this.createPassword();
			this.createTextUI();
		}
		return retval;
	}
	
	public void delete(int index) {
		this.allowTextCreation = false;
		super.delete(index);
		this.allowTextCreation = true;
		this.createPassword();
		this.createTextUI();
	}
	
}
