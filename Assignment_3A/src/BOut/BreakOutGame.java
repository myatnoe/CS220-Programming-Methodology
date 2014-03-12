package BOut;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.object.collision.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;

import static BOut.BreakOutEngine.*;
import XmlImporter.*;

/**
 * This single game file loads and plays any level of the Break Out game
 * @author Eliot Moss
 */
public class BreakOutGame extends GameObject 
{
	/**
	 * the game playing field
	 */
	PlayField playField;

	/**
	 * the game background
	 */
	Background background;

	/**
	 * SpriteGroup for the balls (only contains one at a time)
	 */
	SpriteGroup balls;

	/**
	 * SpriteGroup for the paddles (only contains one)
	 */
	SpriteGroup paddles;

	/**
	 * SpriteGroup for the blocks (generally contains many)
	 */
	SpriteGroup blocks;

	/**
	 * SpriteGroup for powerUps (starts empty)
	 */
	SpriteGroup powerUps;

	/**
	 * we assume a single paddle; this is it
	 */
	Paddle gamePaddle;

	/**
	 * constant giving the width of the paddle
	 */
	static final int PADDLE_WIDTH = 104;

	/**
	 * font to use for the score display
	 */
	GameFont scoreFont;

	/**
	 * font to use for indicating level is won or list
	 */
	GameFont doneFont;

	/**
	 * for starting the game over sounds just once
	 */
	boolean noEndSound = true;

	/**
	 * name of sound file for when you win
	 */
	private static final String WIN_SOUND = SoundsDirectory + "you-win.wav";

	/**
	 * name of sound file for when you lose
	 */
	private static final String LOSE_SOUND = SoundsDirectory + "you-lose.wav";

	/**
	 * The saved game snapshot, if any
	 */
	private Memento snapshot = null;

	/**
	 * Nested class for game mementos (snapshots).
	 * These can be used within a game, to go back to a previous point within
	 * the same session, or can be archived to a file and restored in (the same
	 * or) a later session.
	 */
	public static class Memento
	{
		// Save List<Ball>, List<Block>, and List<PowerUp>
		private List<Ball> ballMemento;
		private List<Block> blockMemento;
		private List<PowerUp> powerupMemento;
		private Paddle paddleMemento;
		private GameState gsMemento;
		
		
		/**
		 * factory method for trying to create a Memento of a game
		 * @param game the BreakOutGame to snapshot
		 * @param state the GameState to snapshot with it
		 * @return the new Memento
		 */
		private static Memento makeMemento (BreakOutGame game, GameState state)
		{
			return new Memento(game, state);
		}

		/**
		 * Create a memento from a given BreakOutGame
		 * @param game the BreakOutGame to snapshot
		 * @param state the GameState at the same time
		 */
		private Memento (BreakOutGame game, GameState state)
		{
			//throw new AssertionError("Memento is not yet implemented");
			ballMemento = new ArrayList<Ball>();
			blockMemento = new ArrayList<Block>();
			powerupMemento = new ArrayList<PowerUp>();
			paddleMemento = game.gamePaddle.memento();
			gsMemento = state.memento();
			
			for(Sprite powerUp : game.powerUps.getSprites()){
				if(powerUp != null && powerUp.isActive())
					powerupMemento.add(((PowerUp) powerUp).memento());
			}
			
			for(Sprite ball : game.balls.getSprites()){
				if(ball != null && ball.isActive())
					ballMemento.add(((Ball) ball).memento());
			}
			
			for(Sprite block : game.blocks.getSprites()){
				if(block != null && block.isActive())
					blockMemento.add(((Block) block).memento());
			}
			
//			for(Sprite paddle : game.paddles.getSprites()){
//				if(paddle != null && paddle.isActive())
//					paddleMemento = ((Paddle) paddle).memento();
//			}
		}
		
		public void restoreStateAndSprites(BreakOutGame g){
			for(PowerUp p : powerupMemento){
				g.addPowerUp(p.memento());
			}
			
			for(Ball b : ballMemento){
				g.addBall(b.memento());
			}
			
			for(Block bk : blockMemento){
				g.addBlock(bk.memento());
			}
			
			g.gamePaddle = paddleMemento.memento();
			g.addPaddle(g.gamePaddle);
		}
		
		public GameState getGameStateMemento(){
			return gsMemento;
		}

	}

	/**
	 * create a Memento of this game's current situation
	 * @return a Memento capturing the current game situation
	 */
	public Memento memento ()
	{
		return Memento.makeMemento(this, GameState.getGameState());
	}

	/**
	 * Constructor; just calls superclass constructor
	 * @param engine
	 */
	public BreakOutGame (GameEngine engine)
	{
		super(engine);
	}

	/**
	 * Constructor for creating a new game from a Memento snaphot
	 * @param engine the GameEngine
	 * @param snapshot the Memento to restore from
	 */
	public BreakOutGame (GameEngine engine, Memento snapshot)
	{
		super(engine);
		this.snapshot = snapshot;
	}

	/**
	 * Does the real work of setting up the game;
	 * be careful about changing the order of things because
	 * some steps depend on the initializations done by earlier ones
	 */
	@Override
	public void initResources () 
	{
		initEventSources();
		background = createBackground();
		playField = createPlayField(background);
		SpriteGroup[] groups = createSpriteGroups();
		addSpriteGroupsToPlayField(playField, groups);
		createAndAddCollisionGroups(playField, background);
		initializeFonts();
		setDisplayRate(60);
		if (snapshot == null)
		{
			GameState.getGameState().startLevel();
			createPaddle(background);
			createBlocks();
		}
		else
		{
			// the restore-from-snapshot case
			GameState.setGameState(snapshot.getGameStateMemento());
			snapshot.restoreStateAndSprites(this);
			GameState.getGameState().restart();
		}
	}

	/**
	 * initialize the event sources or active/inactive states of Sprites
	 */
	private void initEventSources ()
	{
		Ball.newGame();
		PowerUp.newGame();
		Block.newGame();
	}

	/**
	 * create a background image for this game;
	 * at the moment this is a bland gray and we do not make
	 * provision for anything other than a fixed size, 800x600
	 * @return a Background for the game
	 */
	private Background createBackground ()
	{
		return new ColorBackground(Color.gray, 800, 600);
	}

	/**
	 * create the PlayField and set its Background as requested
	 * @param background the Background to use for the new PlayField
	 * @return the new PlayField 
	 */
	private PlayField createPlayField (Background background)
	{
		// create the playing field
		PlayField playField = new PlayField();
		playField.setBackground(background);
		return playField;
	}

	/**
	 * creates a SpriteGroup for each kinds of Sprite in the game,
	 * installs each one, and returns an array of them all 
	 */
	private SpriteGroup[] createSpriteGroups ()
	{
		balls    = new SpriteGroup("balls");
		paddles  = new SpriteGroup("paddles");
		blocks   = new SpriteGroup("blocks");
		powerUps = new SpriteGroup("powerUps");
		return new SpriteGroup[]{balls, paddles, blocks, powerUps};
	}

	/**
	 * adds each group to the PlayField
	 * @param playField the PlayField to which we will add some SpriteGroups
	 * @param groups a SpriteGroup[] giving the SpriteGroups to add 
	 */
	private void addSpriteGroupsToPlayField (PlayField playField, SpriteGroup[] groups)
	{
		for (SpriteGroup group : groups)
		{
			playField.addGroup(group);
		}
	}

	/**
	 * creates collision groups for this game and adds them to the PlayField
	 * @param playField the PlayField to which to add the collision groups 
	 * @param background the Background to use for and CollisionBounds objects
	 */
	private void createAndAddCollisionGroups (PlayField playField, Background background)
	{
		CollisionBounds boundBallColl = new CollisionBounds(background) {
			public void collided (Sprite ball) {
				Ball theBall = (Ball) ball;
				// tell the Ball of the collision, and which side(s) are involved
				theBall.collisionWithBounds(
						this.isCollisionSide(CollisionBounds.TOP_COLLISION   ),
						this.isCollisionSide(CollisionBounds.BOTTOM_COLLISION),
						this.isCollisionSide(CollisionBounds.LEFT_COLLISION  ),
						this.isCollisionSide(CollisionBounds.RIGHT_COLLISION ));
			}
		};

		CollisionBounds boundPowerUpColl = new CollisionBounds(background) {
			public void collided (Sprite powerUp) {
				PowerUp thePowerUp = (PowerUp) powerUp;
				thePowerUp.collisionWithBounds();
			}
		};

		CollisionGroup ballPaddleColl    = new CollisionGroup() {
			public void collided (Sprite ball, Sprite paddle) {
				Paddle thePaddle = (Paddle) paddle;
				Ball theBall = (Ball) ball;
				// tell the Ball of the collision (the Paddle doesn't care)
				theBall.collisionWithPaddle(thePaddle);     
			}
		};

		CollisionGroup powerUpPaddleColl = new CollisionGroup() {
			public void collided (Sprite powerUp, Sprite paddle) {
				PowerUp thePowerUp = (PowerUp) powerUp;
				thePowerUp.collisionWithPaddle();
			}
		};

		CollisionGroup ballBlockColl     = new CollisionGroup() {
			public void collided (Sprite ball, Sprite block) {
				Block theBlock = (Block) block;
				Ball theBall = (Ball) ball;
				// tell the block it was hit by a Ball
				theBlock.collisionWithBall();
				// compute side of the Block that the Ball hit
				// (determines how Ball bounces)
				boolean onBottom = false;
				boolean onTop    = false;
				boolean onLeft   = false;
				boolean onRight  = false;
				// need to take direction into account to avoid double collisions
				// (because of how Golden T computes collisions)
				if        (((getCollisionSide() & BOTTOM_TOP_COLLISION) != 0) && (theBall.getVerticalSpeed() > 0))
				{
					onBottom = true;
				} else if (((getCollisionSide() & TOP_BOTTOM_COLLISION) != 0) && (theBall.getVerticalSpeed() < 0))
				{
					onTop = true;
				} else if (((getCollisionSide() & LEFT_RIGHT_COLLISION) != 0) && (theBall.getHorizontalSpeed() < 0))
				{
					onLeft = true;
				} else if (((getCollisionSide() & RIGHT_LEFT_COLLISION) != 0) && (theBall.getHorizontalSpeed() > 0))
				{
					onRight = true;
				}
				// tell the Ball about its collision
				theBall.collisionWithBlock(onTop, onBottom, onLeft, onRight);
			}
		};

		playField.addCollisionGroup(balls   , paddles, ballPaddleColl   );
		playField.addCollisionGroup(powerUps, paddles, powerUpPaddleColl);
		playField.addCollisionGroup(balls   , null   , boundBallColl    );
		playField.addCollisionGroup(powerUps, null   , boundPowerUpColl );
		playField.addCollisionGroup(balls   , blocks , ballBlockColl    );
	}

	/**
	 * creates the single Paddle of the game
	 * @param Background the Background, both for using as an image Background for
	 * the paddle and to determine the Paddle's starting position 
	 */
	private void createPaddle (Background background)
	{
		gamePaddle = new Paddle(this.background.getWidth() / 2,     // centered horizontally
				this.background.getHeight() - 50);  // near the bottom
		this.addPaddle(gamePaddle);
	}

	/**
	 * loads information about blocks from an XML file;
	 * the methods there will call the utility routine addBlock,
	 * in this class, to add each block 
	 */
	private void createBlocks ()
	{
		// set up and load the game:
		// this handles the blocks, etc.
		LevelLoader xmlLoader = new LevelLoader(this);
		xmlLoader.loadGame(((BreakOutEngine)parent).nextLevel);
	}

	/**
	 * set up the font(s) needed by the game for displaying text
	 */
	private void initializeFonts ()
	{
		GameFontManager fontMgr = new GameFontManager();
		scoreFont = fontMgr.getFont(new Font("serif", Font.BOLD, 16), Color.BLACK);
		doneFont  = fontMgr.getFont(new Font("sansserif", Font.BOLD, 16), new Color(128, 0, 0));
	}

	/**
	 * @param fps an int giving the target display rate, in frames per secound
	 */
	private void setDisplayRate (int fps)
	{
		this.setFPS(fps);
	}

	/**
	 * this is called once per frame, to cause us to update the model and the screen
	 */
	public void update (long elapsedTime) 
	{
		positionPaddleFromMouse();
		processInput();
		setMessage();
		handleEndOfGame();
		performAction(elapsedTime);
	}

	/**
	 * moves the paddle to follow the mouse (horizontally)
	 */
	private void positionPaddleFromMouse ()
	{
		
		gamePaddle.setX(this.getMouseX() - (PADDLE_WIDTH / 2));
	}

	/**
	 * The possible situations with respect to input keystrokes or mouse actions
	 */	
	private enum InputState{
		//pass the game in to gotB, gotR, and gotY
		
		Normal{
			public InputState gotP(){
				return InputState.Pausing;
			}
			public InputState gotB(BreakOutGame breakOutGame) {
				GameState gs = GameState.getGameState();
				if (gs.canFireBall())
				{
					breakOutGame.startNewBall(gs);
				}
				return this;
			}
			
			public InputState gotT(){
				Ball.toggleSpin();
				return this;
			}
			public void performAction(BreakOutGame breakOutGame, long elapsedTime) {
				breakOutGame.playField.update(elapsedTime);
			}
		}
		, Won{
			public String message(){
				return "Level Over ... CONGRATULATIONS, YOU WON!  Press Y to return to Menu";
			}
		}
		, Lost{
			public String message(){
				return "Level Over ... Sorry, you lost.  Press Y to return to Menu";
			}
		}
		, Pausing{
			public InputState gotP(){
				return InputState.Normal;
			}
			public String message(){
				return "PAUSED (press P again to unpause)";
			}
		}
		, Quitting{
			public InputState gotY(BreakOutGame breakOutGame){
				breakOutGame.parent.nextGameID = BreakOutEngine.BreakOutGameMenu;
				return InputState.Finish;
			}
			public String message(){
				return "QUIT? (Y or N)";
			}
		}
		, Restoring{
			public InputState gotY(BreakOutGame breakOutGame) {
				breakOutGame.parent.nextGame = new BreakOutGame(breakOutGame.parent, breakOutGame.snapshot);
				return InputState.Finish;
			}
			public String message(){
				return "Restore from snapshot? (Y or N)";
			}
		}
		, RestoringNoSnapshot{
			public String message(){
				return "No snapshot available; press N to continue";
			}
		}
		, Saving{
			public InputState gotY(BreakOutGame breakOutGame) {
				breakOutGame.snapshot = breakOutGame.memento();
				return InputState.Normal;
			}
			public String message(){
				return "Make a snapshot now? (Y or N) (any previous one is lost)";
			}
		}
		, Finish{
			public void performAction(BreakOutGame breakOutGame, long elapsedTime) {
				breakOutGame.finish();
			}
		}
		;
		public InputState gotX(){
			return InputState.Normal;
		}

		public InputState gotB(BreakOutGame breakOutGame) {
			return this;
		}

		public InputState gotP(BreakOutGame breakOutGame) {
			return this;
		}

		public InputState gotY(BreakOutGame breakOutGame) {
			return this;
		}
		
		public InputState gotR(BreakOutGame breakOutGame) {
			if(breakOutGame.snapshot == null) return InputState.RestoringNoSnapshot;
			return InputState.Restoring;
		}

		public InputState gotT() {
			return this;
		}

		public InputState gotQ() {
			return InputState.Quitting;
		}

		public InputState gotS() {
			return InputState.Saving;
		}
		
		public String message(){
			return null;
		}

		public void performAction(BreakOutGame breakOutGame, long elapsedTime) {
			
		}
		
	}

	/**
	 * The current input state, initially Normal
	 */
	private InputState state = InputState.Normal;

	/**
	 * handles any button or mouse presses
	 */
	private void processInput ()
	{
		switch (bsInput.getKeyPressed()) {
		case KeyEvent.VK_B: // b tries to fire a new ball
			// can fire a ball only in the Normal state
			state = state.gotB(this);
			break;

		case KeyEvent.VK_N: // "No"
			state = state.gotX();
			break;

		case KeyEvent.VK_P: // "Pause" (and unpause)
			state = state.gotP(this);
			break;

		case KeyEvent.VK_Q: // "Quit"
			state = state.gotQ();
			break;

		case KeyEvent.VK_R: // "Restore" from the snapshot
			state = state.gotR(this);
			break;

		case KeyEvent.VK_S: // "Snapshot" (make one)
			state = state.gotS();
			break;

		case KeyEvent.VK_Y: // "Yes"
			state = state.gotY(this);
			break;
			
		case KeyEvent.VK_T: // "Toggle Spin"
			// can toggle spin only in the Normal state
			// toggling spin does not change state
			state = state.gotT();
			break;
		default:
			// get here on any other key, or none
		}
	}

	/**
	 * starts a new ball in the game
	 * @param gs the current GameState
	 */
	private void startNewBall (GameState gs)
	{
		Ball ball = new Ball(400, 400);       // start in center of screen
		double speed = gs.getBallSpeed();
		double angle = 0.75D * Math.PI;  // 45 degrees down and to the left
		ball.setVelocityPolar(speed, angle);
		ball.setActive(true);
		this.addBall(ball);
	}

	/**
	 * The message to display
	 */
	private String message;

	/**
	 * set the "message" field according to our state, for display in render()
	 */
	private void setMessage ()
	{
		message = state.message();
	}

	/**
	 * deals with playing the win/lose sound, waiting for a keypress to exit, etc.
	 */
	private void handleEndOfGame ()
	{
		GameState gs = GameState.getGameState();
		if (gs.levelDone())
		{
			if (noEndSound)
			{
				noEndSound = false;
				boolean won = gs.wonLevel();
				this.bsSound.play(won ? WIN_SOUND : LOSE_SOUND);
				if (state == InputState.Normal)
				{
					state = (won ? InputState.Won : InputState.Lost);
				}
			}
			if (bsInput.isKeyPressed(KeyEvent.VK_ESCAPE))
			{
				parent.nextGameID = BreakOutEngine.BreakOutGameMenu;
				finish();
			}
		}
	}

	/**
	 * do any action appropriate to the state
	 * @param elapsedTime a long giving the number of milliseconds that have
	 * elapsed since the last call to update(); useful if we are going to
	 * update the playfield
	 */
	private void performAction (long elapsedTime)
	{
		state.performAction(this, elapsedTime);
		
	}

	/**
	 * returns an image, given the key part of its file name
	 * @param which a String giving the key part of the image's file name
	 * @return a BufferedImage, fetched from the file of conventional type
	 * (.png) in the directory set to hold images (GraphicsDirectory)
	 */
	private BufferedImage getImageFromFile (String which)
	{
		return getImage(GraphicsDirectory + which + ".png");
	}

	/**
	 * handle the rendering; just the playing field plus the score
	 */
	public void render (Graphics2D g)
	{
		playField.render(g);
		updateTextualDisplay(g);
	}

	/**
	 * updates the textual parts of the display from the GameState 
	 * @param g the Graphcs2D to which to display things
	 */
	private void updateTextualDisplay (Graphics2D g)
	{
		GameState theState = GameState.getGameState();
		scoreFont.drawString(g, "Score: " + theState.getScore(), 10, 10);
		scoreFont.drawString(g, "Multiplier: " + theState.getMultiplier(), 10, 30);
		scoreFont.drawString(g, "Balls left: " + theState.getBallsRemaining(), 10, 50);
		scoreFont.drawString(g, "Spin: " + Ball.reportSpin(), 10, 70);
		if (message != null)
		{
			doneFont.drawString(g, message, 10, 110);
		}
	}    

	/**
	 * Utility routine or adding a paddle
	 * @param p a Paddle to add
	 */
	public void addPaddle (Paddle p)
	{
		p.setImage(getImageFromFile("Paddle"));
		p.setBackground(background);
		paddles.add(p);
	}

	/**
	 * Utility routine for adding a block; insures it is added to the SpriteGroup
	 * @param b a Block to add
	 */
	public void addBlock (Block b)
	{
		b.setAudio(bsSound);
		b.setGame(this);
		this.blocks.add(b);
	}

	/**
	 * Utility routine for adding a ball
	 * @param b a Ball to add
	 */
	public void addBall (Ball b)
	{
		b.setImage(getImageFromFile("Ball"));
		b.setBackground(background);
		b.setAudio(bsSound);
		balls.add(b);
	}

	/**
	 * Utility routine for adding a PowerUp
	 * @param p a PowerUp to add
	 */
	public void addPowerUp (PowerUp p)
	{
		p.setBackground(background);
		p.setAudio(bsSound);
		powerUps.add(p);
	}

	/**
	 * Utility routine to drop a powerUp
	 * @param x a double giving the x position where the powerUp starts 
	 * @param y a double giving the y position there the powerUp starts
	 * @param type a String indicating the type of powerUp
	 */
	public void dropPowerUp (double x, double y, String type)
	{
		PowerUp powerUp = PowerUpFactory.createPowerUp(type, this, x, y);
		powerUp.setSpeed(0, .1);
		powerUp.setActive(true);
		this.addPowerUp(powerUp);
	}

}
