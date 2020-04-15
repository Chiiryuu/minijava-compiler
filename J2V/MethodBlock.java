import java.util.ArrayList;

public class MethodBlock {
  public String name;
  public String args="";
  public ArrayList<String> statements;

  private int tempVars = 0;

  public MethodBlock(String name) {
    this.name = name;
    this.statements = new ArrayList<>();
  }

  public MethodBlock(String name, String args) {
    this.name = name;
    this.args = args;
    this.statements = new ArrayList<>();
  }

  public String getLastStatement() {
    if (statements.size() == 0)
      return null;
    return statements.get(statements.size()-1);
  }

  private String getTempVar() {
      return name+"." + tempVars++;
  }

  public void addStatement(String statement) {
    statements.add(statement);
  }

  @Override
  public String toString() {
    String result="func "+name+"("+args+")\n";
    for (int i=0; i<statements.size();i++) {

        String statement = statements.get(i);
        if (statement.matches(";ClassVar;\\d*;\\s=\\s*;ClassVar;.*")) {
          String[] stats = statement.split(";");
          String var = getTempVar();
          result += "  "+var+" = [this+"+stats[5]+"]\n  [this+"+stats[2]+"] = "+var+'\n';
        }
        else if (statement.matches(";ClassVar;\\d*;\\s*=\\s*.*")) {
          String[] stats = statement.split(";");
          String var = getTempVar();
          //int num = Integer.parseInt(stats[2]) * 4 + 4;
          //result += "  " + var+" = Add(this "+stats[2]+")\n  ["+var+"]"+stats[3]+'\n';
          //result += "  [this+"+stats[2]+"]"+stats[3]+'\n';
          result += "  " + var+stats[3]+"\n  [this+"+stats[2]+"] = "+var+'\n';
        }
        else if (statement.contains(";ClassVar;")) {
          if (statement.split(";ClassVar;").length > 2) {
            String[] stats = statement.split(";");
            String var1 = getTempVar();
            String var2 = getTempVar();
            String extra = "";
            if (stats.length > 6)
              extra = stats[6];
            result +=  "  " + var1 + " = [this+" + stats[2] + "]\n  " + "  " + var2 + " = [this+" + stats[5] + "]\n  "+ stats[0] + var1+ " " + var2 + extra + '\n';
          }
          else {
            //System.out.println("Before: "+statement);
            String[] stats = statement.split(";");
            String var = getTempVar();
            String extra = "";
            if (stats.length > 3)
              extra = stats[3];
            result += "  " + var + " = [this+" + stats[2] + "]\n  " + stats[0] + var + extra + '\n';
            //System.out.println("After: "+"  " + var + " = [this+" + stats[2] + "]\n  " + stats[0] + var + extra + '\n');
          }

        }
        else {
          result += "  " + statement + '\n';
        }


      if (i+1 == statements.size()) {
        if (!statement.matches("ret .*")) {
          result += "  ret\n";
        }
      }

    }
    return result;
  }

}
