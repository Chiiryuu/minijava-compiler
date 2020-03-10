import java.util.ArrayList;

public class ClassObject {
  public String className;
  public boolean isMain = false;
  public String parentClass;
  public ArrayList<String> classVariables;
  public ArrayList<String> classMethods;
  public int classSize = 0;


  public ClassObject(String className) {
    this.className = className;
    this.parentClass = null;
    this.classVariables = new ArrayList<>();
    this.classMethods = new ArrayList<>();
  }

  public void setMain() {
    this.isMain = true;
  }

  public ClassObject(String className, String parentClass) {
    this.className = className;
    this.parentClass = parentClass;
    this.classVariables = new ArrayList<>();
    this.classMethods = new ArrayList<>();
  }

  public void addVariable(String var) {
    classVariables.add(var);
  }

  public void addMethod(String method) {
    classMethods.add(method);
  }


  @Override
  public String toString() {
    String result;
    if (!isMain)
      result = "Class "+className+" extends "+parentClass+'\n';
    else
      result = "Main Class "+className+'\n';
    result+="\tvars:\n";
    for (String var:classVariables) {
      result += "\t\t"+var+'\n';
    }
    result+="\tmethods:\n";
    for (String method:classMethods) {
      result += "\t\t"+method+'\n';
    }
    return result;
  }

}
