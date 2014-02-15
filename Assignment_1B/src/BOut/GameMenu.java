package BOut;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import static BOut.BreakOutEngine.*;

/**
 * The menu screen. Currently just lets you select the level, but you could add high score tables, options, etc.
 * @author Paul Barba
 */
public class GameMenu extends GameObject {

  /**
   * PlayField for the game choice menu screen;
   */
  private PlayField field;

  /**
   * Font used for the title of the game
   */
  private GameFont titleFont;

  /**
   * Font used for the name of the currently chosen level of the game 
   */
  private GameFont choiceFont;

  /**
   * Array of xml level description files found
   */
  private File[] levelChoices;

  /**
   * Currently chosen level (index in levelChoices), defaults to 0,
   * which should correspond to the first level 
   */
  private int levelIndex = 0;

  /**
   * The name of the currently chosen level, for display 
   */
  private String levelName;

  /**
   * Sprite (roughly, "image") for moving forward in the menu list 
   */
  private Sprite forward;

  /**
   * Sprite (roughly, "image") for moving backward in the menu list 
   */
  private Sprite backward;

  /**
   * Sprite for playing the chosen level
   */
  private Sprite play;

  /**
   * Sprite for quitting entirely
   */
  private Sprite quit;
  
  /**
   * horiz position on which to center the level name
   */
  private int nameCenter;
  
  /**
   * vert position for the name
   */
  private int nameYpos;
  
  /**
   * vert position for the title
   */
  private int titleYpos;
  
  /**
   * Default constructor -- nothing special
   * @param engine
   */
  public GameMenu (GameEngine engine)
  {
    super(engine);
  }

  /**
   * Prepares the "playing field" for rendering and receiving input actions 
   */
  public void initResources () 
  {
    // Found in a search for 800x600 public domain images.
    Background background = createBackground();
    field = createPlayField(background);
    initializeFonts();
    populateLevelChoices();

    int maxWidth = determineMaxNameWidth();
    int halfMaxWidth = (maxWidth + 1) / 2; // round up
    createAndPositionSprites(background, halfMaxWidth);

    prepareLevelName();
  }

  /**
   * @return a suitable Background
   */
  private Background createBackground () {
    Background bground = new ImageBackground(
        super.getImage(GraphicsDirectory + "MenuBackground.jpg"));
    return bground;
  }
	
  /**
   * @param background the desired Background
   * @return a new PlayField with that BackGround
   */
  private PlayField createPlayField (Background background) {
    PlayField field = new PlayField();
    field.setBackground(background);
    return field;
  }

  /**
   * sets up our chosen Font objects 
   */
  private void initializeFonts() {
    // Fonts are used to write to the screen
    GameFontManager fontMgr = new GameFontManager();
    // This color is "half-strength" pure red
    titleFont = fontMgr.getFont(new Font("sansserif", Font.BOLD, 48), new Color(128, 0, 0));
    choiceFont = fontMgr.getFont(new Font("serif", Font.BOLD, 24), Color.BLACK);
  }

  /**
   * scan levles directory for level files and
   * create sorted array of their names
   */
  private void populateLevelChoices () {
    File levelDirectory = new File("levels");
    levelChoices = levelDirectory.listFiles(
      new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.matches("Level\\d+\\.xml");
        }
      });
    Arrays.sort(levelChoices, new Comparator<File>() {
      public int compare (File f1, File f2) {
        return f1.getName().compareTo(f2.getName());
      }
    });
  }

  /**
   * @return an int giving the maximum width of a level name,
   * in pixels, in our choice font
   */
  private int determineMaxNameWidth () {
    int maxWidth = 0;
    for (File f : levelChoices)
    {
      String name = f.getName();
      int width = choiceFont.getWidth(name);
      if (maxWidth < width)
      {
        maxWidth = width;
      }
    }
    return maxWidth;
  }

  /**
   * creates Sprites for the buttons, positioning them using information
   * from the arguments
   * @param background the Background object (for determine width and height)
   * @param halfMaxWidth an int giving 1/2 the max width of a level name when displayed
   */
  private void createAndPositionSprites (Background background, int halfMaxWidth) {
    // approximate layout:
    //  <    level-name    >
    //    PLAY        QUIT
    
    titleYpos = background.getHeight() / 3;
    
    nameCenter = background.getWidth() / 2;
    int gap = 10; // to separate forward/backward from name
    
    play = new Sprite(getImage(GraphicsDirectory + "PlayButton.png"));
    field.add(play);
    int startYpos = background.getHeight() - 2*gap - play.getHeight();
    play.setLocation(nameCenter - play.getWidth() - gap, startYpos);

    quit = new Sprite(getImage(GraphicsDirectory + "QuitButton.png"));
    field.add(quit);
    quit.setLocation(nameCenter + gap, startYpos);
    
    forward = new Sprite(getImage(GraphicsDirectory + "RightButton.png"));
    field.add(forward);
    nameYpos = startYpos - gap - forward.getHeight();
    forward.setLocation(nameCenter+halfMaxWidth+gap, nameYpos);

    backward = new Sprite(getImage(GraphicsDirectory + "LeftButton.png"));
    field.add(backward);
    backward.setLocation(nameCenter-halfMaxWidth-gap-backward.getWidth(), nameYpos);
  }

  private enum MyEvent {
    None,
    ForwardLevel,
    BackwardLevel,
    Play,
    Quit;
  }
  
  /**
   * Called to update the screen.
   * Here just checks for input events and updates information for
   * rendering (or starts the chosen game)
   * @param elaspedTime time since last update (not very relevant here)
   */
  @Override
  public void update (long elapsedTime) 
  {
    field.update(elapsedTime);
    processInput();
  }

  /**
   * checks for mouse or keyboard input, and responds by
   * changing the display or invoking a game
   */
  private void processInput () {
    switch (getInputEvent()) {
    case ForwardLevel:
      levelIndex++;
      prepareLevelName();
      break;
    case BackwardLevel:
      levelIndex--;
      prepareLevelName();
      break;
    case Play:
      parent.nextGameID = BreakOutEngine.BreakOutGame;
      ((BreakOutEngine)parent).nextLevel = levelChoices[levelIndex].getAbsolutePath();
      finish();
      break;
    case Quit:
      parent.nextGameID = BreakOutEngine.BreakOutQuit;
      finish();
      break;
    case None:
      break;
    }
  }

  /**
   * @return a MyEvent describing any input event (or None is there is none)
   */
  private MyEvent getInputEvent() {
    if (bsInput.isMousePressed(MouseEvent.BUTTON1)) {
      return getMouseEvent();
    } else {
      return getKeyEvent();  // may return MyEvent.None
    }
  }
  
  /**
   * Checks mouse position and determines appropriate event, if any
   * @return the MyEvent for the current mouse position 
   */
  private MyEvent getMouseEvent ()
  {
    if (mouseOver(forward))
      return MyEvent.ForwardLevel;
    else if (mouseOver(backward))
      return MyEvent.BackwardLevel;
    else if (mouseOver(play))
      return MyEvent.Play;
    else if (mouseOver(quit))
      return MyEvent.Quit;
    else
      return MyEvent.None;
  }
  
  /**
   * Return the MyEvent for the current key pressed, if any
   * @return the MyEvent for the current key pressed (None if none)
   */
  private MyEvent getKeyEvent ()
  {
    if (bsInput.isKeyPressed(KeyEvent.VK_RIGHT))
      return MyEvent.ForwardLevel;
    else if (bsInput.isKeyPressed(KeyEvent.VK_LEFT))
      return MyEvent.BackwardLevel;
    else if (bsInput.isKeyPressed(KeyEvent.VK_ENTER))
      return MyEvent.Play;
    else if (bsInput.isKeyPressed(KeyEvent.VK_Q))
      return MyEvent.Quit;
    else
      return MyEvent.None;
  }
  
  /**
   * paint the menu screen
   */
  public void render (Graphics2D g)
  {
    field.render(g);
    String title = "UMASS BREAKOUT";
    int titleWidth = titleFont.getWidth(title);
    titleFont.drawString(g, title, nameCenter-(titleWidth/2), titleYpos);
    int nameWidth = choiceFont.getWidth(levelName);
    choiceFont.drawString(g, levelName, nameCenter-(nameWidth/2), nameYpos);
  }
  
  /**
   * Tests whether the mouse is currently over a given sprite.
   * Used for telling what to do on mouse presses.
   * @param s the sprite
   * @return true if the mouse is over the sprite
   */
  private boolean mouseOver (Sprite s)
  {
    return (getMouseX() >= s.getX() && getMouseX() < (s.getX() + s.getWidth ()) &&
            getMouseY() >= s.getY() && getMouseY() < (s.getY() + s.getHeight()));
  }
  
  /*
   * Changes the string the engine writes to the screen for the level name.
   */
  private void prepareLevelName ()
  {
    // wrap levelIndex around if user moved it off the end
    int num = levelChoices.length;
    if (num == 0)
    {
      levelIndex = 0;
      levelName = "";
      return;
    }
    levelIndex = (levelIndex + num) % num; // works for levelIndex < 0, etc.
    String fullName = levelChoices[levelIndex].getName();
    levelName = fullName.substring(0, fullName.indexOf('.'));
  }

}
