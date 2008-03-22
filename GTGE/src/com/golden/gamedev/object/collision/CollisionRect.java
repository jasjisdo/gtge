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
package com.golden.gamedev.object.collision;



/**
 * Optimized <code>java.awt.Rectangle</code> with double precision. The default
 * sprite collision bounding box.
 */
public class CollisionRect implements CollisionShape {

    /**
     * The <code>x</code>-coordinate of this collision rect.
     */
	public double x;

    /**
     * The <code>y</code>-coordinate of this collision rect.
     */
    public double y;

    /**
     * The width of this collision rect.
     */
    public int width;

    /**
     * The height of this collision rect.
     */
    public int height;

	private int x1, x2;	// optimization, for intersects method


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

    /**
     * Creates new <code>CollisionRect</code>.
     */
	public CollisionRect() {
        x = 0; y = 0;
        width = 0; height = 0;
    }

    /**
     * Grows this collision rect by <code>h</code> width,
     * and <code>v</code> height.
     */
	public void grow(int h, int v) {
        if (h >= 0) width += h; else { x += h; width -= h; }
        if (v >= 0) height += v; else { y += v; height -= v; }
    }

    /**
     * Shrinks this collision rect by <code>h</code> width,
     * and <code>v</code> height.
     */
    public void shrink(int h, int v) {
        if (h >= 0) width -= h; else { x -= h; width += h; }
        if (v >= 0) height -= v; else { y -= v; height += v; }
    }

    public boolean intersects(CollisionShape shape) {
////		CollisionRect rect = (CollisionRect) shape;
//
//		if (x < shape.getX()) {
//			x1 = (int) x;
//			x2 = (int) Math.ceil(shape.getX());
//
//		} else {
//			x1 = (int) Math.ceil(x);
//			x2 = (int) shape.getX();
//		}
//
//        return (x1 + width > x2 &&
//				x1 < x2 + shape.getWidth() &&
//                y + height > shape.getY() &&
//				y < shape.getY() + shape.getHeight());
//
////        return (x + width > rect.x && x < rect.x + rect.width &&
////                y + height > rect.y && y < rect.y + rect.height);


//		CollisionRect rect = (CollisionRect) shape;

		//System.out.print("s1.x="+x+" s1.w="+width+" s2.x="+shape.getX()+" s2.width="+shape.getWidth()+"-->");

		return (x + width > shape.getX() &&
				x < shape.getX()+shape.getWidth() &&
				y + height > shape.getY() &&
				y < shape.getY() + shape.getHeight());
	}

    public void setBounds(double x1, double y1, int w1, int h1) {
        x = x1; y = y1;
        width = w1; height = h1;
    }

	/**
	 * Sets the boundary of this colllision rect to be same with specified
	 * collision shape.
	 */
    public void setBounds(CollisionShape shape) {
        setBounds(shape.getX(), shape.getY(),
				  shape.getWidth(), shape.getHeight());
    }

	public void setLocation(double x1, double y1) {
		x = x1; y = y1;
	}

    public void move(double dx, double dy) {
		x += dx; y += dy;
	}

    public double getX() {
		return x;
	}

    public double getY() {
		return y;
	}

    public int getWidth() {
		return width;
	}

    public int getHeight() {
		return height;
	}

	public String toString() {
		return super.toString() + " " +
			"[x=" + x +
			", y=" + y +
			", width=" + width +
			", height=" + height + "]";
	}

}