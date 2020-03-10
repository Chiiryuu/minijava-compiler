import java.util.ArrayList;

public class MethodObject {
  public String methodName;
  public String className;
  public String[] inputs;


  public MethodObject(String methodName) {
    this.methodName = methodName;
    this.className = null;
    this.inputs = new String[]{};
  }

  public MethodObject(String parentClass, String methodName) {
    this.methodName = methodName;
    this.className = parentClass;
    this.inputs = new String[]{};
  }

  public MethodObject(String parentClass, String methodName, String[] inputs) {
    this.methodName = methodName;
    this.className = parentClass;
    this.inputs = inputs;
  }


  @Override
  public String toString() {
    String result;
    result = "Method "+className+'.'+methodName+'\n';
    for (String input:inputs) {
      result += "  "+input+'\n';
    }
    return result;
  }

}
