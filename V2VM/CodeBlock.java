import java.util.ArrayList;

public class CodeBlock {
  public SymbolTable table;
  public String name;
  public String type;
  public int in=0;
  public int out=0;
  public int local=0;
  public int varStart=0;
  public boolean recursive=false;
  public ArrayList<String> locals;
  public ArrayList<String> lines;
  public ArrayList<String> localVars;
  public ArrayList<String> params;
  public ArrayList<String> labels;

  public CodeBlock(String name, String type, SymbolTable table) {
    this.name = name;
    this.type = type;
    this.table = table;
    this.lines = new ArrayList<>();
  }

  public CodeBlock(String name, String type, SymbolTable table, ArrayList<String> lines) {
    this.name = name;
    this.type = type;
    this.table = table;
    this.lines = lines;
  }

  public void calculateIn() {
    this.in = Math.max(params.size() - 4,0);
  }

  public void calculateOut() {
    ArrayList<String> thisVars = new ArrayList<>();
    ArrayList<String> thisAssigned = new ArrayList<>();
    for (String line:lines) {
      if (line.contains("= [this]")) {
        String var = line.split("=")[0].trim();
        //System.out.println(var);
        thisAssigned.add(var);
      }
      if (line.contains("=")) {
        String var = line.split("=")[0].trim();
        //System.out.println(var);
        if (!var.matches("t\\.\\d+") && !var.matches("\\[t\\.\\d+\\]") && !thisVars.contains(var) ) {
          thisVars.add(var);
          local++;
        }
      }
      if (line.contains(" call ")) {
        if (line.contains(":"+this.name))
          this.recursive = true;
        else {
        for (String var: thisAssigned) {
          if (line.contains("call " + var + "(")) {
            this.recursive = true;
          }
        }
        }

        int start = line.indexOf('(');
        int end = line.indexOf(')');
        String args = line.substring(start+1,end);
        String[] argSplit = args.split(" ");
        int newVal = argSplit.length - 4;
        if (newVal > out)
          out = newVal;
      }
    }
    if (!this.recursive)
      local = 0;
    else {
      for (String var : params) {
        if (!thisVars.contains(var)) {
          thisVars.add(var);
          local++;
        }
      }
    }
    locals = thisVars;
  }

  public int getVarIndex(String var) {
    return (localVars.indexOf(var) + varStart) * 4;
  }

  public void trimLabels() {
    for (int i=labels.size()-1;i>-1;i--) {
      if (labels.get(i).length() == 0)
        labels.remove(i);
    }
  }

  public void addInputs() {
    ArrayList<String> preOps = new ArrayList<>();
    for (int i = 0; i < params.size();i++) {
      int index = getVarIndex(params.get(i));
      if (i<4) {
        //"  "+table.registers[17+argNum]
        //[" + table.registers[8] + "+" + (4 * varIndex) + "]"
        preOps.add("  [" + table.registers[8] + "+" + (index) + "] = "+table.registers[17+i]);
      }
      else { //table.registers[9 + 4]
        preOps.add("  "+table.registers[9 + 1]+" = in["+(i-4)+"]");
        preOps.add("  [" + table.registers[8] + "+" + (index) + "] = "+table.registers[9 + 1]);
      }
    }
    for (int i = 0; i < preOps.size(); i++) {
      lines.add(i,preOps.get(i));
    }
  }

  public void hideLabels() {
    trimLabels();
    ArrayList<String> cleanedBlock = new ArrayList<>(lines.size());
    for (String lineRaw: lines) {
      String line = new String(lineRaw);
      int i=0;
      for (String label: labels) {
        if (line.contains(":"+label) || line.contains(label+":"))
          line = line.replaceAll(label,"{"+i+"}");
              i++;
      }
      cleanedBlock.add(line);
    }
    lines = cleanedBlock;
  }

  public void revealLabels() {
    ArrayList<String> cleanedBlock = new ArrayList<>(lines.size());
    for (String lineRaw: lines) {
      String line = new String(lineRaw);
      int i=0;
      if (line.matches(".*\\{\\d+\\}.*")) {
        String val = line.split("\\{")[1].split("\\}")[0];
        int num = Integer.parseInt(val);
        line = line.replaceAll("\\{"+val+"\\}", labels.get(num));
      }
      cleanedBlock.add(line);
    }
    lines = cleanedBlock;
  }

  public void addRecursion() {
    if (this.local > 0) {
      for (int i = 0; i < local; i++) {
        //System.out.println("Local: "+locals.get(i));
        int id = getVarIndex(locals.get(i));
        lines.add(0, "  "+table.registers[9]+" = [" + table.registers[8] + "+" + id+ "]");
        lines.add(1, "  local[" + i + "] = "+table.registers[9]);

        lines.add(lines.size()-1,"  "+table.registers[9]+" = local[" + i + "]");
        lines.add(lines.size()-1,"  [" + table.registers[8] + "+" + id+ "] = "+table.registers[9]);
      }
    }
  }

  public void fixVars() {
    hideLabels();
    //System.out.println("Label len: "+labels.size());
    for (String label: labels) {
      if (label.length() > 0)
        ;//System.out.println("Label '"+label+"'");
    }

    ArrayList<String> cleanedBlock = new ArrayList<>(lines.size());
    boolean done = false;
    for (String lineRaw: lines) {
      if (done)
        continue;
      if (lineRaw.endsWith(":")) {
        cleanedBlock.add(lineRaw);
        continue;
      }


      String line = new String(lineRaw);
      int assignNum = -1;
      int assignReg = -1;
      int regNum = 1;

      if (line.contains(" ret ")) {
        line = line.trim();
        String[] words = line.split(" ");
        String var = words[1];
        if (localVars.contains(var)) {
          cleanedBlock.add("  " + table.registers[21] + " = [" + table.registers[8] + "+" + getVarIndex(var) + "]");
        }
        else {
          cleanedBlock.add("  " + table.registers[21] + " = "+var);
        }
        cleanedBlock.add("  ret");
        done = true;
      }

      else if (line.contains(" call ")) {
        int start = line.indexOf('(');
        int end = line.indexOf(')');
        String[] args = line.substring(start + 1, end).split(" ");
        String assign = line.substring(0, start).trim();

        String result = "argList: [ ";
        for (String var:args) {
          result+=var+" ";
        }
        result+="]";
        //System.out.println(result);
        //System.out.println(assign);


        int argNum = 0;
        for (String arg : args) {
          if (argNum < 4) {
            if (localVars.contains(arg)) {
              cleanedBlock.add("  "+table.registers[17+argNum]+" = [" + table.registers[8] + "+" + getVarIndex(arg) + "]");
            }
            else {
              cleanedBlock.add("  "+table.registers[17+argNum]+" = "+arg);
            }
          }
          else {
            if (localVars.contains(arg)) { //table.registers[9 + 0]
              cleanedBlock.add("  "+table.registers[9 + 4]+" = [" + table.registers[8] + "+" + getVarIndex(arg) + "]");
              cleanedBlock.add("  out["+(argNum-4)+"] = "+table.registers[9 + 4]);


              //cleanedBlock.add("  out["+(argNum-4)+"] = [" + table.registers[8] + "+" + getVarIndex(arg) + "]");
            }
            else {
              cleanedBlock.add("  out["+(argNum-4)+"] = "+arg);
            }
          }
          argNum++;
        }

        String[] callers = assign.split(" ");
        String result2 = "callers: [ ";
        for (String var:callers) {
          result+=var+" ";
        }
        //System.out.println(result2);

        if (callers.length == 4) {
          String assigned = callers[0];
          String function = callers[3];
          if (function.startsWith(":")) {
            cleanedBlock.add("  call " + function);
          }
          else {
            cleanedBlock.add("  " + table.registers[9 + 0] + " = [" + table.registers[8] + "+" + getVarIndex(function) + "]");
            cleanedBlock.add("  call " + table.registers[9 + 0]);
          }
          cleanedBlock.add("  [" +table.registers[8] + "+" + getVarIndex(assigned)+"] = "+table.registers[21]);
        }
        else {
          String function = callers[1];
          if (function.startsWith(":")) {
            cleanedBlock.add("  call " + function);
          }
          else {
            cleanedBlock.add("  " + table.registers[9 + 0] + " = [" + table.registers[8] + "+" + getVarIndex(function) + "]");
            cleanedBlock.add("  call " + table.registers[9 + 0]);
          }
        }


      }

      else {
        for (String var : localVars) {
          String lineExt = " "+line+" ";
          if (line.contains(var) && lineExt.matches(".*\\W"+var+"\\W.*")) {
            int varIndex = localVars.indexOf(var);
            //System.out.println(line+" -> "+var);
            cleanedBlock.add("  " + table.registers[9 + regNum] + " = [" + table.registers[8] + "+" + getVarIndex(var) + "]");
            if (line.contains(var+" =")) {
              assignNum = varIndex;
              assignReg = regNum;
            }
            line = line.replaceAll(var, "\\" + table.registers[9 + regNum]);
            regNum++;
          }

        }
        cleanedBlock.add(line);
        if (assignNum > -1 && assignReg > -1) {
          cleanedBlock.add(
              "  [" + table.registers[8] + "+" + getVarIndex(localVars.get(assignNum)) + "]" + " = " + table.registers[9
                  + assignReg]);
        }
      }
    }

    this.lines = cleanedBlock;
    addInputs();
    addRecursion();
    revealLabels();

  }

  @Override
  public String toString() {
      String result = "";
      /*
      if (type.equals("function")) {
        String vars = String.join(" ",params);
        result = "func "+name+"("+vars+")\n";
      }*/
    if (type.equals("function")) {
      result = "func "+name+" [in "+in+", out "+out+", local "+local+"]\n";
      if (name.equals("Main")) {
        lines.add(0, "  "+table.registers[8]+" = HeapAllocZ("+4*table.vars.size()+")");
      }
    }
      else if (type.equals("data")) {
        result = "const "+name+'\n';
      }
      else {
        result = "Unknown Type\n";
      }
      for (String line: lines) {
        result+=line+'\n';
      }
      return result;
  }

}
