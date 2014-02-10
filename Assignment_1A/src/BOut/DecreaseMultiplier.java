package BOut;

public class DecreaseMultiplier implements LosePowerUp {

	// losing points for missing Power Up
	public void updateMultiplier() {
		GameState.getGameState().decrementMultiplier();
	}

}
