package gameeditor.mapeditor;
import java.awt.*;
import static java.lang.Math.*;
import java.util.*;
import java.awt.geom.*;
public class LineTool {
	Point p1=null;
	private ArrayList<Point> p = new ArrayList<Point>(500);
	Point p2=null;
	private Point oldp1=new Point(),oldp2=new Point();
	public void findLine(int width,int height){
		int dx=p1.x-p2.x,dy=p1.y-p2.y;
		p.clear();
		//slope is oblique if both dx & dy = 0,
		//OR both aren't equal to 0.
		if((dx==0 && dy==0) || (dx!=0 && dy!=0))
			p=getObliquePoints(p1,p2,width,height);
		else if(dx==0 && dy!=0){//slope is undefined (vertical)
			int min=min(p1.y,p2.y);
			int max=max(p1.y,p2.y);
			for(int i = min;i<=max;i+=height+1){
				p.add(new Point(p1.x,i));
			}
		}
		else if(dy==0 && dx!=0){//slope is horizontal
			int min=min(p1.x,p2.x);
			int max=max(p1.x,p2.x);
			for(int i=min;i<=max;i+=width+1){
				p.add(new Point(i,p1.y));
			}
		}
		oldp1=p1;
		oldp2=p2;
	}
	public ArrayList<Point> getObliquePoints(Point p1,Point p2,int width,int height){
		return getObliquePoints(p1.x,p1.y,p2.x,p2.y,width,height);
	}
	public ArrayList<Point> getObliquePoints(int x0, int y0, int x1, int y1, int width, int height) {
        int distance = (int) distance(x0, y0, x1, y1);
        ArrayList<Point> points = new ArrayList<Point>(distance);
        double dx = x1 - x0;
        double dy = y1 - y0;
        double m=0;
        points.add(new Point(x0,y0));
        if (Math.abs(dx) > Math.abs(dy)) {          // slope < 1
            m = dy / dx;      // compute slope
            double b = y0 - m * x0;
            dx = (dx < 0) ? -1 : 1;
            boolean subtracting=x0>x1;
            double inc=dx+(subtracting?-width:width);
            while (subtracting?x0>x1:x0<x1) {
            	x0 += inc;
                points.add(new Point(x0, (int)Math.round(m*x0+b)));
            }
        } else
        if (dy != 0) {                        // slope >= 1
        	m = (float) dx / (float) dy;      // compute slope
            double b = x0 - m*y0;
            dy = (dy < 0) ? -1 : 1;
            boolean subtracting=y0>y1;
            double inc=dy+(subtracting?-height:height);
            while (subtracting?y0>y1 : y0<y1) {
            	y0+=inc;
                points.add(new Point((int)Math.round(m* y0 + b), y0));
            }
        }
        return points;
    }
    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) );
    }
    public void finalizeLine(MapPane mapPane,MapImagePane imgPane){
    	if(p1!=null && p2==null)
    		mapPane.paintTiles(p1);
    	else{
    		Point[] points = new Point[p.size()];
    		p.toArray(points);
    		int[][] tiles=imgPane.selectedTiles;
    		int tx,ty;
    		for(int i = 0;i<points.length;i++)
    			for(int y = 0;y<tiles.length;y++){
    				ty=points[i].y+y;
    				if(ty<mapPane.currentLayer.length && ty>-1)
    				for(int x=0;x<tiles[0].length;x++){
    					tx=points[i].x+x;
    					if(tx<mapPane.currentLayer[0].length && tx>-1)
    					mapPane.currentLayer[ty][tx]=
    						imgPane.selectedTiles[y][x];
    				}
    			}
    	}
    }
	public void drawCurrentLine(Graphics2D g, MapPane mapPane, 
			MapImagePane mImgPane){
		if(p1==null || p2==null || mImgPane.selectedTiles==null || mImgPane.selectedTiles.length<1)
			return;
		if(!oldp1.equals(p1) || !oldp2.equals(p2)){
			findLine(mImgPane.selectedTiles[0].length-1,mImgPane.selectedTiles.length-1);
		}
		Point[] points = new Point[p.size()];
		p.toArray(points);
		Rectangle rect=(Rectangle)mapPane.getMapClip().clone();
		rect.setFrame(rect.x/32-2,rect.y/32-2,rect.width/32+4,rect.height/32+4);
		g.setColor(Color.BLACK);
		int[][] tiles=mImgPane.selectedTiles;
		for(int i = points.length-1;i>-1;i--){
			if(!rectContains(rect,points[i]))
				break;
			for(int y=0;y<tiles.length;y++)
				for(int x=0;x<tiles[0].length;x++){
					g.drawImage(mapPane.mapImg[tiles[y][x]],
						(points[i].x+x)*32,(points[i].y+y)*32,null);
				}
			g.drawRect(points[i].x*32,points[i].y*32,tiles[0].length*32,tiles.length*32);
		}
	}
	public boolean rectContains(Rectangle rect,Point p){
		return rect.x+rect.width>=p.x && rect.x <=p.x && rect.y+rect.height>=p.y
			&& rect.y<=p.y;
	}
}
