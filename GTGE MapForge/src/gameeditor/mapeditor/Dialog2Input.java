package gameeditor.mapeditor;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
public class Dialog2Input extends ModalDialog implements ActionListener{
	JButton ok = new JButton("Ok"),cancel = new JButton("Cancel");
	JSpinner sp1,sp2;
	int val1,val2;
	public Dialog2Input(Frame owner,String descript,String num1,String num2){
		super(owner,"Input");
		
		ok.addActionListener(this);
		cancel.addActionListener(this);
		sp1=new JSpinner();
		sp2=new JSpinner();
		JPanel pane1 = new JPanel(new FlowLayout());
		JPanel pane2 = new JPanel(new FlowLayout());
		pane1.add(new JLabel(num1));
		pane1.add(sp1);
		pane2.add(new JLabel(num2));
		pane2.add(sp2);
		JPanel btnPane = new JPanel(new FlowLayout());
		btnPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		btnPane.add(ok);
		btnPane.add(cancel);
		
		JPanel inputPane = new JPanel(new GridLayout(1,2));
		add(new JLabel(descript),BorderLayout.NORTH);
		inputPane.add(pane1);
		inputPane.add(pane2);
		add(inputPane,BorderLayout.CENTER);
		add(btnPane,BorderLayout.SOUTH);
		pack();
		this.setLocationRelativeTo(owner);
	}
	public void setLimits(int min, int max){
		sp1.setModel(createNumberModel(min,min,max,1));
		sp2.setModel(createNumberModel(min,min,max,1));
	}
	private SpinnerNumberModel createNumberModel(int v,int min, int max,int step){
		return new SpinnerNumberModel(v,min,max,step);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==ok){
			val1=(Integer)sp1.getValue();
			val2=(Integer)sp2.getValue();
		}	
		dispose();
	}
}
