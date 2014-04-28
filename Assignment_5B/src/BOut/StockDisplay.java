package BOut;
import java.util.ArrayList;

import com.golden.gamedev.object.Sprite;


public class StockDisplay {

	private int width;
	private int height;
	private int maxColumn;
	private int maxRow;
	private int bottomLeftX;
	private int bottomLeftY;
	private int gap;
	private ArrayList<Sprite> spriteList;
	
	/**
	 * 
	 * @param image width
	 * @param image height
	 * @param maximum row to display
	 * @param maximum column to display
	 * @param bottom left x corner of the screen
	 * @param bottom left y corner of the screen
	 * @param gap between the images
	 */
	public StockDisplay(int width, int height, int maxRow, int maxColumn, int bottomLeftX, int bottomLeftY, int gap){
		this.width = width;
		this.height = height;
		this.maxColumn = maxColumn;
		this.maxRow = maxRow;
		this.bottomLeftX = bottomLeftX;
		this.bottomLeftY = bottomLeftY;
		this.gap = gap;
		this.spriteList = new ArrayList<Sprite>();
		initializeSprite();

	}
	
	private void initializeSprite(){
		
		// start (x,y) for the first ball
		int x = this.bottomLeftX + this.gap;
		int y = this.bottomLeftY - this.gap - this.height;
		for(int count = 1; count <= maxColumn*maxRow; count++){
			Sprite s = new Sprite();
			s.setImmutable(true);
			s.setActive(false);
			s.setX(x);
			s.setY(y);
			spriteList.add(s);
			int[] newxy = getNextPosition(x,y,count);
			x = newxy[0];
			y = newxy[1];
		}
		
	}
	
	private int[] getNextPosition(int x, int y, int current){
		if(current % this.maxRow == 0){ // next column
			x += this.gap + this.width;
			y = this.bottomLeftY - this.gap - this.height;
		}else{ // same column, go up one space
			y -= this.gap + this.height;
		}
		int[] newxy =  {x , y};
		return newxy;
	}
	
	public ArrayList<Sprite> getSprites(){
		return spriteList;
	}
	
	public int getMaxCount(){
		return this.maxColumn*this.maxRow;
	}

}
