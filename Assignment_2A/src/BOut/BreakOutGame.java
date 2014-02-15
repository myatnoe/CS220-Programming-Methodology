package BOut;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.object.collision.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
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
   * Constructor; just calls superclass constructor
   * @param engine
   */
  public BreakOutGame (GameEngine engine)
  {
    super(engine);
  }

  /**
   * Does the real work of setting up the game;
   * be careful about changing the order of things because
   * some steps depend on the initializations done by earlier ones
   */
  @Override
  public void initResources () 
  {
    GameState.getGameState().startLevel();
    background = createBackground();
    playField = createPlayField(background);
    SpriteGroup[] groups = createSpriteGroups();
    addSpriteGroupsToPlayField(playField, groups);
    createAndAddCollisionGroups(playField, background);
    createPaddle(background);
    createBlocks();
    initializeFonts();
    setDisplayRate(60);
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
    CollisionBounds boundBallColl    = new BoundaryBallCollision(background);
    CollisionBounds boundPowerUpColl = new BoundaryPowerUpCollision(background);
    CollisionGroup ballPaddleColl    = new BallPaddleCollision();
    CollisionGroup powerUpPaddleColl = new PowerUpPaddleCollision();
    CollisionGroup ballBlockColl     = new BallBlockCollision();

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
    gamePaddle.setImage(getImageFromFile("Paddle"));
    gamePaddle.setBackground(background);
    paddles.add(gamePaddle);
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
    playField.update(elapsedTime);
  }

  /**
   * moves the paddle to follow the mouse (horizontally)
   */
  private void positionPaddleFromMouse ()
  {
    gamePaddle.setX(this.getMouseX() - (PADDLE_WIDTH / 2));
  }

  /**
   * handles any button or mouse presses
   */
  private void processInput ()
  {
    switch (bsInput.getKeyPressed()) {
    case KeyEvent.VK_B: { // b tries to fire a new ball
      GameState gs = GameState.getGameState();
      if (gs.canFireBall()) {
        startNewBall(gs);
      }
      break;
    }
    case KeyEvent.VK_Q: { // q quits the game
      this.parent.nextGameID = BreakOutEngine.BreakOutGameMenu;
      this.finish();
      break;
    }
    case KeyEvent.VK_T: { // t toggles ball spin
      Ball.toggleSpin();
      break;
    }
    }
  }

  /**
   * starts a new ball in the game
   * @param gs the current GameState
   */
  private void startNewBall (GameState gs)
  {
    gs.noteBallActive();
    Ball ball = new Ball(400, 400);       // start in center of screen
    double speed = gs.getBallSpeed();
    double angle = 0.75D * Math.PI;  // 45 degrees down and to the left
    ball.setVelocityPolar(speed, angle);
    ball.setActive(true);
    ball.setBackground(this.background);
    ball.setImage(getImageFromFile("Ball"));
    ball.setAudio(this.bsSound);
    this.balls.add(ball);
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
  }    

  /**
   * Utility routine for adding a block; insures it is added to the SpriteGroup
   * @param b a Block to add
   */
  public void addBlock (Block b)
  {
    this.blocks.add(b);
    b.setAudio(bsSound);
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
    powerUp.setBackground(background);
    powerUp.setAudio(bsSound);
    powerUp.setActive(true);
    powerUps.add(powerUp);
  }

}
