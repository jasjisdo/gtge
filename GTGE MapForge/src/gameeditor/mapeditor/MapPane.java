package gameeditor.mapeditor;

import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.BufferedImage;

import com.golden.gamedev.object.background.abstraction.*;
import com.golden.gamedev.util.*;

/**
 * This class is used to display an tiled map. It handles its own events,
 * drawing, and resizing in relation to the tiled map.
 * 
 * @author William Morrison
 * 
 */
public class MapPane extends JLabel implements MouseListener,
		MouseMotionListener, ActionListener, AdjustmentListener, Scrollable {
	Map map;
	int[][] lay1, lay2, lay3;
	int[][] currentLayer;
	int[][] cutLayer = null;
	boolean saved = false;
	
	BufferedImage[] mapImg;
	BufferedImage fullMapImg;
	
	MapToolBar toolBar;
	MapEditorGUI editor;
	JPopupMenu popUp;
	JPopupMenu eventMenu;
	Point cutStart = null, cutEnd = null, popPoint = null,
			startLocation = null, tempStartLocation = null,
			rectStart=null;
	String imgPath = "OldSchool.PNG";
	int mouseX, mouseY;
	String mscPath = "";
	String mapFileName = "";
	Rectangle cutRect = new Rectangle();
	Stroke stroke1, stroke2;
	SprayPaintTool sprayPaint;
	StatusLabel statusLbl;
	FloodFillTool floodFill;
	RectangleTool rectTool;
	LineTool lineTool;
	NewMapGUI newMapGUI;
	/**
	 * Accepts a GameEditorGUI for an argument. References the scrollpane
	 * contain this object for custom clipping and rendering for the map.
	 * 
	 * @param gameEditor some gameeditorGUI
	 */
	public MapPane(MapEditorGUI gameEditor) {
		this.editor = gameEditor;
		newMapGUI=new NewMapGUI(editor);
		statusLbl=editor.statusLbl;
		this.setPreferredSize(new Dimension(30 * 32, 20 * 32));
		addMouseListener(this);
		addMouseMotionListener(this);
		initPopupMenus();
		lay1 = new int[20][30];
		lay2 = new int[20][30];
		lay3 = new int[20][30];
		map = new Map(32, 32);
		currentLayer = lay1;
		map.setSize(30, 20);
		//Split image applying a magenta mask.
		try {
			mapImg = ImageUtil.splitImages((fullMapImg=ImageUtil.getImage(getClass().getClassLoader().
					getResource("gameeditor/Images/"+imgPath),new Color(255,0,255))), 15, 11);
		} catch (Exception e) {
			System.out.println("Problem reading MapPane image");
			e.printStackTrace();
		}
		toolBar = new MapToolBar(this);
		toolBar.setOrientation(JToolBar.VERTICAL);
		float[] pattern = new float[] { 10, 10 };
		stroke1 = new BasicStroke(3, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_MITER, 10, pattern, 0);
		stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_MITER, 10, pattern, 0);
		
		sprayPaint = new SprayPaintTool(this,editor.mapImagePane);
		floodFill=new FloodFillTool(editor);
		rectTool = new RectangleTool();
		lineTool=new LineTool();
	}
	public Rectangle getMapClip(){
		return map.getClip();
	}
	/*
	 * Create a new map with newMapGUI.open()
	 * All variables are set in reference to newMapGUI's variables.
	 * 
	 * NOTE:  The AbstractTileBackground's clip is set to a default of 30x20 tiles,
	 * this needs to be fixed.
	 */
	public void createNewMap() {
		newMapGUI.open();
		if(newMapGUI.canceled)
			return;
		newMapGUI.maskImage();
		this.setMapImage(newMapGUI.getImage());
		int w,h;
		setMapSize(w=newMapGUI.getMapWidth(),h=newMapGUI.getMapHeight());
		setPreferredSize(new Dimension(w*32, h*32));
		startLocation = null;
		saved = false;
		mapFileName = "";
		mscPath = "";
		resetMapLayer(lay1);
		resetMapLayer(lay2);
		resetMapLayer(lay3);
		currentLayer = lay1;
		map.setClip(0, 0, 30 * 32, 20 * 32);
		toolBar.layers.getElements().nextElement().setSelected(true);
		repaint();
	}

	private void resetMapLayer(int[][] l) {
		for (int i = 0; i < l.length; i++)
			java.util.Arrays.fill(l[i], 0);
	}

	public void setMapImage(BufferedImage img) {
		mapImg = ImageUtil.splitImages(img, img.getWidth() / 32, img
				.getHeight() / 32);
		clearLayer(lay1);
		clearLayer(lay2);
		clearLayer(lay3);
		editor.mapImagePane.setImage(img);
		editor.mapImagePane.selectedTiles = new int[][] { { 0 } };
		editor.mapImagePane.repaint();
		repaint();
	}

	private void clearLayer(int[][] l) {
		for (int i = 0; i < l.length; i++)
			for (int j = 0; j < l[0].length; j++) {
				if (l[i][j] >= mapImg.length)
					l[i][j] = 0;
			}
	}

	private void initPopupMenus() {
		popUp = new JPopupMenu();
		eventMenu = new JPopupMenu();
		popUp.add(createItem("Cut"));
		popUp.add(createItem("Copy"));
		popUp.add(createItem("Paste"));
		eventMenu.add(createItem("Set start location"));
		eventMenu.add(createItem("Remove start location"));
		eventMenu.add(createItem("Add event"));
	}

	private JMenuItem createItem(String name) {
		JMenuItem i = new JMenuItem(name);
		i.addActionListener(this);
		return i;
	}
	public JToolBar getToolBar() {
		return toolBar;
	}
	public boolean hasBeenSaved() {
		return saved;
	}
	public Point getTileAt(Point p) {
		return new Point(p.x/32, p.y /32);
	}

	public boolean paintTile(Point p) {
		if (!(toolBar.getDrawTool().equals(MapToolBar.PENCIL) || toolBar
				.getDrawTool().equals(MapToolBar.ERASE)))
			return false;
		saved = false;
		return true;
	}

	public void eraseTile(Point p) {
		if (!inBounds(p))
			return;
		currentLayer[p.y][p.x] = 0;
		repaint();
		saved = false;
	}

	public boolean inBounds(Point p) {
		if (p == null)
			return false;
		return inBounds(p.x, p.y);
	}

	public boolean inBounds(int tx, int ty) {
		return tx >= 0 && ty >= 0 && tx < currentLayer[0].length
				&& ty < currentLayer.length;
	}
	
	/* 
	 * This process doesn't execute if Point p is out of bounds,
	 * no tiles are selected, or the selected starting point for the
	 * flood fill is equal to the upper left tile of the selected tiles.
	 */
	public void floodFill(Point p) {
		if (!inBounds(p))
			return;
		editor.mapImagePane.determineSelectedTiles();
		if(editor.mapImagePane.selectedTiles.length==0)
			return;
		else if(currentLayer[p.y][p.x]==editor.mapImagePane.selectedTiles[0][0])
			return;
		floodFill.fill(p.x, p.y, currentLayer[p.y][p.x]);
		repaint();
		saved = false;
	}
	public void mouseDragged(MouseEvent e) {
		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		scrollRectToVisible(r);
		Point p = e.getPoint();
		p = map.tileAt(p.x, p.y);
		if (!inBounds(p))
			return;
		updateMousePosition(e.getPoint());
		if (toolBar.drawTool.equals(MapToolBar.CUT)) {
			cutEnd = p;
			if (cutStart == null)
				return;
			int x = Math.min(cutEnd.x, cutStart.x);
			int y = Math.min(cutEnd.y, cutStart.y);
			int w = Math.abs(cutEnd.x - cutStart.x) + 1;
			int h = Math.abs(cutEnd.y - cutStart.y) + 1;
			cutRect.setFrame(x, y, w, h);
		} else if (toolBar.drawTool.equals(MapToolBar.PENCIL)) {
			paintTiles(p);
			return;
		} else if (toolBar.drawTool.equals(MapToolBar.ERASE)) {
			if(currentLayer[p.y][p.x]!=0){
				currentLayer[p.y][p.x] = 0;
			}
		}
		else if(toolBar.drawTool.equals(MapToolBar.LINE)){
			lineTool.p2=p;
		}
		else if(toolBar.drawTool.equals(MapToolBar.RECTANGLE))
			rectTool.findRect(rectStart,p);
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		updateMousePosition(e.getPoint());
	}
	public void adjustmentValueChanged(AdjustmentEvent e) {
		Rectangle seeRect = editor.mapScroll.getVisibleRect();
		JScrollBar horizBar = editor.mapScroll.getHorizontalScrollBar();
		JScrollBar vertBar = editor.mapScroll.getVerticalScrollBar();
		int x = horizBar.getValue();
		int y = vertBar.getValue();
		int w = seeRect.width;
		int h = seeRect.height;
		w = w + x + 32 > map.getWidth() ? map.getWidth() - x : w;
		h = h + y + 32 > map.getHeight() ? map.getHeight() - y : h;
		Rectangle rect = new Rectangle(x, y, w, h);
		map.setClip(x, y, w, h);
		map.setToCenter((int) rect.getCenterX() - 16,
				(int) rect.getCenterY() - 16, 32, 32);
	}

	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if (s.equals("Cut")) {
			// convert coordinates to tile coordinates
			int tx = cutEnd.x / 32;
			int ty = cutEnd.y / 32;
			// outside bounds, return.
			if (!inBounds(tx, ty))
				return;
			cutLayer = new int[cutRect.height][cutRect.width];
			// Copy tiles from the map into the "cut" array, clear the tiles in
			// the map.
			for (int i = 0; i < cutLayer.length; i++)
				for (int j = 0; j < cutLayer[0].length; j++) {
					cutLayer[i][j] = currentLayer[cutRect.y + i][cutRect.x + j];
					currentLayer[cutRect.y + i][cutRect.x + j] = 0;
				}
			// set cut start to null so the next tile we'll have a new cut start
			// point
			cutStart = null;
		} else if (s.equals("Copy")) {
			// convert coordinates to tile coordinates
			int tx = cutEnd.x / 32;
			int ty = cutEnd.y / 32;
			// outside bounds, return.
			if (!inBounds(tx, ty))
				return;
			cutLayer = new int[cutRect.height][cutRect.width];
			// Copy tiles from the map into the "cut" array, don't clear the
			// tiles in the map.
			for (int i = 0; i < cutLayer.length; i++)
				for (int j = 0; j < cutLayer[0].length; j++) {
					cutLayer[i][j] = currentLayer[cutRect.y + i][cutRect.x + j];
				}
			// set cut start to null so the next tile we'll have a new cut start
			// point
			cutStart = null;
		} else if (s.equals("Paste")) {
			if (cutLayer == null || !inBounds(popPoint))
				return;
			int x = popPoint.x;
			int y = popPoint.y;
			for (int i = 0; i < cutLayer.length; i++)
				for (int j = 0; j < cutLayer[0].length; j++) {
					currentLayer[y + i][x + j] = cutLayer[i][j];
				}
			cutStart = null;
		} else if (s.equals("Set start location")) {
			if(!startLocation.equals(tempStartLocation)){
				startLocation = tempStartLocation;
			}
		} else if (s.equals("Remove start location")) {
			startLocation = null;
		}
		repaint();
	}

	public void mouseClicked(MouseEvent e) {
		updateMousePosition(e.getPoint());
		if (toolBar.drawTool.equals(MapToolBar.CUT)) {
			repaint();
			if (e.getButton() == MouseEvent.BUTTON3) {
				popPoint = e.getPoint();
				popPoint = map.tileAt(popPoint.x, popPoint.y);
				popUp.show(e.getComponent(), e.getX(), e.getY());
			}
		} else if (toolBar.drawTool.equals(MapToolBar.EVENTS)) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				eventMenu.show(e.getComponent(), e.getX(), e.getY());
				tempStartLocation = e.getPoint();
				tempStartLocation = new Point(tempStartLocation.x / 32,
						tempStartLocation.y / 32);
			}
		}
		else
		{
			if (e.getButton() == MouseEvent.BUTTON3)
			{
				Point p = e.getPoint();
				int x = p.x / 32;
				int y = p.y / 32;
				editor.mapImagePane.setSelectedTiles(new int[][] { { currentLayer[y][x] } });
				editor.mapImagePane.repaint();
			}
		}
	}

	public void setSelectedTiles(int[][] tiles) {
		editor.mapImagePane.setSelectedTiles(tiles);
	}

	public void mousePressed(MouseEvent e) {
		//only mouse presses of button one are considered
		if(e.getButton()!=MouseEvent.BUTTON1)
			return;
		Point p = e.getPoint();
		p = map.tileAt(p.x, p.y);
		updateMousePosition(e.getPoint());
		editor.mapImagePane.determineSelectedTiles();
		if (!(toolBar.drawTool.equals(MapToolBar.ERASE) || toolBar.drawTool
				.equals(MapToolBar.PENCIL))) {// isn't a pencil or eraser tool
			if (toolBar.drawTool.equals(MapToolBar.FILL)) {
				floodFill(p);
			} else if (toolBar.drawTool.equals(MapToolBar.CUT)
					&& e.getButton() != MouseEvent.BUTTON3) {
				cutStart = e.getPoint();
				cutStart = map.tileAt(cutStart.x, cutStart.y);
			}
			else if(toolBar.drawTool.equals(MapToolBar.SPRAY)){
				sprayPaint.timer.start();
			}
			else if(toolBar.drawTool.equals(MapToolBar.RECTANGLE)){
				rectStart=p;
			}
			else if(toolBar.drawTool.equals(MapToolBar.LINE)){
				lineTool.p1=p;
			}
		} else {// is a pencil/eraser
			if (!inBounds(p))
				return;
			if (toolBar.drawTool.equals(MapToolBar.ERASE)) {
				if(currentLayer[mouseY][mouseX]!=0)	{
					currentLayer[mouseY][mouseX] = 0;
				}
			}else
				paintTiles(p);
		}
	}
	public void paintTiles(Point p) {
		if (p == null)
			return;
		boolean wasChange=false;
		MapImagePane mImg = editor.mapImagePane;
		mImg.determineSelectedTiles();
		int[][] tiles=new int[mImg.selectedTiles.length][mImg.selectedTiles[0].length];
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[0].length; j++) {
				if (!inBounds(p.x + j, p.y + i))
					break;
				if(currentLayer[p.y+i][p.x+j]!=mImg.selectedTiles[i][j]){
					tiles[i][j]=currentLayer[p.y + i][p.x + j]; 
					currentLayer[p.y + i][p.x + j]= mImg.selectedTiles[i][j];
					wasChange=true;
				}
			}
		repaint();
	}

	public void updateMousePosition(Point p) {
		int x = (int) p.getX() / 32;
		int y = (int) p.getY() / 32;
		if (x != mouseX || y != mouseY) {
			mouseX = x;
			mouseY = y;
			repaint();
			statusLbl.setTile(mouseX,mouseY);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
		if (toolBar.drawTool.equals(MapToolBar.CUT)) {
			Point p = e.getPoint();
			cutEnd = map.tileAt(p.x, p.y);
		}
		else if(toolBar.drawTool.equals(MapToolBar.SPRAY)){
			sprayPaint.timer.restart();
			sprayPaint.timer.stop();
		}
		else if(toolBar.drawTool.equals(MapToolBar.RECTANGLE)){
			rectTool.finalizeRect(this,editor.mapImagePane);
			repaint();
		}
		else if(toolBar.drawTool.equals(MapToolBar.LINE)){
			lineTool.finalizeLine(this,editor.mapImagePane);
			lineTool.p2=lineTool.p1=null;
			repaint();
		}
	}
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - 32;
		} else {
			return visibleRect.height - 32;
		}
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// Get the current position.
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		} else {
			currentPosition = visibleRect.y;
		}

		// Return the number of pixels between currentPosition
		// and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition - (currentPosition / 32) * 32;
			return (newPosition == 0) ? 32 : newPosition;
		} else {
			return ((currentPosition / 32) + 1) * 32 - currentPosition;
		}
	}

	public void renderMap() {
		map.render(map.imgG);
	}

	public void setMapSize(int w, int h) {
		if (w == map.getWidth() && h == map.getHeight())
			return;
		setPreferredSize(new Dimension(w * 32, h * 32));
		map.setSize(w, h);
		int[][] tlay1 = new int[h][w];
		int[][] tlay2 = new int[h][w];
		int[][] tlay3 = new int[h][w];

		w = w < lay1[0].length ? w : lay1[0].length;
		h = h < lay1.length ? h : lay1.length;
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				tlay1[y][x] = lay1[y][x];
				tlay2[y][x] = lay2[y][x];
				tlay3[y][x] = lay3[y][x];
			}
		lay1 = tlay1;
		lay2 = tlay2;
		lay3 = tlay3;
		currentLayer = lay1;
		toolBar.layers.getElements().nextElement().setSelected(true);
		if (startLocation != null)
			if (startLocation.x >= lay1.length
					|| startLocation.y >= lay1[0].length)
				startLocation = null;
		repaint();
		saved = false;
		editor.mapScroll.setPreferredSize(new Dimension(w * 32, h * 32));
		revalidate();
		statusLbl.setMapSize(lay1[0].length,lay1.length);
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		map.render(g2d);
		if (startLocation != null) {
			g2d.setComposite(map.fullAC);
			g2d.drawImage(toolBar.startImg, startLocation.x * 32,
					startLocation.y * 32, null);
		}
		int x = mouseX * 32;
		int y = mouseY * 32;
		if (toolBar.drawTool.equals(MapToolBar.CUT)) {
			if (cutStart != null) {
				if (cutEnd != null) {
					g2d.setStroke(stroke1);
					g2d.setColor(Color.BLACK);
					g2d.drawRect(x, y, 31, 31);
					g2d.drawRoundRect(cutRect.x * 32, cutRect.y * 32,
							cutRect.width * 32, cutRect.height * 32, 10, 10);
					g2d.setColor(Color.WHITE);
					g2d.setStroke(stroke2);
					g2d.drawRect(x, y, 31, 31);
					g2d.drawRoundRect(cutRect.x * 32, cutRect.y * 32,
							cutRect.width * 32, cutRect.height * 32, 10, 10);
					g2d.setStroke(MapImagePane.oldStroke);
					//return;
				}
			}
		}else if(toolBar.drawTool.equals(MapToolBar.RECTANGLE)){
			rectTool.drawCurrentRect(g2d, this, editor.mapImagePane);
		}else if(toolBar.drawTool.equals(MapToolBar.LINE)){
			lineTool.drawCurrentLine(g2d,this,editor.mapImagePane);
		}
		g2d.setComposite(map.fullAC);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(MapImagePane.bigStroke);
		g2d.drawRect(x+1, y+1, 31, 31);
		g2d.setColor(Color.WHITE);
		g2d.setStroke(MapImagePane.smallStroke);
		g2d.drawRect(x+1, y+1, 31, 31);
		g2d.setStroke(MapImagePane.oldStroke);
		g2d.dispose();
	}
	private class Map extends AbstractTileBackground {
		AlphaComposite transAC;
		AlphaComposite fullAC;
		BufferedImage img;
		int tileWidth, tileHeight;
		Graphics2D imgG;
		ArrayList<int[][]> layers;
		public Map(int w, int h) {
			super(30, 20, w, h);
			layers=new ArrayList<int[][]>();
			tileWidth = w;
			tileHeight = h;
			img = ImageUtil.createImage(w * 32, h * 32,
					Transparency.TRANSLUCENT);
			imgG = img.createGraphics();
			transAC = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
			fullAC = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
			
		}
		public void render(Graphics2D g){
			//before rendering all tiles, add all layers into an int[][] ArrayList
			//This way we can treat the arrays as a list of objects for convenience.
			layers.clear();
			layers.add(lay1);
			layers.add(lay2);
			layers.add(lay3);
			super.render(g);
			if(editor.views[4].isSelected()){
				int x = (int)getX()/32,i=0;
				int y = (int)getY()/32;
				int yLim = getClip().height/32+y+getOffsetY()+1;
				int xLim=getClip().width/32+x+getOffsetX()+1;
				g.setColor(Color.BLACK);
				for(i=0;i<yLim;i++)
					g.drawLine(0,i*32,xLim*32,i*32);
				for(i=0;i<xLim;i++)
					g.drawLine(i*32,0,i*32,yLim*32);
				g.drawLine(0,yLim*32-1,xLim*32,yLim*32-1);
				g.drawLine(xLim*32-1,0,xLim*32-1,yLim*32-1);
			}
		}
		public void renderTile(Graphics2D g, int tileX, int tileY, int x, int y) {
			//draw current only
			if(editor.views[1].isSelected()){
				g.drawImage(mapImg[currentLayer[tileY][tileX]], x, y, null);
			}
			//view all layers
			else if(editor.views[0].isSelected()){
				//Dim others
				if(editor.views[3].isSelected()){
					g.setComposite(transAC);
					for(int i = 0;i<3;i++)
						if(toolBar.layerIndex==i+1){
							g.setComposite(fullAC);
							g.drawImage(mapImg[layers.get(i)[tileY][tileX]],x,y,null);
							g.setComposite(transAC);
						}
						else
							g.drawImage(mapImg[layers.get(i)[tileY][tileX]],x,y,null);
				}
				//Don't dim others, just draw all layers.
				else{
					g.drawImage(mapImg[lay1[tileY][tileX]],x,y,null);
					g.drawImage(mapImg[lay2[tileY][tileX]],x,y,null);
					g.drawImage(mapImg[lay3[tileY][tileX]],x,y,null);
				}
			}
			//draw current and all underlying layers, dim where asked
			else if(editor.views[2].isSelected()){
				if(editor.views[3].isSelected()){
					g.setComposite(transAC);
					//layer > 1 selected
					if(toolBar.layerIndex-1>0){
						for(int i = 0;i<toolBar.layerIndex-1;i++)
							g.drawImage(mapImg[layers.get(i)[tileY][tileX]],x,y,null);
						g.setComposite(fullAC);
						g.drawImage(mapImg[layers.get(toolBar.layerIndex-1)[tileY][tileX]],x,y,null);
					}
					//layer 1 is selected, dim and draw
					else{
						g.setComposite(fullAC);
						g.drawImage(mapImg[lay1[tileY][tileX]],x,y,null);
					}
				}else{
					for(int i = 0;i<toolBar.layerIndex;i++)
						g.drawImage(mapImg[layers.get(i)[tileY][tileX]],x,y,null);
				}
			}
		}
		public int getTilesWidth() {
			return getWidth() / 32;
		}

		public int getTilesHeight() {
			return getHeight() / 32;
		}

		public Point tileAt(double x, double y) {
			Point p = new Point();
			if (x < getClip().x || x > getClip().x + getClip().width
					|| y < getClip().y || y > getClip().y + getClip().height)
				return null;
			p.x = (int) (getX() + x - getClip().x) / tileWidth;
			p.y = (int) (getY() + y - getClip().y) / tileHeight;
			return p;
		}
	}
}
