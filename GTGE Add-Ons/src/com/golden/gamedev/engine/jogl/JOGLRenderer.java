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
package com.golden.gamedev.engine.jogl;

// JFC
import java.awt.Graphics2D;

import net.java.games.jogl.GL;
import net.java.games.jogl.GLDrawable;
import net.java.games.jogl.GLEventListener;
import net.java.games.jogl.WGL;

/**
 * Listen to JOGL events and init the JOGL renderer.
 */
public class JOGLRenderer implements GLEventListener {
	
	/** *************************** JOGL COMPONENT ****************************** */
	
	private GL gl;
	
	private boolean vsync;
	
	/** *************************** BACK BUFFER ********************************* */
	
	private JOGLGraphics currentGraphics;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Constructs new <code>JOGLRenderer</code> to listen JOGL events and
	 * makes up JOGL renderer.
	 * 
	 * @param vsync whether the rendering should be sync to vertical refresh
	 *        rate or not
	 */
	public JOGLRenderer(boolean vsync) {
		this.vsync = vsync;
	}
	
	/** ************************************************************************* */
	/** *************************** JOGL PROPERTIES ***************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns OpenGL mapping to Graphics2D rendering.
	 */
	public Graphics2D getRenderer() {
		return this.currentGraphics;
	}
	
	/**
	 * Returns whether this graphics engine is vsync to display mode refresh
	 * rate or not.
	 */
	public boolean isVSync() {
		return this.vsync;
	}
	
	/**
	 * Returns the basic JOGL OpenGL interface routines.
	 */
	public GL getGL() {
		return this.gl;
	}
	
	/** ************************************************************************* */
	/** ***************** GLEventListener Implementation ************************ */
	/** ************************************************************************* */
	
	/**
	 * Called by the JOGL rendering process at initialisation. This method is
	 * responsible for setting up the GL context.
	 * 
	 * @param drawable The GL context which is being initialised
	 */
	public void init(GLDrawable drawable) {
		// get hold of the GL content
		this.gl = drawable.getGL();
		
		// by default JOGL use vsync
		// change to requested vsync value
		if (!this.vsync) {
			if (this.gl.isExtensionAvailable("WGL_EXT_swap_control")) {
				try {
					((WGL) this.gl).wglSwapIntervalEXT(0);
				}
				catch (Throwable e) {
					// something wrong here, just ignore it
					// it do no harm :-)
					this.vsync = true;
				}
				
			}
			else {
				// vsync control is not available
				this.vsync = true;
			}
		}
		
		// init GL rendering value
		
		// enable textures since we're going to use these for our sprites
		this.gl.glEnable(GL.GL_TEXTURE_2D);
		
		// set the background colour of the display to black
		this.gl.glClearColor(0, 0, 0, 0);
		
		// disable the OpenGL depth test since we're rendering 2D graphics
		this.gl.glDisable(GL.GL_DEPTH_TEST);
		
		// enable transparency
		this.gl.glEnable(GL.GL_BLEND);
		this.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		// init fake Graphics2D (JOGL Graphics)
		this.currentGraphics = new JOGLGraphics(this.gl);
	}
	
	/**
	 * Called by JOGL rendering process to initiate OpenGL rendering.
	 */
	public void display(GLDrawable drawable) {
		// flush the graphics commands to the card
		this.gl.glFlush();
	}
	
	/**
	 * Called by the JOGL rendering process if and when the display is resized.
	 * 
	 * @param drawable The GL content component being resized
	 * @param x The new x location of the component
	 * @param y The new y location of the component
	 * @param width The width of the component
	 * @param height The height of the component
	 */
	public void reshape(GLDrawable drawable, int x, int y, int width, int height) {
		this.gl = drawable.getGL();
		
		// at reshape we're going to tell OPENGL that we'd like to
		// treat the screen on a pixel by pixel basis by telling
		// it to use Orthographic projection.
		this.gl.glMatrixMode(GL.GL_PROJECTION);
		this.gl.glLoadIdentity();
		
		this.gl.glOrtho(0, width, height, 0, -1, 1);
		
		// set the area being rendered
		this.gl.glViewport(0, 0, width, height);
	}
	
	/**
	 * Called by the JOGL rendering process if/when the display mode is changed.
	 * 
	 * @param drawable the GL context which has changed
	 * @param modeChanged true if the display mode has changed
	 * @param deviceChanged true if the device in use has changed
	 */
	public void displayChanged(GLDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}
	
}
