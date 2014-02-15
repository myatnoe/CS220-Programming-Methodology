package BOut;

/**
 * This abstract class is handy because it gives default
 * implementations of the behaviors
 * 
 * @author Eliot Moss
 *
 */
public abstract class PowerUpStrategyAbstract implements PowerUpStrategy {
  
  /* (non-Javadoc)
   * @see BOut.PowerUpStrategy#activate()
   */
  @Override
  public void activate () {
    // default behavior: do nothing
  }

  /* (non-Javadoc)
   * @see BOut.PowerUpStrategy#lose()
   */
  @Override
  public void lose () {
    // default behavior: do nothing
  }

}
