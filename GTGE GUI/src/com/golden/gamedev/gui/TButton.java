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

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.golden.gamedev.gui.toolkit.TComponent;

public class TButton extends TComponent {
	
	private String text = "";
	private boolean over, pressed;
	private int pressedTime;
	
	public TButton(String text, int x, int y, int w, int h) {
		super(x, y, w, h);
		
		this.text = text;
	}
	
	public void update() {
		if (this.pressedTime > 0 && --this.pressedTime <= 0) {
			this.pressed = false;
		}
	}
	
	public boolean isMouseOver() {
		return this.over;
	}
	
	public boolean isMousePressed() {
		return this.pressed;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String st) {
		this.text = st;
		this.createUI();
	}
	
	/**
	 * Do the action as the user pressed and released this button.
	 */
	public void doAction() {
	}
	
	/**
	 * Programmatically perform a "click", with the button stays in pressed
	 * state for <code>pressedTime</code> time. The button action will also be
	 * performed.
	 * 
	 * @param pressedTime time for the button to stay in pressed state
	 */
	public void doClick(int pressedTime) {
		this.pressedTime = pressedTime;
		this.pressed = true;
		this.doAction();
	}
	
	protected void processMousePressed() {
		if (this.bsInput.getMousePressed() == MouseEvent.BUTTON1) {
			this.pressed = true;
		}
	}
	
	protected void processMouseReleased() {
		if (this.bsInput.getMouseReleased() == MouseEvent.BUTTON1) {
			this.pressed = false;
		}
	}
	
	protected void processMouseClicked() {
		if (this.bsInput.getMouseReleased() == MouseEvent.BUTTON1) {
			this.doAction();
		}
	}
	
	protected void processKeyPressed() {
		if (this.isSelected()
		        && this.bsInput.getKeyPressed() == KeyEvent.VK_ENTER) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doAction();
		}
	}
	
	protected void processKeyReleased() {
		if (this.isSelected()
		        && this.bsInput.getKeyReleased() == KeyEvent.VK_ENTER) {
			this.pressed = false;
		}
	}
	
	protected void processMouseEntered() {
		this.over = true;
	}
	
	protected void processMouseExited() {
		this.over = this.pressed = false;
	}
	
	protected void processMouseDragged() {
		if (this.bsInput.isMouseDown(MouseEvent.BUTTON1)) {
			this.over = this.pressed = this.intersects(
			        this.bsInput.getMouseX(), this.bsInput.getMouseY());
		}
	}
	
	/**
	 * This Component UI Name is <b>Button</b>.
	 */
	public String UIName() {
		return "Button";
	}
	
	public String toString() {
		return super.toString() + " " + "[text=" + this.text + "]";
	}
}
