package gameeditor.splash;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
public class EditorSplash extends JFrame{
	public EditorSplash(BufferedImage img){
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		setUndecorated(true);
		add(new JLabel(new ImageIcon(img)),BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
}