import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class SymbolTable {

  private String mainClass = null;
  public HashMap<String, String> classVars;
  public String currentClass = "";
  public String currentBlock = "";

  public String[] registers = null;
  public ArrayList<String> registerAssignments = null;

  /*  0-7:    $s
      8-16:   $t
      17-20:  $a
      21-22:  $v

  "$s0","$s1","$s2","$s3","$s4","$s5","$s6","$s7",
  "$t0","$t1","$t2","$t3","$t4","$t5","$t6","$t7","$t8",
  "$a0","$a1","$a2","$a3",
  "$v0","$v1"

  $s0..$s7: general use callee-saved
  $t0..$t8: general use caller-saved
  $a0..$a3: reserved for argument passing
  $v0: returning a result from a call
  $v0, $v1: can also be used as temporary registers for loading values from the stack

  */
  public ArrayList<String> vaporProgram = new ArrayList<>();
  public ArrayList<CodeBlock> codeBlocks = new ArrayList<>();
  public ArrayList<String> vars = new ArrayList<>();
  public String classConsts = "";
  public boolean inMethod = false;
  public boolean foundAbstractAllocation = false;

  public void setCurrentClass(String className) {
    this.currentClass = className;
  }

  public void setCurrentBlock(String name) {
    this.currentBlock = name;
  }

  public void setCurrentBlock(String name, String args) {
    this.currentBlock = name;
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

  public void convertCode() {
    CodeBlock main=null;
    for (CodeBlock block : codeBlocks) {
      if (block.type.equals("function")) {
        if (block.name.equals("Main"))
          main=block;
        block.varStart = vars.size();
        String title = block.name;
        for (String var : block.localVars) {
          vars.add(title+"."+var);
        }
        block.calculateIn();
        block.calculateOut();


        /*
        System.out.print("Vars: [ ");
        for (String var:block.localVars) {
          System.out.print(var+" ");
        }
        System.out.println("]");

         */
      }
    }


    for (CodeBlock block : codeBlocks) {
      if (block.type.equals("function")) {
        block.fixVars();
      }
    }

  }

  public void printVarArr() {
    String result = "[ ";
    for (String var:vars) {
      result+=var+" ";
    }
    result+="]";
    System.out.println(result);
  }


}