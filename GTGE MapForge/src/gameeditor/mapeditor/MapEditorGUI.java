package gameeditor.mapeditor;
//JRE
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.*;

//GTGE
import com.golden.gamedev.util.*;

//GameEditor
import gameeditor.util.*;
import gameeditor.splash.*;
public class MapEditorGUI extends JFrame implements ActionListener{
	/**
	 * This class handles the "map" itself.  There's a lot going on here.  All 
	 * specific rendering and algorithms are executed in accordance with the current 
	 * drawTool of the ToolBar.
	 * 
	 *  @author William Morrison
	 */
	JSplitPane splitPane;
	MapPane mapPane;
	MapImagePane mapImagePane;
	JCheckBoxMenuItem[] views = new JCheckBoxMenuItem[5];
	final JFileChooser fc;
	static GenericFilter imgFilter=new GenericFilter(
			new String[]{"png","jpg","jpeg","gif"});
	GenericFilter musicFilter;
	JScrollPane mapScroll;
	MapFormatGUI mapFormat;
	StatusLabel statusLbl;
	public MapEditorGUI(){
		System.out.println("Begin loading editor...");
		EditorSplash splash = new EditorSplash(ImageUtil.getImage(getClass().getClassLoader().
				getResource("gameeditor/Images/mapforgelogo.PNG")));
		System.out.println("Splash loaded.");
		splash.setVisible(true);
		FrameUtil.setNativeLAF();
		JDialog.setDefaultLookAndFeelDecorated(true);
		String[] musicExt = new String[]{"mid","au","wav","mp3"};
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		musicFilter = new GenericFilter(musicExt);
		mapFormat=new MapFormatGUI(this);
		
		initMenuBar();
		System.out.println("MenuBar loaded");
		initMapPanes();
		System.out.println("MapPanes loaded");
		add(mapPane.getToolBar(),BorderLayout.WEST);
		setSize(640,480);
		setTitle("Map Editor: Untitled");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        if(checkForChanges()!=JOptionPane.CANCEL_OPTION )
		            System.exit(0);
		    }
		});
		setLocationRelativeTo(null);
		splash.setVisible(false);
		setVisible(true);
	}
	public void initMapPanes(){
		//Init status pane & label
		JPanel lblPane = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
		statusLbl=new StatusLabel();
		lblPane.add(statusLbl);
		add(lblPane,BorderLayout.SOUTH);
		
		//Init mapImagePane, the mapPane itself,
		//and their respective containers
		mapPane = new MapPane(this);
		mapImagePane = new MapImagePane(mapPane);
		mapScroll = new JScrollPane(mapPane);
		mapScroll.getHorizontalScrollBar().addAdjustmentListener(mapPane);
		mapScroll.getVerticalScrollBar().addAdjustmentListener(mapPane);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(mapImagePane),
				mapScroll);
		splitPane.setDividerSize(3);
		splitPane.setDividerLocation(200);
		add(splitPane,BorderLayout.CENTER);
	}
	public void initMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu view = new JMenu("View");
		JMenu window = new JMenu("Window");
			
		ButtonGroup b = new ButtonGroup();
		String[] s = new String[]{"View all layers","View current only","View current and below"};
		for(int i = 0;i<s.length;i++){
			views[i] = createBoxItem(s[i]);
			b.add(views[i]);
			view.add(views[i]);
		}
		view.addSeparator();
		view.add(views[3]=createBoxItem("Dim others"));
		view.addSeparator();
		view.add(views[4]=createBoxItem("Show grid"));
		views[0].setSelected(true);
		views[3].setSelected(true);
		
		file.add(createStrokeItem("New",
				KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK)));
		file.add(createStrokeItem("Open",
				KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK)));
		file.add(createStrokeItem("Save",
				KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK)));
		file.add(createItem("Save As..."));
		file.addSeparator();
		file.add(createItem("Exit"));
		menuBar.add(file);
		
		edit.add(createItem("Set Map Size"));
		edit.add(createItem("Set Map Music"));
		edit.add(createItem("Set Map Image"));
		edit.add(createItem("Edit Map format"));
		window.add(FrameUtil.getSkinMenu(this));
		window.addSeparator();
		menuBar.add(edit);
		menuBar.add(view);
		menuBar.add(window);
		
		this.setJMenuBar(menuBar);
	}
	private JCheckBoxMenuItem createBoxItem(String txt){
		JCheckBoxMenuItem i = new JCheckBoxMenuItem(txt);
		i.addActionListener(this);
		return i;
	}
	public void actionPerformed(ActionEvent e){
		String s = e.getActionCommand();
		if(s.equals("Exit")){
			if(checkForChanges()==JOptionPane.CANCEL_OPTION)
				return;
			System.exit(0);
		}
		else if(s.equals("New")){
			if(checkForChanges()==JOptionPane.CANCEL_OPTION)
				return;
			//code for creating new map
			mapPane.createNewMap();
		}
		else if(s.equals("Open")){
			if(checkForChanges()==JOptionPane.CANCEL_OPTION)
				return;
			int opt = fc.showOpenDialog(this);
			if(opt!=JFileChooser.APPROVE_OPTION)
				return;
			String path = fc.getSelectedFile().getAbsolutePath();
			parseMap(path);
			mapPane.saved=true;
		}
		else if(s.equals("Set Map Music")){
			File f = getFilteredFile(musicFilter,"Select map's music");
			if(f==null)
				return;
			mapPane.mscPath=f.getAbsolutePath();
		}
		else if(s.equals("Set Map Image")){
			File f = getFilteredFile(imgFilter,"Select map's Image");
			if(f==null)
				return;
			try{
			BufferedImage img = ImageUtil.getImage(f.toURL());
			mapImagePane.setImage(img);
			mapPane.setMapImage(img);
			mapPane.imgPath=f.getName();
			}catch(Exception ee){
				return;
			}
		}
		else if(s.equals("Save")){
			if(!mapPane.mapFileName.equals(""))
				saveMap(new File(mapPane.mapFileName));
			else
				saveMap(chooseSaveLocation());
		}
		else if(s.equals("Save As...")){
			saveMap(chooseSaveLocation());
		}
		else if(s.equals("Set Map Size")){
			int[] in = ModalDialog.get2Input(this,"What size do you want the map?","Width:","Height:",10,500);
			if(in==null)
				return;
			if(in[0]<10 || in[1]<10)
				return;
			mapPane.setMapSize(in[0],in[1]);
		}
		else if(s.equals("Edit Map format")){
			mapFormat.setVisible(true);
		}
		else if(s.equals("Show grid"))
			mapPane.repaint();
		else
			mapPane.repaint();
	}
	private File getFilteredFile(GenericFilter f, String dialogTitle){
		fc.setFileFilter(f);
		fc.setDialogTitle(dialogTitle);
		if(fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)
			return null;
		return fc.getSelectedFile();
	}
	private File chooseSaveLocation(){
		int opt = fc.showSaveDialog(this);
		if(opt==JFileChooser.APPROVE_OPTION){
			File f = fc.getSelectedFile();
			if(f.exists()){
				opt=JOptionPane.showConfirmDialog(this, f.getAbsolutePath()+
						" exists.\nOverwrite?","Overwrite?",JOptionPane.YES_NO_OPTION);
				if(opt==JOptionPane.YES_OPTION)
					return f;
			}
			else
				return f;
		}
		return null;
	}
	public boolean parseMap(String path){
		try{
		MapFormatReader.openWithFormat(mapFormat.currentFormat,this,new File(path));
		mapPane.repaint();
		}catch(Exception e){
			System.out.println("Exception parsing map in MapEditorGUI: "+e);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean saveMap(File f){
		if(f==null)
			return false;
		try{
			writeBytes(MapFormatReader.read(mapFormat.currentFormat,this), f.getAbsolutePath());
		}catch(IOException e){
			JOptionPane.showMessageDialog(this,"IOException saving map "+f.getName()+"!\n" +
					"Details: "+e);
			return false;
		}
		mapPane.saved=true;
		setTitle("Map Editor: "+(mapPane.mapFileName=f.getAbsolutePath()));
		return true;
	}
	public static void writeBytes(String data, String fileName) throws IOException{
		FileOutputStream out=null;
		out = new FileOutputStream(new File(fileName));
		out.write(data.getBytes());
		out.flush();
		out.close();
	}
	/**
	 * Returns the appropriate JOptionPane integer for when user
	 * presses yes no or cancel.  Should be used to check if any changes
	 * have occurred to the map since last loading a map.
	 */
	public int checkForChanges(){
		if(!mapPane.hasBeenSaved()){
		int o = JOptionPane.showConfirmDialog(this, "This map instance hasn't been saved.\n"+
			"Do you want to save this map instance?", "Map Editor",
			JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
		switch(o){
			case JOptionPane.YES_OPTION:
				saveMap(chooseSaveLocation());
				return JOptionPane.YES_OPTION;
			case JOptionPane.CANCEL_OPTION:
				return JOptionPane.CANCEL_OPTION;
			case JOptionPane.NO_OPTION:
				return JOptionPane.NO_OPTION;
		}
		}
		return JOptionPane.UNDEFINED_CONDITION;
	}
	private JMenuItem createStrokeItem(String name, KeyStroke ks){
		JMenuItem i = createItem(name);
		i.setAccelerator(ks);
		return i;
	}
	private JMenuItem createItem(String name){
		JMenuItem i = new JMenuItem(name);
		i.addActionListener(this);
		return i;
	}
	public static void main(String[] args){
		new MapEditorGUI();
	}
}
