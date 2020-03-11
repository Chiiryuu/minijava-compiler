import java.util.HashMap;
import java.util.TreeMap;

public class SymbolTable {
  private String mainClass=null;
  public HashMap<String, ClassObject> classes;
  public HashMap<String, MethodObject> methods;
  public HashMap<String, MethodBlock> blocks;
  public String currentClass = "";
  public String currentBlock = "";
  public String blockIndentationLevel = "";
  public String lastTemp = "";
  public String classConsts = "";

  public String getLastExpression() {
    return blocks.get(currentBlock).getLastStatement();
  }

  public void addStatement(String miniBlock) {
    for (String line: miniBlock.split("\n")) {
      if (line.trim().length()>0)
        blocks.get(currentBlock).addStatement(blockIndentationLevel+line);
    }
  }

  public int getMethodOffset(String className, String method) {
    String sig = className+"."+method;
    ClassObject object = classes.get(className);
    for (int i=0; i<object.classMethods.size();i++) {
      if (sig.equals(object.classMethods.get(i))) {
        return 4*i;
      }
    }
    return 0;
  }

  public void makeClassConsts() {
    classConsts = "";
    for (ClassObject object : classes.values()) {
      if (!object.isMain) {
        String classConst = "const vmt_" + object.className+"\n";
        for (String method:object.classMethods) {
          classConst+="  :"+method+"\n";
        }
        classConsts+=classConst+"\n";
      }
    }
  }

  public String popLastStatement() {
    String result = blocks.get(currentBlock).getLastStatement();
    blocks.get(currentBlock).statements.remove(result);
    return result;
  }

  public void setCurrentClass(String className) {
    this.currentClass = className;
  }

  public void setCurrentBlock(String name) {
    this.currentBlock = name;
  }

  public void setBlockArgs(String args) {
    blocks.get(currentBlock).args = args;
  }

  public void setCurrentBlock(String name, String args) {
    this.currentBlock = name;
  }

  public void addMainClass(String name, ClassObject object) {
    object.setMain();
    classes.put(name,object);
  }

  public void addClass(String name, ClassObject object) {
    classes.put(name,object);
  }

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String classIn) {
    this.mainClass = classIn;
  }

  public boolean classIsMain(String classIn) {
    return classIn.equals(mainClass);
  }

  public void addVar(String var) {
    ClassObject object = classes.get(currentClass);
    object.addVariable(var);
  }

  public void addMethod(MethodObject method) {
    String sig = method.className+'.'+method.methodName;
    methods.put(sig, method);
    ClassObject object = classes.get(currentClass);
    object.addMethod(sig);
    blocks.put(sig,new MethodBlock(sig));
  }

  public void calculateClassSizes() {
    for (ClassObject object : classes.values()) {
      object.classSize = (object.classVariables.size()*4)+4;
    }
  }

  public SymbolTable() {
    this.classes = new HashMap<>();
    this.methods = new HashMap<>();
    this.blocks = new HashMap<>();
    blocks.put("Main", new MethodBlock("Main"));
  }

  public void printSorted() {
    TreeMap<String, ClassObject> treeTable = new TreeMap<>();
    treeTable.putAll(classes);
    treeTable.forEach((k,v) -> System.out.println(v));
  }

  public void printRaw() {
    classes.forEach((k,v) -> System.out.println(v));
  }


  @Override
  public String toString() {
    String result = "Classes:\n";
    for (ClassObject object : classes.values()) {
      result += object.toString() + '\n';
    }
    result += "Methods:\n";
    for (MethodObject object : methods.values()) {
      result += object.toString() + '\n';
    }
    return result;
  }
}
