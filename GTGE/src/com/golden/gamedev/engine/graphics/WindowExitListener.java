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
package com.golden.gamedev.engine.graphics;

// JFC
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * <code>WindowExitListener</code> class is a dummy window listener class that
 * that forcing Java Virtual Machine to quit by calling
 * <code>System.exit()</code>.
 * <p>
 * 
 * This window listener is used by all <code>java.awt.Frame</code> class in
 * this graphics engine package.
 */
public final class WindowExitListener implements WindowListener {
	
	private static final WindowListener singleton = new WindowExitListener();
	
	/**
	 * Returns <code>WindowExitListener</code> singleton instance.
	 * @return The singleton instance.
	 */
	public static WindowListener getInstance() {
		return WindowExitListener.singleton;
	}
	
	private WindowExitListener() {
	}
	
	/**
	 * Calls <code>System.exit(0)</code> to force Java Virtual Machine to quit
	 * when the close button of the parent object is pressed.
	 */
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
	
	/** Do nothing. */
	public void windowOpened(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowClosed(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowIconified(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowDeiconified(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowActivated(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowDeactivated(WindowEvent e) {
	}
	
}
