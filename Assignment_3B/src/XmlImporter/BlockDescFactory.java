package XmlImporter;

import java.util.ArrayList;
import java.util.List;

import BOut.BlockDesc;
import breakout.BlockTemplateDocument.BlockTemplate;
import breakout.ColorTypeDocument.ColorType;

/**
 * Singleton Factory that constructs BlockDesc descriptions out of the block specification in a Block Template.
 * 
 * @author Eliot MOss
 */
public class BlockDescFactory {
	
  // the singleton instance
  private static BlockDescFactory instance = new BlockDescFactory();
  private static List<BlockDesc> blockList = new ArrayList<BlockDesc>();
	
  // private to enforce singleton
  private BlockDescFactory () { }

  /**
   * getter for the singleton
   * @return the singleton BlockDescFactory instance
   */
  public static BlockDescFactory getInstance ()
  {
    return instance;
  }

  /**
   * Returns the unique BlockDesc corresponding to the template's
   * specification of width, height, and color 
   * @param template a BlockTemplate giving width and height of a rectangular block, and RGB color
   * @return the BlockDesc for that appearance (guaranteed only one for a given appearance)
   */
  public static BlockDesc getBlockDesc (BlockTemplate template)
  {
    int width = Integer.parseInt(template.getWidth());
    int height = Integer.parseInt(template.getHeight());
    ColorType color = template.getColorType();
    int r = color.getR();
    int g = color.getG();
    int b = color.getB();
    return getBlock(width, height, r, g, b);
  }
  
  private static BlockDesc getBlock(int width, int height, int r, int g, int b){
	  for(BlockDesc bd: blockList){
		  if(bd.getWidth() == width && 
				  bd.getHeight() == height &&
				  bd.getR() == r &&
				  bd.getG() == g &&
				  bd.getB() == b){
			  return bd;
		  }
	  }
	  BlockDesc block = new BlockDesc(width, height, r, g, b);
	  blockList.add(block);
	  return block;
  }

}
