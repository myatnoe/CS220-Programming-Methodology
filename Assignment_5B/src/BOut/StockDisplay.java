package BOut;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;


public class StockDisplay {

	private int width;
	private int height;
	private int maxColumn;
	private int maxRow;
	private int totalCount; // total number of sprites
	private int screenHeight;
	private int screenWidth;
	private int gap;
	private ArrayList<Sprite> spriteList;
	
	private BufferedImage image;
	
	public StockDisplay(int width, int height, int maxRow, int maxColumn, int totalCount, int screenWidth, int screenHeight, int gap){
		this.width = width;
		this.height = height;
		this.maxColumn = maxColumn;
		this.maxRow = maxRow;
		this.totalCount = totalCount;
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		this.gap = gap;
		this.spriteList = new ArrayList<Sprite>();
		initializeSprite();

	}
	
	private void initializeSprite(){
		
		// start (x,y) for the first ball
		int x = this.gap;
		int y = this.screenHeight - this.gap - this.height;
		for(int count = 1; count <= maxColumn*maxRow; count++){
			Sprite s = new Sprite();
			s.setImmutable(true);
			//s.setActive(false);
			s.setX(x);
			s.setY(y);
			spriteList.add(s);
			int[] newxy = getNextPosition(x,y,count);
			x = newxy[0];
			y = newxy[1];
		}
		
	}
	
	private int[] getNextPosition(int x, int y, int current){
		if(current % this.maxRow == 0){
			x += this.gap + this.width;
			y = this.screenHeight - this.gap - this.height;
		}else{
			y -= this.gap + this.height;
		}
		int[] newxy =  {x , y};
		return newxy;
	}
	
	public ArrayList<Sprite> getSprites(){
		return spriteList;
	}

}
