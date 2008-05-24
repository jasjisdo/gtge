package gameeditor.mapeditor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import com.golden.gamedev.util.*;
public class NewMapGUI extends JDialog implements ActionListener,MouseListener{
	Graphic imgLbl,maskLbl,partMaskLbl;
	JScrollPane scrollPane;
	JFileChooser fileChooser;
	boolean canceled=false;
	JSpinner mapWidth,mapHeight;
	public NewMapGUI(Frame owner){
		super(owner,"New Map",true);
		fileChooser=new JFileChooser();
		imgLbl=new Graphic();
		imgLbl.addMouseListener(this);
		maskLbl=new Graphic();
		partMaskLbl=new Graphic();
		maskLbl.setBackground(new Color(255,0,255));
		partMaskLbl.setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		layPane();
	}
	public void maskImage(){
		int w = imgLbl.img.getWidth();
		int h = imgLbl.img.getHeight();
		int transRGB=maskLbl.getBackground().getRGB();
		Color transColor=null;
		int partTRGB=(transColor=partMaskLbl.getBackground()).getRGB();
		transColor=new Color(transColor.getRed(),transColor.getGreen(),transColor.getBlue(),75);
		int partTransRGB=transColor.getRGB();
		int rgb=-1;
		for(int i = 0;i<h;i++)
			for(int j = 0;j<w;j++){
				if((rgb=imgLbl.img.getRGB(j,i))==transRGB)
					imgLbl.img.setRGB(j,i,0xff);
				else if(rgb==partTRGB)
					imgLbl.img.setRGB(j,i,partTransRGB);
			}
	}
	public void layPane(){
		mapWidth=new JSpinner(new SpinnerNumberModel(30,30,500,1));
		mapHeight=new JSpinner(new SpinnerNumberModel(20,20,500,1));
	
		JPanel mapDetails = new JPanel(new BorderLayout(5,5));
		JPanel cntrl = new JPanel(new BorderLayout(5,5));
		
		mapDetails.add(scrollPane=new JScrollPane(imgLbl),BorderLayout.CENTER);
		JPanel colors = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
		colors.add(new JLabel("Mask:"));
		colors.add(maskLbl);
		colors.add(new JLabel("Partial mask:"));
		colors.add(partMaskLbl);
		cntrl.add(colors,BorderLayout.NORTH);
		
		JPanel mapDim = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
		mapDim.add(new JLabel("Map width:"));
		mapDim.add(mapWidth);
		mapDim.add(new JLabel("Map height:"));
		mapDim.add(mapHeight);
		cntrl.add(mapDim,BorderLayout.CENTER);
		
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		btnPanel.add(createButton("OK"));
		btnPanel.add(createButton("Cancel"));
		cntrl.add(btnPanel,BorderLayout.SOUTH);
		
		mapDetails.add(cntrl,BorderLayout.SOUTH);
		add(mapDetails,BorderLayout.CENTER);
	}
	public Color getColorAt(Point p){
		if(p.x>=imgLbl.img.getWidth() || p.y>=imgLbl.img.getHeight())
			return null;
		return new Color(imgLbl.img.getRGB(p.x, p.y));
	}
	public JButton createButton(String text){
		JButton b = new JButton(text);
		b.addActionListener(this);
		return b;
	}
	public void open(){
		if(JFileChooser.APPROVE_OPTION==fileChooser.showDialog(this,"Select new map's image...")){
			BufferedImage img=null;
			try{
				java.io.File f = fileChooser.getSelectedFile();
				if(!MapEditorGUI.imgFilter.accept(f)){
					setVisible(!(canceled=true));
					JOptionPane.showMessageDialog(this,"File "+f.getName()+" isn't a supported image format.");
					return;
				}
				System.out.println("Attempting to load image file...");
				img = ImageUtil.getImage(f.toURL(),Transparency.TRANSLUCENT);
				System.out.println("Image loaded...\n");
			}catch(Exception e){
				System.out.println("Exception "+e+" while loading image.");
				setVisible(!(canceled=true));
				return;
			}
			imgLbl.setImage(img);
			scrollPane.revalidate();
			setSize(400,400);
			setLocationRelativeTo(null);
			setVisible(true);
		}
		else{
			canceled=true;
			setVisible(false);
		}
	}
	public void setVisible(boolean b){
		repaint();
		super.setVisible(b);
	}
	public BufferedImage getImage(){
		return imgLbl.img;
	}
	public int getMapWidth(){
		return ((Integer)mapWidth.getValue()).intValue();
	}
	public int getMapHeight(){
		return ((Integer)mapHeight.getValue()).intValue();
	}
	public void actionPerformed(ActionEvent e){
		String s = e.getActionCommand();
		if(s.equals("Cancel"))
			canceled=true;
		else
			canceled=false;
		setVisible(false);
	}
	public void mousePressed(MouseEvent e){}
	public void mouseClicked(MouseEvent e){
		//Right click, set partial mask
		if(e.getButton()==MouseEvent.BUTTON3){
			Color c = getColorAt(e.getPoint());
			if(c==null)
				return;
			partMaskLbl.setBackground(c);
		}
		//Left click, set full mask
		else if(e.getButton()==MouseEvent.BUTTON1){
			Color c = getColorAt(e.getPoint());
			if(c==null)
				return;
			maskLbl.setBackground(c);
		}
	}
	public void mouseMoved(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	private class Graphic extends JLabel{
		BufferedImage img;
		public Graphic(){
			this(null);
		}
		public Graphic(BufferedImage img){
			this.img=img;
			if(img==null)
				setPreferredSize(new Dimension(50,50));
			else
				setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
		}
		public void setImage(BufferedImage img){
			this.img=img;
			if(img!=null){
				setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
				revalidate();
			}
		}
		public void paintComponent(Graphics g){
			if(img!=null)
				g.drawImage(img,0,0,null);
			else{
				g.setColor(getBackground());
				g.fillRect(0,0,getWidth()-1,getHeight()-1);
				g.setColor(Color.BLACK);
				g.drawRect(0,0,getWidth()-1,getHeight()-1);
			}
		}
	}
}