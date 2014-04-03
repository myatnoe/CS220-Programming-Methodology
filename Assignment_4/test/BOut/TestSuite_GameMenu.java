package BOut;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Suite.*;

@RunWith(Suite.class)
@SuiteClasses({
  TestGameMenu_initResources.class,
  TestGameMenu_update_render1.class
  })
public class TestSuite_GameMenu {

}
