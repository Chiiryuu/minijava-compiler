import java.util.ArrayList;

public class TokenEntry {
  public String scope;
  public String symbol;
  public String type;
  int length;
  public String returnType;
  public String inherit;
  public String[] inputs;


  public TokenEntry(String scope, String symbol, String type) {
    this.scope=scope;
    this.symbol=symbol;
    this.type=type;
  }


  @Override
  public String toString() {
    String result;
    if (type.equals("Method")) {

      String inputList = String.join(", ",inputs);
      result = "<"+scope+", "+symbol+", "+type+" (returns "+returnType+", accepts "+inputList+")>";
    }
    else if (type.equals("Class")) {
      result = "<"+scope+", "+symbol+", "+type+" (inherits "+inherit+")>";
    }
    else
      result = "<"+scope+", "+symbol+", "+type+">";
    return result;
  }

  public boolean isType(String tokenType) {
    if (this.type.equals(tokenType))
      return true;
    return false;
  }

}
