import cs132.vapor.ast.*;

public class InstrVisitor extends VInstr.VisitorR<String, Exception> {

  public boolean debug=false;

  public void print(Object o) {
    if (debug)
      System.out.print(o);
  }

  public void println(Object o) {
    if (debug) {
      System.out.print('\t');
      System.out.println(o);
    }
  }

  @Override
  public String visit(VAssign vAssign) throws Exception {
    println("VAssign: "+vAssign.source+" -> "+vAssign.dest);
    return "";
  }

  @Override
  public String visit(VCall vCall) throws Exception {
    println("VCall: "+vCall.dest.ident+" -> "+vCall.args);
    return "";
  }

  @Override
  public String visit(VBuiltIn vBuiltIn) throws Exception {
    String args = "";
    for (VOperand op:vBuiltIn.args) {
      args += op.toString()+" ";
    }
    println("VBuiltIn: "+vBuiltIn.op.name+" -> "+vBuiltIn.dest+" -> [ "+vBuiltIn.args+" ]");
    return "";
  }

  @Override
  public String visit(VMemWrite vMemWrite) throws Exception {
    println("VMemWrite: "+vMemWrite.source+" -> "+vMemWrite.dest);
    return "";
  }

  @Override
  public String visit(VMemRead vMemRead) throws Exception {
    println("VMemRead: "+vMemRead.source+" -> "+vMemRead.dest);
    return "";
  }

  @Override
  public String visit(VBranch vBranch) throws Exception {
    println("VBranch: "+vBranch.positive+" -> "+vBranch.target.ident+" -> "+vBranch.value);
    return "";
  }

  @Override
  public String visit(VGoto vGoto) throws Exception {
    println("VGoto: "+vGoto.target);
    return "";
  }

  @Override
  public String visit(VReturn vReturn) throws Exception {
    println("VReturn: "+vReturn.value);
    return "";
  }
}