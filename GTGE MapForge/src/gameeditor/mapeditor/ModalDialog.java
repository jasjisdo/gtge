package gameeditor.mapeditor;
import javax.swing.*;
import java.awt.*;
public class ModalDialog extends JDialog{
	public ModalDialog(Frame frame){
		this(frame,"Dialog");
	}
	public ModalDialog(Frame frame, String title){
		super(frame,title,true);
	}
	public static int[] get2Input(Frame owner, String desc,String num1,String num2,int min, int max){
		Dialog2Input d = new Dialog2Input(owner, desc,num1,num2);
		d.setLimits(min,max);
		d.setSize(200,150);
		d.setLocationRelativeTo(null);
		d.setVisible(true);
		return new int[]{d.val1,d.val2};
	}
	public void setVisible(boolean b){
		super.setVisible(b);
	}
}