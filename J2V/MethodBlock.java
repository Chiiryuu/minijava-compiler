import java.util.ArrayList;

public class MethodBlock {
  public String name;
  public ArrayList<String> statements;

  public MethodBlock(String name) {
    this.name = name;
    this.statements = new ArrayList<>();
  }

  public String getLastStatement() {
    if (statements.size() == 0)
      return null;
    return statements.get(statements.size()-1);
  }

  public void addStatement(String statement) {
    statements.add(statement);
  }

  @Override
  public String toString() {
    String result="func "+name+"()\n";
    for (int i=0; i<statements.size();i++) {
      if (i+1 == statements.size()) {
        String last = "  "+statements.get(i);
        if (last.matches("  ret .*")) {
          result += last;
        }
        else {
          result += last+"\n  ret";
        }
      }
      else
      result += "  " + statements.get(i) + '\n'; {

      }
    }
    return result;
  }

}
