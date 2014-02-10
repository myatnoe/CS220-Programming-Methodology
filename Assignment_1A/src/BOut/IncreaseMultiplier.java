package BOut;

public class IncreaseMultiplier implements ActivatePowerUp {

	/*
	 *  increment the multiplier if gotten
	 */
	public void updateMultiplier() {
		GameState.getGameState().incrementMultiplier();
	}

}
