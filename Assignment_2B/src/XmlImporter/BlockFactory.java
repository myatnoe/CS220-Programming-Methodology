package XmlImporter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import breakout.BlockTemplateDocument.BlockTemplate;
import breakout.ColorTypeDocument.ColorType;

/**
 * Singleton Factory that constructs bufferedImages out of the block specification in a Block Template.
 * 
 * @author Paul Barba
 */
public class BlockFactory {
	
  // the singleton instance
  private static BlockFactory instance = new BlockFactory();
	
  // private to enforce singleton
  private BlockFactory () { }

  /**
   * getter for the singleton
   * @return the singleton BlockFactory instance
   */
  public static BlockFactory getInstance ()
  {
    return instance;
  }

  /**
   * remembers already-computed block images, to save work and space
   */
  private Hashtable<String, BufferedImage> computedBlocks = new Hashtable<String, BufferedImage>();

  /**
   * Static function so you can get Blocks without having to use getInstance() every time. This class could
   * have been made static, especially since there's only one method. It may make sense to add other functions
   * to the block Factory for handling different block descriptions, for requesting modified blocks, or animated
   * blocks, etc.
   * 
   * @param template the description of the block
   * @return An image
   */
  public static BufferedImage getBlock (BlockTemplate template)
  {
    ColorType color = template.getColorType();
    return instance.getBlockImage(template.getWidth(), template.getHeight(),
                                  color.getR(), color.getG(), color.getB());
  }
	
  /**
   * Gets a block image with the specified properties
   * @param width an int giving the width in pixels
   * @param height an int giving the height in pixels
   * @param r an int giving the red intensity (0-255)
   * @param g an int giving the green intensity (0-255)
   * @param b an int giving the blue intensity (0-255)
   * @return the new, or previously computer, BlockImage
   */
  public BufferedImage getBlockImage(String width, String height, int  r, int g, int b)
  {
    // Caching of blocks is important, because a level will tend to use many identical blocks.
    String description = width+"_"+height+"_"+r+"_"+g+"_"+b;
    if (computedBlocks.contains(description))
    {
      return computedBlocks.get(description);
    }
    else
    {
      BufferedImage image = new BufferedImage(Integer.parseInt(width), Integer.parseInt(height),
                                              BufferedImage.TYPE_3BYTE_BGR);
      Graphics2D graphics = image.createGraphics();
      graphics.setColor(new Color(r, g, b));
      graphics.fill3DRect(0, 0, Integer.parseInt(width), Integer.parseInt(height), true);
      computedBlocks.put(description, image);
      return image;
    }
  }
}
