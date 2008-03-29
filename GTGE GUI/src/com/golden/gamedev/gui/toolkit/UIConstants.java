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
package com.golden.gamedev.gui.toolkit;

import com.golden.gamedev.object.GameFont;

/**
 * Constants used for UI Rendering.
 */
public class UIConstants {
	
	private UIConstants() {
	}
	
	public static Integer LEFT = new Integer(GameFont.LEFT);
	public static Integer CENTER = new Integer(GameFont.CENTER);
	public static Integer RIGHT = new Integer(GameFont.RIGHT);
	
	public static Integer TOP = new Integer(GameFont.LEFT + 10);
	public static Integer BOTTOM = new Integer(GameFont.LEFT + 11);
	
	public static Integer JUSTIFY = new Integer(GameFont.JUSTIFY);
	
}
