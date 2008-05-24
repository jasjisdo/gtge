package gameeditor.mapeditor;
//JRE
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.Vector;
import java.io.*;
import java.util.*;

import com.golden.gamedev.util.FileUtil;
public class MapFormatGUI extends ModalDialog implements ActionListener,ListSelectionListener{
	private JList mapFormat;
	private JList mapAttributes;
	JButton delete,add;
	Vector<String> currentFormat = new Vector<String>(10);
	private Vector<String> origFormat;
	private JFileChooser fileChooser;
	static final String[] defaultFormat = new String[]{"Map Image","Start Location","Map Music",
		"Delineator:END","Layer 1 Data","Delineator:*","Layer 2 Data","Delineator:*","Layer 3 Data","Delineator:*"};
	public MapFormatGUI(Frame owner){
		super(owner,"Map Format");
		fileChooser=new JFileChooser();
		fileChooser.setFileFilter(new GenericFilter("fmt"));
		fileChooser.setName("Open map format...");
		mapFormat=new JList();
		resetFormat();
		mapAttributes = new JList(MapFormatReader.mapSpecs);
		Dimension d = new Dimension(200,300);
		mapAttributes.setPreferredSize(d);
		mapFormat.setPreferredSize(d);
		mapFormat.addListSelectionListener(this);
		mapAttributes.addListSelectionListener(this);
		mapAttributes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mapFormat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		init();
		pack();
		
		setResizable(false);
	}
	public JMenuItem createItem(String txt){
		JMenuItem item = new JMenuItem(txt);
		item.addActionListener(this);
		return item;
	}
	public void resetFormat(){
		currentFormat.clear();
		for(int i = 0;i<defaultFormat.length;i++)
			currentFormat.add(defaultFormat[i]);
		mapFormat.setListData(currentFormat);
		mapFormat.repaint();
	}
	public void init(){
		JPanel pane = new JPanel(new BorderLayout(3,0));
		JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
		JPanel btnFormat = new JPanel(new FlowLayout());
		
		btnPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		btnPane.add(createButton("Ok"));
		btnPane.add(createButton("Cancel"));
		
		pane.add(new JScrollPane(mapAttributes),BorderLayout.WEST);
		pane.add(new JScrollPane(mapFormat),BorderLayout.EAST);
		pane.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		add(pane,BorderLayout.CENTER);
		add(btnPane,BorderLayout.SOUTH);
		
		delete=createButton("Delete from format");
		add=createButton("Add to format");
		delete.setEnabled(false);
		add.setEnabled(false);
		btnFormat.add(add);
		btnFormat.add(delete);
		pane.add(btnFormat,BorderLayout.SOUTH);
		
		initMenuBar();
	}
	public void initMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		file.add(createItem("Restore default format"));
		file.add(createItem("Save current format"));
		file.add(createItem("Load saved format"));
		menuBar.add(file);
		setJMenuBar(menuBar);
	}
	public JButton createButton(String txt){
		JButton b = new JButton(txt);
		b.addActionListener(this);
		return b;
	}
	public void actionPerformed(ActionEvent e){
		String s = e.getActionCommand();
		if(s.equals("Ok"))
			setVisible(false);
		else if(s.equals("Cancel")){
			currentFormat=origFormat;
			setVisible(false);
		}
		else if(s.equals("Add to format")){
			int index = mapFormat.getSelectedIndex();
			String opt =  MapFormatReader.mapSpecs[mapAttributes.getSelectedIndex()];
			if(opt.equals("Delineator:")){
				opt=JOptionPane.showInputDialog(this, "What will the delineator be?",
					"*");
				if(opt==null)
					return;
				opt="Deleneator:"+opt;
			}
			if(currentFormat.size()>0){
				currentFormat.insertElementAt(opt,index+1);
				mapFormat.setListData(currentFormat);
				mapFormat.setSelectedIndex(index+1);
				mapFormat.repaint();
			}else{
				currentFormat.add(opt);
				mapFormat.setListData(currentFormat);
				mapFormat.setSelectedIndex(0);
				mapFormat.repaint();
			}
		}
		else if(s.equals("Delete from format")){
			int index = mapFormat.getSelectedIndex();
			currentFormat.remove(index);
			if(index>=currentFormat.size())
				mapFormat.setSelectedIndex(index-1);
			if(currentFormat.size()==0)
				delete.setEnabled(false);
			mapFormat.repaint();
		}
		else if(s.equals("Restore default format")){
			resetFormat();
			mapFormat.setSelectedIndex(0);
		}
		else if(s.equals("Load saved format")){
			fileChooser.setName("Load saved map format...");
			int opt=fileChooser.showOpenDialog(this);
			if(opt==JFileChooser.APPROVE_OPTION){
				File f = fileChooser.getSelectedFile();
				Scanner scan=null;
				try{
					scan = new Scanner(f);
				}catch(IOException ex){
					JOptionPane.showMessageDialog(this,"Exception encountered loading "+f.getName()+".\n" +
							"Details: "+ex);
					ex.printStackTrace();
					return;
				}
				Vector<String> tempCommands = new Vector<String>();
				while(scan.hasNext()){
					String command=scan.nextLine();
					if(MapFormatReader.isValidFormatArg(command))
						tempCommands.add(command);
					else{
						JOptionPane.showMessageDialog(this,"Unable to load format.\nUnrecognized format.");
						return;
					}
				}
				mapFormat.setListData(currentFormat=tempCommands);
				mapFormat.repaint();
			}
		}else if(s.equals("Save current format")){
			fileChooser.setName("Save current format...");
			int opt = fileChooser.showSaveDialog(this);
			if(opt==JFileChooser.APPROVE_OPTION){
				File f = fileChooser.getSelectedFile().getAbsoluteFile();
				if(f.exists()){
					opt=JOptionPane.showConfirmDialog(this,"Overwrite "+f.getName()+"?","Overwrite?",
							JOptionPane.YES_NO_OPTION);
					if(opt!=JOptionPane.YES_OPTION)
						return;
				}
				System.out.println("Saving file "+f.getName());
				String[] str = new String[currentFormat.size()];
				for(int i = 0;i<currentFormat.size();i++)
					str[i]=currentFormat.get(i);
				f=FileUtil.setExtension(f,"fmt");
				if(!FileUtil.fileWrite(str, f))
					JOptionPane.showMessageDialog(this,"Map failed to save.\n" +
							"Check your current format, & encryption settings.");
			}
		}
	}
	public void setVisible(boolean v){
		if(v)
			origFormat=currentFormat;
		setLocationRelativeTo(null);
		super.setVisible(v);
	}
	public void valueChanged(ListSelectionEvent e){
		if(!e.getValueIsAdjusting()){
			if(mapFormat==e.getSource()){
				delete.setEnabled(true);
			}
			else{
				add.setEnabled(true);
			}
		}
	}
	public void addToFormat(){}
	public void deleteFromFormat(){}
}
