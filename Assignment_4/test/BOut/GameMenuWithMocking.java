package BOut;

import com.golden.gamedev.*;
import com.golden.gamedev.engine.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;

public class GameMenuWithMocking extends GameMenu {
  
  private PlayField mockField;
  private ImageBackground mockBackground;
  private BaseInput mockBaseInput;
  private String levelsDir; 
  private String archivesDir;
  public boolean sawFinish = false;

  // for testing update and render.
  protected GameMenuWithMocking (GameEngine engine,
      BaseInput mockBaseInput, String levelsDir, String archivesDir) {
    super(engine);
    this.mockBaseInput = mockBaseInput;
    this.levelsDir = levelsDir;
    this.archivesDir = archivesDir;
  }
  
  // for testing initResources()
  protected GameMenuWithMocking (GameEngine engine,
      PlayField mockField, ImageBackground mockBackground, String levelsDir, String archivesDir) {
    super(engine);
    this.mockField      = mockField;
    this.mockBackground = mockBackground;
    this.levelsDir = levelsDir;
    this.archivesDir = archivesDir;
  }

  /**
   * override so that we can substitute a mock PlayField
   */
  @Override
  protected PlayField createPlayField (Background background) {
    if (mockField == null) {
      return super.createPlayField(background);
    } else {
      mockField.setBackground(background);
      return mockField;
    }
  }

  @Override
  protected ImageBackground createBackground () {
    return (mockBackground == null) ? super.makeImageBackground() : mockBackground;
  }
  
  @Override
  protected BaseInput getBsInput () {
    return (mockBaseInput == null) ? super.getBsInput() : mockBaseInput;
  }
  
  @Override
  protected String getLevelsDirName () {
    return levelsDir;
  }
  
  @Override
  protected String getArchivesDirName () {
    return archivesDir;
  }
  
  @Override
  public void finish () {
    sawFinish = true;
  }

  private Sprite mouseSprite;
  
  public void setMouseSprite (Sprite sprite) {
    mouseSprite = sprite;
  }
  
  @Override
  public boolean mouseOver (Sprite sprite) {
    return sprite == mouseSprite;
  }
  
  public Sprite swapPlay (Sprite newPlay) {
    Sprite oldPlay = play;
    play = newPlay;
    return oldPlay;
  }
  
  public Sprite swapQuit (Sprite newQuit) {
    Sprite oldQuit = quit;
    quit = newQuit;
    return oldQuit;
  }
  
  public Sprite swapForward (Sprite newForward) {
    Sprite oldForward = forward;
    forward = newForward;
    return oldForward;
  }
  
  public Sprite swapBackward (Sprite newBackward) {
    Sprite oldBackward = backward;
    backward = newBackward;
    return oldBackward;
  }

  public GameFont swapTitleFont (GameFont newTitle) {
    GameFont oldTitle = titleFont;
    titleFont = newTitle;
    return oldTitle;
  }

  public GameFont swapChoiceFont (GameFont newChoice) {
    GameFont oldChoice = choiceFont;
    choiceFont = newChoice;
    return oldChoice;
  }

  State getState () {
    return state;
  }
}
