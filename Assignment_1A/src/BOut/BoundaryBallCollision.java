package BOut;

import com.golden.gamedev.object.*;
import com.golden.gamedev.object.collision.*;

/**
 * Handles collision of a Ball with the Boundary
 * 
 * @author Eliot Moss
 */
public class BoundaryBallCollision extends CollisionBounds 
{
  public BoundaryBallCollision (Background b)
  {
    super(b);
  }

  /**
   * Calls the Ball's collision method
   * @param ball the colliding Ball
   */
  public void collided (Sprite ball)
  {
    Ball theBall = (Ball) ball;
    // tell the Ball of the collision, and which side(s) are involved
    theBall.collisionWithBounds(
        this.isCollisionSide(CollisionBounds.TOP_COLLISION   ),
        this.isCollisionSide(CollisionBounds.BOTTOM_COLLISION),
        this.isCollisionSide(CollisionBounds.LEFT_COLLISION  ),
        this.isCollisionSide(CollisionBounds.RIGHT_COLLISION ));
  }

}
