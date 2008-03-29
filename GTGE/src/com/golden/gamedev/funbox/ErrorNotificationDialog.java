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
package com.golden.gamedev.funbox;

// JFC
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import com.golden.gamedev.Game;
import com.golden.gamedev.engine.BaseGraphics;

/**
 * <code>ErrorNotificationDialog</code> class is a dialog UI to show the
 * player the game exception and tell the player to send it to the game author.
 */
public class ErrorNotificationDialog extends JDialog implements Runnable,
        ActionListener, WindowListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4446981217800668592L;
	
	/**
	 * The error exception of the game.
	 * <p>
	 * 
	 * This is the exception that will be notified to the player.
	 */
	protected Throwable error;
	
	/**
	 * The graphics engine used by the game.
	 */
	protected BaseGraphics bsGraphics;
	
	/**
	 * The game title.
	 */
	protected String title;
	
	/**
	 * The email address which the player should report the exception to.
	 */
	protected String emailAddress;
	
	/**
	 * The text area UI where the game exception is printed out.
	 */
	protected JTextArea textArea;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Builds up an error notification dialog for the player.
	 * 
	 * @param error the game exception
	 * @param bsGraphics the graphics engine of the game
	 * @param title the game title
	 * @param emailAddress the email address which the player should be
	 *        reporting the exception to
	 */
	public ErrorNotificationDialog(Throwable error, BaseGraphics bsGraphics,
	        String title, String emailAddress) {
		super((Frame) null, "Game Exception");
		
		error.printStackTrace();
		
		this.error = error;
		this.bsGraphics = bsGraphics;
		this.title = title;
		this.emailAddress = emailAddress;
		
		this.addWindowListener(this);
		
		if (bsGraphics instanceof Applet) {
			Thread action = new Thread(this);
			action.setDaemon(true);
			action.start();
		}
		else {
			this.initGUI();
		}
	}
	
	/**
	 * Thread implementation that actually {@link #initGUI() create the dialog
	 * UI} used whenever the game is in applet environment.
	 * <p>
	 * 
	 * In applet, the dialog UI is created under a daemon thread to avoid the
	 * dialog creation interrupting the applet process.
	 * 
	 * @see #initGUI()
	 */
	public void run() {
		try {
			Thread.sleep(1500L);
		}
		catch (InterruptedException e) {
		}
		
		this.initGUI();
	}
	
	/**
	 * Prints exception stack trace along with the system information to the
	 * {@linkplain #textArea text area}.
	 * <p>
	 * 
	 * Override this method to add more information about the exception.
	 * 
	 * @see #textArea
	 */
	protected void printStackTrace() {
		this.append(this.textArea, "Game Exception on " + this.title);
		String underline = "------------------";
		for (int i = 0; i < this.title.length(); i++) {
			underline += "-";
		}
		this.append(this.textArea, underline);
		
		// print out error stack trace to text area via string writer
		StringWriter src = new StringWriter();
		PrintWriter writer = new PrintWriter(src);
		this.error.printStackTrace(writer);
		this.append(this.textArea, src.toString());
		
		try {
			this.append(this.textArea, "Game Environment");
			this.append(this.textArea, "----------------");
			
			SimpleDateFormat format = new SimpleDateFormat(
			        "EEE, dd MMM yyyy 'at' HH:mm");
			this.append2(this.textArea, "Date/Time         : ", format
			        .format(Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
			                .getTime()));
			this.append2(this.textArea, "Java Version      : ", this
			        .getSystemProperty(new String[] {
			                "java.vm.version", "java.vendor"
			        }));
			this.append2(this.textArea, "GTGE Version      : ",
			        Game.GTGE_VERSION);
			this.append2(this.textArea, "Environment       : ", this.bsGraphics
			        .getGraphicsDescription());
			this.append2(this.textArea, "Operating System  : ", this
			        .getSystemProperty(new String[] {
			                "os.name", "os.version", "sun.os.patch.level"
			        })); // Windows XP Service Pack 1
			this.append2(this.textArea, "User Name         : ", this
			        .getSystemProperty(new String[] {
				        "user.name"
			        }));
			this.append2(this.textArea, "Working Directory : ", this
			        .getSystemProperty(new String[] {
				        "user.dir"
			        }));
		}
		catch (Throwable e) {
			this.append(this.textArea, e.toString());
		}
	}
	
	/**
	 * The initialization of the error notification GUI.
	 */
	protected void initGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Throwable e) {
		}
		
		try {
			Color color = new Color(236, 233, 216);
			
			// text area for output console
			this.textArea = new JTextArea();
			this.textArea.setEditable(false);
			this.textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
			this.textArea.setBackground(color);
			
			// write the exception to text area
			this.printStackTrace();
			
			// content pane
			JPanel panel = new JPanel();
			panel.setBackground(color);
			panel.setLayout(new BorderLayout());
			panel.setBorder(BorderFactory.createEmptyBorder(10, 7, 10, 7));
			
			// north pane
			JPanel northPane = new JPanel();
			northPane.setLayout(new BoxLayout(northPane, BoxLayout.X_AXIS));
			northPane.setBackground(color);
			northPane.setBorder(BorderFactory.createEmptyBorder(0, 2, 10, 0));
			
			// information label
			JPanel leftPane = new JPanel();
			leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.Y_AXIS));
			leftPane.setBackground(color);
			
			// label 1: error notification
			Font labelFont = new Font("SansSerif", Font.PLAIN, 12);
			JLabel lb1 = new JLabel(
			        "An unrecoverable error occured in the game.");
			lb1.setFont(labelFont);
			
			// label 2: order to send the error to specified email
			JLabel lb2 = new JLabel("Please mail below exception to:");
			lb2.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
			lb2.setFont(labelFont);
			
			// label 3: the email address
			JLabel lb3 = new JLabel(this.emailAddress);
			if (this.emailAddress == null || this.emailAddress.length() == 0) {
				lb3.setText("the game author");
			}
			lb3.setFont(labelFont.deriveFont(Font.BOLD));
			
			leftPane.add(lb1);
			leftPane.add(lb2);
			leftPane.add(lb3);
			
			// copy to clipboard, and exit button
			JPanel rightPane = new JPanel();
			rightPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			rightPane.setBackground(color);
			
			JPanel insideRightPane = new JPanel();
			insideRightPane.setLayout(new GridLayout(0, 1, 0, 9));
			insideRightPane.setOpaque(false);
			
			Color btnColor = new Color(252, 249, 232);
			Font btnFont = new Font("Dialog", Font.PLAIN, 11);
			ImageIcon[] icons = new ImageIcon[2];
			
			// copy to clipboard button
			icons[0] = new ImageIcon(ErrorNotificationDialog.class
			        .getResource("Icon1.png"));
			JButton btn1 = new JButton("copy to clipboard", icons[0]);
			btn1.setBackground(btnColor);
			btn1.setBorderPainted(false);
			btn1.setFocusPainted(false);
			btn1.setIconTextGap(9);
			btn1.setVerticalTextPosition(SwingConstants.TOP);
			btn1.setHorizontalAlignment(SwingConstants.LEFT);
			btn1.setFont(btnFont);
			btn1.addActionListener(this);
			btn1.setPreferredSize(new Dimension(120, 22));
			btn1.setMargin(new Insets(0, 0, 0, 0));
			
			// exit button
			icons[1] = new ImageIcon(ErrorNotificationDialog.class
			        .getResource("Icon2.png"));
			JButton btn2 = new JButton("close application", icons[1]);
			btn2.setBackground(btnColor);
			btn2.setBorderPainted(false);
			btn2.setFocusPainted(false);
			btn2.setIconTextGap(9);
			btn2.setVerticalTextPosition(SwingConstants.TOP);
			btn2.setHorizontalAlignment(SwingConstants.LEFT);
			btn2.setFont(btnFont);
			btn2.addActionListener(this);
			btn2.setPreferredSize(new Dimension(120, 22));
			btn2.setMargin(new Insets(0, 0, 0, 0));
			
			insideRightPane.add(btn1);
			insideRightPane.add(btn2);
			
			rightPane.add(insideRightPane);
			
			northPane.add(leftPane);
			northPane.add(rightPane);
			
			// center pane
			// the text area is in scroll pane
			JScrollPane scrollPane = new JScrollPane(this.textArea);
			scrollPane.setBackground(color);
			scrollPane.setBorder(BorderFactory.createCompoundBorder(
			        BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
			        BorderFactory.createEmptyBorder(3, 4, 3, 4)));
			
			// the content pane
			panel.add(northPane, BorderLayout.NORTH);
			panel.add(scrollPane, BorderLayout.CENTER);
			
			// show up this dialog
			this.setContentPane(panel);
			this.pack();
			this.setSize(475, 400);
			this.setLocationRelativeTo(null);
			
			// make sound right before we show up this dialog
			try {
				Toolkit.getDefaultToolkit().beep();
			}
			catch (Throwable e) {
			}
			
			this.setResizable(false);
			this.setVisible(true);
			
			this.getRootPane().setDefaultButton(btn2);
			btn2.requestFocus();
			
			// if the dialog is not in focus, force to exit
			while (true) {
				try {
					Thread.sleep(500L);
				}
				catch (InterruptedException e) {
				}
				
				if (!this.isFocused()) {
					// wait for 5 seconds before shutting down the game
					try {
						Thread.sleep(5000L);
					}
					catch (InterruptedException e) {
					}
					
					if (!this.isFocused()) {
						this.textArea.selectAll();
						this.textArea.copy();
						
						this.closeDialog();
					}
				}
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			this.closeDialog();
		}
	}
	
	/**
	 * Action listener for exit and copy to clipboard button.
	 */
	public void actionPerformed(ActionEvent e) {
		this.textArea.selectAll();
		this.textArea.copy();
		
		if (e.getActionCommand().equals("close application")) {
			this.closeDialog();
		}
	}
	
	/**
	 * Closes this error notification dialog.
	 * <p>
	 * 
	 * This method will call <code>System.exit(0);</code> if the graphics
	 * engine is not instance of <code>Applet</code> class.
	 */
	public void closeDialog() {
		try {
			if (this.bsGraphics instanceof Applet == false) {
				System.exit(0);
			}
		}
		catch (Throwable excp) {
		}
		
		this.dispose();
	}
	
	/**
	 * Closes this error notification dialog by calling {@link #closeDialog()}
	 * when the window close button is pressed.
	 * 
	 * @see #closeDialog()
	 */
	public void windowClosing(WindowEvent e) {
		this.closeDialog();
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
	
	private void append(JTextArea text, String st) {
		text.append(st + "\n");
	}
	
	private void append2(JTextArea text, String st, String st2) {
		text.append(st);
		text.append(st2 + "\n");
	}
	
	private String getSystemProperty(String[] props) {
		StringBuffer results = new StringBuffer();
		for (int i = 0; i < props.length; i++) {
			try {
				String prop = System.getProperty(props[i]);
				if (prop != null) {
					results.append(prop).append(" ");
				}
			}
			catch (Throwable e) {
				return e.toString();
			}
		}
		
		if (results.length() > 0) {
			results.deleteCharAt(results.length() - 1);
		}
		
		return results.toString();
	}
	
}
