import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TypecheckTest {

  File[] testList;
  MiniJavaParser parser;

  @Before
  void setUp() throws FileNotFoundException {
    File testDir = new File("tests");
    testList = testDir.listFiles();
    MiniJavaParser parser = new MiniJavaParser(new FileInputStream(testDir.listFiles()[0]));
  }


  @Test
  void typeCheckAllFiles() throws ParseException, FileNotFoundException {

    for (File test: testList) {
      if (test.getName().contains("error"))
        System.out.println(test.getName()+" -- error");
      else {
        System.out.println(test.getName() + " -- valid");
        parser.ReInit(new FileInputStream(test));
        Typecheck.typeCheck(parser);
      }

    }

    //assertEquals(0, tester.multiply(10, 0), "10 x 0 must be 0");
  }


}