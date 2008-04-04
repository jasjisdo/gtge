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

import com.golden.gamedev.gui.toolkit.TComponent;
import com.golden.gamedev.gui.toolkit.TContainer;

public class TFloatPanel extends TContainer {
	
	private TTitleBar titleBar;
	private TContainer contentPane;
	private boolean icon;
	
	private int distance = -2;
	
	public TFloatPanel(String title, boolean closable, boolean iconifiable,
	        int x, int y, int w, int h) {
		super(x, y, w, h);
		
		this.titleBar = new TTitleBar(title, closable, iconifiable, 0, 0, w, 25);
		this.contentPane = new TPanel(0, 0, w, h - 25 - this.distance);
		
		this.addAccessory(this.titleBar);
		this.addAccessory(this.contentPane);
		this.relayout();
		
		this.setElastic(true);
		this.setLayer(100);
	}
	
	public void add(TComponent comp) {
		this.contentPane.add(comp);
	}
	
	public void addAccessory(TComponent comp) {
		super.add(comp);
	}
	
	public String getTitle() {
		return this.titleBar.getTitle();
	}
	
	public void setTitle(String title) {
		this.titleBar.setTitle(title);
	}
	
	public void relayout() {
		this.titleBar.setLocation(0, 0);
		this.contentPane.setLocation(0, this.titleBar.getHeight()
		        + this.distance);
		this.setSize(this.contentPane.getWidth(), this.titleBar.getHeight()
		        + this.distance + this.contentPane.getHeight());
	}
	
	protected void validateSize() {
		super.validateSize();
		this.titleBar.setSize(this.getWidth(), this.titleBar.getHeight());
		
		if (this.icon) {
			// iconified
			this.contentPane.setVisible(false);
		}
		else {
			// deiconified
			this.contentPane.setVisible(true);
			this.contentPane.setSize(this.getWidth(), this.getHeight()
			        - (this.titleBar.getHeight() + this.distance));
		}
	}
	
	public boolean isIcon() {
		return this.icon;
	}
	
	/**
	 * Iconifies or de-iconifies this float panel.
	 */
	public void setIcon(boolean b) {
		if (this.icon == b) {
			return;
		}
		
		this.icon = b;
		
		int w = this.getWidth(), h = this.titleBar.getHeight();
		if (!this.icon) {
			h += this.contentPane.getHeight() + this.distance;
		}
		
		this.setSize(w, h);
		
		this.titleBar.getIconifiedButton().setToolTipText(
		        (this.icon) ? "Restore" : "Minimize");
	}
	
	public int getPaneDistance() {
		return this.distance;
	}
	
	public void setPaneDistance(int i) {
		this.distance = i;
		this.relayout();
	}
	
	public TTitleBar getTitleBar() {
		return this.titleBar;
	}
	
	public void setTitleBar(TTitleBar bar) {
		this.replace(this.titleBar, bar);
		this.titleBar = bar;
		this.relayout();
	}
	
	public TContainer getContentPane() {
		return this.contentPane;
	}
	
	public void setContentPane(TContainer container) {
		this.replace(this.contentPane, container);
		this.contentPane = container;
		this.relayout();
	}
	
	/**
	 * {@inheritDoc} This Component UI Name is <b>FloatPanel</b>.
	 */
	public String UIName() {
		return "FloatPanel";
	}
	
	public class TTitleBar extends TContainer {
		
		private String title = "";
		private TTitleBarButton close, iconified;
		
		public TTitleBar(String title, boolean closable, boolean iconifiable,
		        int x, int y, int w, int h) {
			super(x, y, w, h);
			
			this.title = title;
			
			this.close = new TTitleBarButton("x", TTitleBarButton.CLOSE_BUTTON,
			        0, 0, 20, h - 5);
			this.iconified = new TTitleBarButton("=",
			        TTitleBarButton.ICONIFIED_BUTTON, 0, 0, 20, h - 5);
			if (!closable) {
				this.close.setVisible(false);
			}
			if (!iconifiable) {
				this.iconified.setVisible(false);
			}
			
			this.add(this.close);
			this.add(this.iconified);
			this.relayout();
		}
		
		protected void processMouseDragged() {
			if (TFloatPanel.this.getContainer() != null) {
				TFloatPanel.this.getContainer().sendToFront(TFloatPanel.this);
			}
			
			TFloatPanel.this.move(this.bsInput.getMouseDX(), this.bsInput
			        .getMouseDY());
		}
		
		public void relayout() {
			this.close.setLocation(this.getWidth() - 22, 2);
			
			// if no close button
			// iconified button will replace close button position
			this.iconified.setLocation(
			        this.close.isVisible() ? this.getWidth() - 43 : this
			                .getWidth() - 22, 2);
		}
		
		protected void validateSize() {
			super.validateSize();
			
			this.close.setSize(20, this.getHeight() - 5);
			this.iconified.setSize(20, this.getHeight() - 5);
		}
		
		public boolean isClosable() {
			return this.close.isVisible();
		}
		
		public void setClosable(boolean b) {
			this.close.setVisible(b);
			this.relayout();
		}
		
		public boolean isIconifiable() {
			return this.iconified.isVisible();
		}
		
		public void setIconifiable(boolean b) {
			this.iconified.setVisible(b);
			this.relayout();
		}
		
		public String getTitle() {
			return this.title;
		}
		
		public void setTitle(String st) {
			this.title = st;
			this.createUI();
		}
		
		public TTitleBarButton getCloseButton() {
			return this.close;
		}
		
		public void setCloseButton(TTitleBarButton btn) {
			this.replace(this.close, btn);
			this.close = btn;
			this.relayout();
		}
		
		public TTitleBarButton getIconifiedButton() {
			return this.iconified;
		}
		
		public void setIconifiedButton(TTitleBarButton btn) {
			this.replace(this.iconified, btn);
			this.iconified = btn;
			this.relayout();
		}
		
		/**
		 * This Component UI Name is <b>TitleBar</b>.
		 */
		public String UIName() {
			return "Panel.TitleBar";
		}
		
		public class TTitleBarButton extends TButton {
			
			public static final int CLOSE_BUTTON = 1;
			public static final int ICONIFIED_BUTTON = 2;
			
			private int action;
			
			public TTitleBarButton(String text, int action, int x, int y,
			        int w, int h) {
				super(text, x, y, w, h);
				
				this.action = action;
				
				switch (action) {
					case CLOSE_BUTTON:
						this.setToolTipText("Close");
						break;
					case ICONIFIED_BUTTON:
						this
						        .setToolTipText((TFloatPanel.this.isIcon()) ? "Restore"
						                : "Minimize");
						break;
				}
			}
			
			public void doAction() {
				switch (this.action) {
					case CLOSE_BUTTON:
						TFloatPanel.this.setVisible(false);
						break;
					case ICONIFIED_BUTTON:
						TFloatPanel.this.setIcon(!TFloatPanel.this.isIcon());
						this.createUI();
						break;
				}
			}
			
			/**
			 * Returns the action of this button.
			 * 
			 * @see #CLOSE_BUTTON
			 * @see #ICONIFIED_BUTTON
			 */
			public int getAction() {
				return this.action;
			}
			
			/**
			 * This Component UI Name is <b>TitleBarButton</b>.
			 */
			public String UIName() {
				return "Button.TitleBarButton";
			}
		}
	}
	
}
