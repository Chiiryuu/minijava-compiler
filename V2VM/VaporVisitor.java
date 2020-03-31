import cs132.vapor.ast.*;
import java.util.ArrayList;
import java.util.Arrays;

/*


 */

public class VaporVisitor {
  public boolean debug=false;

  public String writeBlock(String title, ArrayList<String> block) {
    String result = title+'\n';
    for (String element:block) {
      result+="  "+element+'\n';
    }
    return result;
  }

  public void print(Object o) {
    if (debug)
      System.out.print(o);
  }

  public void println(Object o) {
    if (debug)
      System.out.println(o);
  }

  public String visit(VaporProgram n, SymbolTable table) {
    String _ret=null;
    println("Program");
    table.registers = n.registers;
    table.registerAssignments = new ArrayList<>(n.registers.length);
    print("Registers: [ ");
    for (String reg: table.registers) {
     print(reg+" ");
    }
    print("]\n");



    VDataSegment[] dataSegs = n.dataSegments;
    ArrayList<String> dataSegVals = new ArrayList<>();
    for (VDataSegment data: dataSegs) {
      String seg = visit(data, table);
      dataSegVals.add(seg);
    }

    VFunction[] functions = n.functions;
    ArrayList<String> funcVals = new ArrayList<>();
    for (VFunction func: functions) {
      String seg = visit(func, table);
      funcVals.add(seg);
    }

    _ret = "";
    for (String s:dataSegVals) {
      _ret += s+ '\n';
    }

    _ret += '\n';
    for (String s:funcVals) {
      _ret += s+ '\n';
    }

    return _ret;
  }

  public String visit(VDataSegment n, SymbolTable table) {
    String _ret=null;
    String title = n.ident;
    CodeBlock data = new CodeBlock(title,"data", table);
    VOperand.Static[] elements = n.values;
    println("Data Segment: Line: "+n.sourcePos.line+", #vals: "+elements.length);
    println("\tCorresponding line: "+table.vaporProgram.get(n.sourcePos.line));
    ArrayList<String> ops = new ArrayList<>();
    for (VOperand.Static op: elements) {
      String opVal = visit(op, table);
      ops.add(opVal);
      data.lines.add('\t'+opVal);
    }
    table.codeBlocks.add(data);
    _ret = writeBlock(title, ops);

    return _ret;
  }

  public String visit(VOperand.Static n, SymbolTable table) {
    String _ret=n.toString();
    println("OpStatic: "+n);
    return _ret;
  }

  public String visit(VFunction n, SymbolTable table) {
    String _ret=n.toString();
    String title = n.ident;
    println("Function: "+title);


    String[] localVars = n.vars;
    print("  LocalVars: [ ");
    for (String var: localVars) {
      print(var+" ");
    }
    print("]\n");

    CodeBlock function = new CodeBlock(title, "function", table);

    function.params = new ArrayList<>();
    for (VVarRef.Local var: n.params) {
      String seg = visit(var, table);
      function.params.add(seg);
    }

    Arrays.sort(localVars, (a, b) -> b.length() - a.length());

    function.localVars = new ArrayList<>(Arrays.asList(localVars));

    int currentLineIndex = n.sourcePos.line;
    String currentLine = table.vaporProgram.get(currentLineIndex);
    while (!currentLine.strip().startsWith("ret ") && !currentLine.strip().equals("ret")) {
      //System.out.println("'"+currentLine.strip()+"'");
      function.lines.add(currentLine);
      currentLineIndex++;
      currentLine = table.vaporProgram.get(currentLineIndex);
    }
    function.lines.add(currentLine);



    ArrayList<String> labelList= new ArrayList<>();
    for (VCodeLabel var: n.labels) {
      String seg = visit(var, table);
      labelList.add(seg);
    }

    function.labels=labelList;

    table.codeBlocks.add(function);


    ArrayList<String> instructionList= new ArrayList<>();
    for (VInstr var: n.body) {
      String seg = visit(var, table);
      labelList.add(seg);
    }

    return _ret;
  }


  public String visit(VVarRef.Local n, SymbolTable table) {
    String _ret=n.ident;
    println("Var "+n.ident+", id: "+n.index);
    return _ret;
  }

  public String visit(VCodeLabel n, SymbolTable table) {
    String _ret=n.ident;
    println("Label "+n.ident+", ind: "+n.instrIndex);
    return _ret;
  }

  public String visit(VInstr n, SymbolTable table) {
    InstrVisitor vis = new InstrVisitor();
    vis.debug=debug;
    String _ret="";
    try {
      _ret=n.accept(vis);
    } catch (Exception e) {
      e.printStackTrace();
    }
    println("Instr at "+n.sourcePos);
    return _ret;
  }


}
