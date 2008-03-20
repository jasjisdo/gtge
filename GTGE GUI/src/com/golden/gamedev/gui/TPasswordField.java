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

	private String	password = "";
	private char	echoChar = '*';
	protected boolean	allowTextCreation;

	public TPasswordField(String text, int x, int y, int w, int h) {
		super(text,x,y,w,h);

		allowTextCreation = true;
		createPassword();
		createTextUI();
	}
	protected void createPassword() {
		if (echoChar != 0) {
			int len = super.getText().length();
			buff.setLength(0);
			for (int i=0;i < len;i++) {
				buff.append(echoChar);
			}

			password = buff.toString();
		} else {
			password = super.getText();
		}
	}

	protected void createTextUI() {
		if (!allowTextCreation) return;
		super.createTextUI();
	}

	public void setText(String st) {
		allowTextCreation = false;
		super.setText(st);
		allowTextCreation = true;
		createPassword();
		createTextUI();
	}

	public String getText() { return password; }
	public String getPasswordText() { return super.getText(); }

	public char getEchoChar() { return echoChar; }
	public void setEchoChar(char c) {
		echoChar = c;
		createPassword();
		createTextUI();
		moveCaretPosition(0);
	}

	public void setMaxLength(int i) {
		allowTextCreation = false;
		super.setMaxLength(i);
		allowTextCreation = true;
		createPassword();
		createTextUI();
	}

	public boolean insertString(int offset, String st) {
		allowTextCreation = false;
		boolean retval = super.insertString(offset, st);
		allowTextCreation = true;
		if (retval) {
			createPassword();
			createTextUI();
		}
		return retval;
	}

	public void delete(int index) {
		allowTextCreation = false;
		super.delete(index);
		allowTextCreation = true;
		createPassword();
		createTextUI();
	}

}