package BOut;

/**
 * Implements the PowerUpStrategyDecorator that increase Balls count when hit
 * 
 * @author Eliot Moss
 */
public class PowerUpStrategyDecoratorExtra extends PowerUpStrategyDecorator {

  /**
   * wraps the given strategy with our behavior
   * @param decorated
   */
  public PowerUpStrategyDecoratorExtra (PowerUpStrategy decorated)
  {
    super(decorated);
  }
  
  /**
   * add 1 extra ball count to the GameState
   */
  @Override
  public void activate ()
  {
    super.activate();
    GameState.getGameState().addExtraBall(1);
  }

}
