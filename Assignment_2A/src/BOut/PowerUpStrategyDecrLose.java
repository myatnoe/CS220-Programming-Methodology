package BOut;

/**
 * The DecrLose strategy:
 *   decrement the score multiplier if lost
 * 
 * @author Eliot Moss
 */
public final class PowerUpStrategyDecrLose extends PowerUpStrategyAbstract {

  /* (non-Javadoc)
   * @see BOut.PowerUpStrategyAbstract#lose()
   */
  @Override
  public void lose () {
    GameState.getGameState().decrementMultiplier();
  }

}
