package dom;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import adm.ADM;

public class Projector extends DynamicObject {
	public Projector(){}
	public Projector(int x, int y, int direction, int assetIndex, int frame){
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.assetIndex = assetIndex;
		this.frame = frame;
	}
	
	public void draw(Graphics g){
		BufferedImage img = ADM.getInstance().getProjectorAsset(assetIndex, direction*DynamicObject.max_frame + frame);
		g.drawImage(img, x-img.getWidth(), y-img.getHeight(), null);
	}
}