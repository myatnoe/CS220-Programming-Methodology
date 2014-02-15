package BOut;

import com.golden.gamedev.object.*;
import com.golden.gamedev.object.collision.*;

/**
 * Handles collision of PowerUp with the Boundary
 * 
 * @author Eliot Moss
 */
public class BoundaryPowerUpCollision extends CollisionBounds 
{
  public BoundaryPowerUpCollision (Background b)
  {
    super(b);
  }
	
  /**
   * @param powerUp the colliding PowerUp
   */
  public void collided (Sprite powerUp)
  {
    PowerUp thePowerUp = (PowerUp) powerUp;
    thePowerUp.collisionWithBounds();
  }
	
}
