package gameeditor.util;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
public class GUISkinListener implements ActionListener{
	Frame frame;
	public GUISkinListener(Frame f){
		this.frame=f;
	}
	public void actionPerformed(ActionEvent e){
		String command=e.getActionCommand();
		if(UIManager.getLookAndFeel().getName().equals(command))
			return;
		if(FrameUtil.LAFNameHash.containsKey(command)){
			FrameUtil.setLAFByName(FrameUtil.LAFNameHash.get(command));
			Window[] w = frame.getOwnedWindows();
			for(int i = 0;i<w.length;i++)
				SwingUtilities.updateComponentTreeUI(w[i]);
			SwingUtilities.updateComponentTreeUI(frame);
		}
	}
}