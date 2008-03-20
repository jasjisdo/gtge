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
package com.golden.gamedev.engine.input;

// JFC
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class EnhancedAWTInput extends AWTInput {


	private BitSet keyDown;


	public EnhancedAWTInput(Component comp) {
		super(comp);

		keyDown = new BitSet();
	}

	protected InputListener createInputListener() {
		return new EnhancedInputListener();
	}

	public void refresh() {
		super.refresh();

		keyDown.clear();
	}

	public boolean[] getKeyDown() {
		throw new UnsupportedOperationException(
			"Enhanced AWT Input use BitSet boolean, " +
			"use getKeyDownBitSet() instead.");
	}

	public BitSet getKeyDownBitSet() {
		return keyDown;
	}

    public boolean isKeyDown(int keyCode) {
		return keyDown.get(keyCode);
	}

	protected class EnhancedInputListener extends InputListener {

		public void keyPressed(KeyEvent e) {
			if (!keyDown.get(e.getKeyCode())) {
				keyDown.set(e.getKeyCode());

				keyPressed[pressedKey] = e.getKeyCode();
				pressedKey++;
			}

			e.consume();
		}

		public void keyReleased(KeyEvent e) {
			keyDown.clear(e.getKeyCode());

			keyReleased[releasedKey] = e.getKeyCode();
			releasedKey++;

			e.consume();
		}

	}

}