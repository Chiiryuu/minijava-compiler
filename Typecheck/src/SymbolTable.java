import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class SymbolTable {
  public String scope;
  private String mainClass=null;
  public HashMap<String, TokenEntry> table;

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String classIn) {
    this.mainClass = classIn;
  }

  public boolean classIsMain(String classIn) {
    return classIn.equals(mainClass);
  }

  public SymbolTable() {
    this.scope ="";
    this.table = new HashMap<>();
  }

  public SymbolTable(String scope) {
    this.scope =scope;
    this.table = new HashMap<>();
  }

  public void printSorted() {
    TreeMap<String, TokenEntry> treeTable = new TreeMap<>();
    treeTable.putAll(table);
    treeTable.forEach((k,v) -> System.out.println(v));
  }

  public void printRaw() {
    table.forEach((k,v) -> System.out.println(v));
  }

  public void relaxScope() {
    String[] scopes = scope.split("::");
    scope = "";
    if (scopes.length > 1) {
      for(int i = 0; i<scopes.length-1;i++) {
        if (i!=0)
          scope = scope + "::";
        scope = scope + scopes[i];
      }
    }

  }

  public String getRelaxedScope(String scopeIn) {
    String[] scopes = scopeIn.split("::");
    String scope = "";
    if (scopes.length > 1) {
      for(int i = 0; i<scopes.length-1;i++) {
        if (i!=0)
          scope = scope + "::";
        scope = scope + scopes[i];
      }
    }
    return scope;
  }

  public String getCurrentClass() {
    String[] scopes = scope.split("::");
    if (scopes.length > 1) {
      return scopes[0];
    }
    return null;

  }

  public TokenEntry getValidMethodToken(String className, String method) {
    if (!table.containsKey(className))
      return null;
    TokenEntry methodToken = getBestSymbol(method, className);
    if (methodToken.type.equals("Method"))
      return methodToken;
    return null;
  }

  public TokenEntry getBestSymbol(String name) {
    return getBestSymbol(name, scope);
  }

  public TokenEntry getBestSymbol(String name, String scope) {
    String tempScope = new String(scope);
    //System.out.println("Starting Pos: "+scope);
    while (tempScope.length() > 0) {
      String varPath = tempScope+"::"+name;
      //System.out.println(varPath+", "+name);
      if (table.containsKey(varPath))
        return table.get(varPath);
      tempScope = getRelaxedScope(tempScope);
    }
    if (table.containsKey(name))
      return table.get(name);
    String className = scope.split("::")[0];
    TokenEntry currentClass = table.get(className);
    while (currentClass != null) {
      String newScope = currentClass.symbol+"::"+name;
      if (table.containsKey(newScope))
        return table.get(newScope);
      if (currentClass.inherit == null)
        currentClass = null;
      else
        currentClass = table.get(currentClass.inherit);
    }
    return null;

  }

  public Boolean classExtends(String class1, String class2) {
    if (!table.containsKey(class1) || !table.containsKey(class2))
      return false;
    TokenEntry classToken = table.get(class1);
    while (classToken.inherit != null) {
      if (classToken.inherit.equals(class2))
        return true;
      classToken = table.get(classToken.inherit);
    }
    return false;
  }

  public String getRelaxedScope() {
    String[] scopes = scope.split("::");
    String scope = "";
    if (scopes.length > 1) {
      for(int i = 0; i<scopes.length-1;i++) {
        if (i!=0)
          scope = scope + "::";
        scope = scope + scopes[i];
      }
    }
    return scope;

  }

  public ArrayList<TokenEntry> getClasses() {
    ArrayList<TokenEntry> classes = new ArrayList<>();
    table.forEach((k,v) -> {
      if (!k.contains("::")) {
        classes.add(v);
      }
    });
    return classes;
  }

  public boolean hasValidInheritance() {
    ArrayList<TokenEntry> classes = getClasses();
    ArrayList<String> exploredClasses = new ArrayList<>(classes.size());
    for (int i=0; i<classes.size();i++) {
      TokenEntry classToken = classes.get(i);
      exploredClasses.clear();
      while (classToken.inherit != null) {
        exploredClasses.add(classToken.symbol);
        String parentClass = classToken.inherit;
        //System.out.println(parentClass+" ==? "+mainClass);
        if (classIsMain(parentClass))
          return false;
        //System.out.println(exploredClasses);
        //System.out.println(parentClass);
        if (!table.containsKey(parentClass))
          return false;
        for (String visitedClass:exploredClasses) {
          if (visitedClass.equals(parentClass))
            return false;
        }
        classToken = table.get(parentClass);
      }
    }

    return true;
  }

  public void constrictScope(String newScope) {
    if (scope.length() == 0)
      scope = newScope;
    else
      scope = scope+"::"+newScope;
  }


  @Override
  public String toString() {
    String result;
    result = "<"+ scope +">";
    return result;
  }

}
