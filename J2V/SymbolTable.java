import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class SymbolTable {
  private String mainClass=null;
  public HashMap<String, ClassObject> table;
  public HashMap<String, MethodObject> methods;
  public HashMap<String, MethodBlock> blocks;
  public String currentClass = "";
  public String currentBlock = "";
  public String blockIndentationLevel = "";
  public String lastTemp = "";

  public String getLastExpression() {
    return blocks.get(currentBlock).getLastStatement();
  }

  public void addStatement(String miniBlock) {
    for (String line: miniBlock.split("\n")) {
      blocks.get(currentBlock).addStatement(blockIndentationLevel+line);
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

  public void addMainClass(String name, ClassObject object) {
    object.setMain();
    table.put(name,object);
  }

  public void addClass(String name, ClassObject object) {
    table.put(name,object);
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
    ClassObject object = table.get(currentClass);
    object.addVariable(var);
  }

  public void addMethod(MethodObject method) {
    String sig = method.className+'.'+method.methodName;
    methods.put(sig, method);
    ClassObject object = table.get(currentClass);
    object.addMethod(sig);
    blocks.put(sig,new MethodBlock(sig));
  }

  public void calculateClassSizes() {
    for (ClassObject object : table.values()) {
      object.classSize = object.classVariables.size() + object.classMethods.size();
    }
  }

  public SymbolTable() {
    this.table = new HashMap<>();
    this.methods = new HashMap<>();
    this.blocks = new HashMap<>();
    blocks.put("Main", new MethodBlock("Main"));
  }

  public void printSorted() {
    TreeMap<String, ClassObject> treeTable = new TreeMap<>();
    treeTable.putAll(table);
    treeTable.forEach((k,v) -> System.out.println(v));
  }

  public void printRaw() {
    table.forEach((k,v) -> System.out.println(v));
  }


  @Override
  public String toString() {
    String result = "Classes:\n";
    for (ClassObject object : table.values()) {
      result += object.toString() + '\n';
    }
    result += "Methods:\n";
    for (MethodObject object : methods.values()) {
      result += object.toString() + '\n';
    }
    return result;
  }
}
