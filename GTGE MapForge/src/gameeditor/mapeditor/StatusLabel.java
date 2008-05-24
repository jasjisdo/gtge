package gameeditor.mapeditor;
import java.awt.Font;
import javax.swing.*;
public class StatusLabel extends JLabel{
	String currentLayer="Layer 1";
	String tile = "[0,0]";
	String mapSize="30x20";
	public StatusLabel(){
		super("Now editing: Layer 1, tile: [0,0] map size: 30x20");
		setFont(new Font("Arial",Font.PLAIN,14));
	}
	public void setTile(int tileX,int tileY){
		tile="["+tileX+","+tileY+"]";
		updateStatus();
	}
	public void setMapSize(int w,int h){
		mapSize=w+"x"+h;
		updateStatus();
	}
	public void setLayer(String s){
		currentLayer=s;
		updateStatus();
	}
	protected void updateStatus(){
		setText("Now editing: "+currentLayer+", tile: "+tile+", map size: "+mapSize);
	}
}