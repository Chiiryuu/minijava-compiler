import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class CodeConverter {


  public ArrayList<String> vaporMProgram = new ArrayList<>();
  public ArrayList<String> MIPS = new ArrayList<>();
  public ArrayList<String> AdditionalLines = new ArrayList<>();
  boolean foundFirstText = false;
  boolean debug = false;

  static final String finalCode = "\n_print:\n"
      + "  li $v0 1\n"
      + "  syscall\n"
      + "  la $a0 _newline\n"
      + "  li $v0 4\n"
      + "  syscall\n"
      + "  jr $ra\n"
      + "\n"
      + "_error:\n"
      + "  li $v0 4\n"
      + "  syscall\n"
      + "  li $v0 10 \n"
      + "  syscall\n"
      + "\n"
      + "_heapAlloc:\n"
      + "  li $v0 9\n"
      + "  syscall\n"
      + "  jr $ra\n"
      + "\n"
      + ".data\n"
      + ".align 0\n"
      + "_newline: .asciiz \"\\n\"\n"
      + "_str0: .asciiz \"null pointer\\n\"";

  String currentName = "";
  int currentIn = 0;
  int currentOut = 0;
  int currentLocal = 0;

  int regionType = -1; //0 = data, 1 = text. Else = undefined

  /*

  frame[0] = $fp backup (at prologue) ( -4($fp) )
  frame[1] = $ra backup (at prologue) ( -8($fp) )
  frame[2] = local[0]                 ( -12($fp) )
  ...
  frame[n+1] = local[n-1]
  frame[n+2] = out[0]
  ...
  frame[n+m-1+2] = out[m-1] // The total size of the frame is 2+n+m

   */

  public boolean isImmediate(String val) {
    try {
      Integer.parseInt(val);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  public boolean isRegister(String val) {
    return val.startsWith("$");
  }

  public boolean isLabel(String val) {
    return val.startsWith(":");
  }

  public boolean isAddress(String val) {
    return val.contains("(");
  }

  public boolean isOutVar(String val) {
    return val.startsWith("out[");
  }

  public boolean isInVar(String val) {
    return val.startsWith("in[");
  }


  public void convertCode() {
    for (String line : vaporMProgram) {
      String lineTrimmed = line.trim();
      if (lineTrimmed.startsWith("const ")) {
        if (regionType != 0) {
          regionType = 0;
          MIPS.add(".data\n");
        }
        MIPS.add(lineTrimmed.substring(6)+":");
      }
      else if (lineTrimmed.startsWith("func ") || lineTrimmed.startsWith("Func ")) {

        if (regionType != 1) {
          regionType = 1;
          MIPS.add(".text\n");
          if (!foundFirstText) {
            foundFirstText = true;
            MIPS.add("  jal Main\n  li $v0 10\n  syscall\n");
          }
        }
          String[] fixedLine = lineTrimmed.replaceAll("[,\\[\\]\\s+]"," ").split("\\s");
          for (int i=1; i<fixedLine.length;i++) {
            if (fixedLine[i-1].equals("func") || fixedLine[i-1].equals("Func"))
              currentName = fixedLine[i];
            else if (fixedLine[i-1].equals("in"))
              currentIn = Integer.parseInt(fixedLine[i]);
            else if (fixedLine[i-1].equals("out"))
              currentOut = Integer.parseInt(fixedLine[i]);
            else if (fixedLine[i-1].equals("local"))
              currentLocal = Integer.parseInt(fixedLine[i]);
          }
          MIPS.add(currentName+':');
          int size = 2 +  currentOut + currentLocal; // In words, *4 = bytes
          MIPS.add("  sw $fp -8($sp)\n  move $fp $sp\n  subu $sp $sp "+(size * 4)+"\n  sw $ra -4($fp)");
      }
      else if (lineTrimmed.contains("=")) {
        String[] assignment = lineTrimmed.split("=");
        String lhs = assignment[0].trim();
        String rhs = assignment[1].trim();

        lhs = treatStatement(lhs);
        rhs = treatStatement(rhs);

        if (rhs.contains("HeapAllocZ(")) {
          String size = rhs.split("[()]")[1];
          MIPS.add("  li $a0 "+size+"\n  jal _heapAlloc\n  move "+lhs+" $v0");
        }

        else if (rhs.contains("LtS(")) {
          String[] vars = rhs.split("[()]");
          vars = vars[1].split(" ");
          String var1 = vars[0];
          String var2 = vars[1];
          if (isRegister(var1) && isRegister(var2)) {
            MIPS.add("  slt "+lhs+" "+var1+" "+var2);
          }
          else if (isRegister(var1) && isImmediate(var2)) {
            MIPS.add("  slti "+lhs+" "+var1+" "+var2);
          }
          else if (isImmediate(var1) && isRegister(var2)) {
            MIPS.add("  slti "+lhs+" "+var2+" "+var1);
          }
        }

        else if (rhs.contains("Add(")) {
          String[] vars = rhs.split("[()]");
          vars = vars[1].split(" ");
          String var1 = vars[0];
          String var2 = vars[1];
          if (isRegister(var1) && isRegister(var2)) {
            MIPS.add("  addu "+lhs+" "+var1+" "+var2);
          }
          else if (isRegister(var1) && isImmediate(var2)) {
            MIPS.add("  addiu "+lhs+" "+var1+" "+var2);
          }
          else if (isImmediate(var1) && isRegister(var2)) {
            MIPS.add("  addiu "+lhs+" "+var2+" "+var1);
          }
          else if (isImmediate(var1) && isImmediate(var2)) {
            MIPS.add("  li $t9 "+var2+"\n  addiu " + lhs + " $t9 " + var1);
          }
        }

        else if (rhs.contains("Sub(")) {
          String[] vars = rhs.split("[()]");
          vars = vars[1].split(" ");
          String var1 = vars[0];
          String var2 = vars[1];
          if (isImmediate(var1) && !isImmediate(var2)) {
            MIPS.add("  subu " + lhs + " " + var2 + " " + var1);
          }
          else if (isImmediate(var1) && isImmediate(var2)) {
            MIPS.add("  li $t9 "+var2+"\n  subu " + lhs + " $t9 " + var1);
          }
          else {
            MIPS.add("  subu " + lhs + " " + var1 + " " + var2);
          }
        }

        else if (rhs.contains("MulS(")) {
          String[] vars = rhs.split("[()]");
          vars = vars[1].split(" ");
          String var1 = vars[0];
          String var2 = vars[1];
          if (isImmediate(var1) && !isImmediate(var2)) {
            MIPS.add("  mul " + lhs + " " + var2 + " " + var1);
          }
          else if (isImmediate(var1) && isImmediate(var2)) {
            MIPS.add("  li $t9 "+var2+"\n  mul " + lhs + " $t9 " + var1);
          }
          else {
            MIPS.add("  mul " + lhs + " " + var1 + " " + var2);
          }
        }

        else if (isInVar(rhs)) {
          int inNum = Integer.parseInt(rhs.split("[\\[\\]]")[1]);
          MIPS.add("  lw "+lhs+" "+(inNum*4)+"($fp)");
        }

        else if (isOutVar(lhs)) {
          int outNum = Integer.parseInt(lhs.split("[\\[\\]]")[1]);
          if (isImmediate(rhs)) {
            MIPS.add("  li $t9 " + rhs + "\n  sw $t9 " + (outNum * 4) + "($sp)");
          }
          else {
            MIPS.add("  sw "+rhs+" "+ (outNum * 4) + "($sp)");
          }
        }

        else if (isRegister(lhs) && isLabel(rhs)) {
          MIPS.add("  lw "+lhs+" "+rhs.substring(1));
        }

        else if (isRegister(lhs) && isAddress(rhs)) {
          MIPS.add("  lw "+lhs+" "+rhs);
        }

        else if (isRegister(lhs) && isRegister(rhs)) {
          MIPS.add("  move "+lhs+" "+rhs);
        }

        else if (isRegister(lhs) && isImmediate(rhs)) {
          MIPS.add("  li "+lhs+" "+rhs);
        }

        else if (isAddress(lhs) && isLabel(rhs)) {
          MIPS.add("  la $t9 "+rhs.substring(1)+"\n  sw $t9 "+lhs);
        }

        else if (isAddress(lhs) && isImmediate(rhs)) {
          MIPS.add("  sw $"+rhs+" "+lhs);
        }

        else if (isAddress(lhs) && isRegister(rhs)) {
          MIPS.add("  sw "+rhs+" "+lhs);
        }

        else {
          MIPS.add("  " + lhs + " <- " + rhs);
        }
      }

      else if (lineTrimmed.equals("ret")) {
        int size = 2 +  currentOut + currentLocal; // In words, *4 = bytes
        MIPS.add("  lw $ra -4($fp)\n  lw $fp -8($fp)\n  addu $sp $sp "+(size * 4)+"\n  jr $ra");
      }

      else if (lineTrimmed.startsWith("PrintIntS(")) {
       String var = lineTrimmed.split("[()]")[1];
       if (isImmediate(var))
          MIPS.add("  li $a0 "+var+"\n  jal _print");
       else
         MIPS.add("  move $a0 "+var+"\n  jal _print");
      }

      else if (lineTrimmed.startsWith("call ")) {
        String var = lineTrimmed.substring(5);
        if (isLabel(var)) {
          MIPS.add("  jal "+var.substring(1));
        }
        else {
          MIPS.add("  jalr "+var);
        }
      }
      else if (lineTrimmed.matches("if0 .*goto :.*")) {
        String[] components = lineTrimmed.substring(3).split(" goto ");
        MIPS.add("  beqz "+components[0]+" "+components[1].substring(1));
      }
      else if (lineTrimmed.matches("if .*goto :.*")) {
          String[] components = lineTrimmed.substring(3).split(" goto ");
          MIPS.add("  bnez "+components[0]+" "+components[1].substring(1));
      }
      else if (lineTrimmed.matches("goto :.*")) {
        String point = lineTrimmed.substring(6);
        MIPS.add("  j "+point);
      }
      else if (lineTrimmed.contains("Error(\"")) {
        MIPS.add("  la $a0 _str0\n  j _error");
      }
      else {
        if (lineTrimmed.startsWith(":")) {
          line = "  "+lineTrimmed.substring(1);
        }
        MIPS.add(line);
      }
    }
  }

  public String treatStatement(String statement) {
    if (statement.contains("local[")) {
      String[] split = statement.split("[\\[\\]]");
      int i = Integer.parseInt(split[1]);
      return ((i*4))+"($sp)";
    }
    /*
    else if (statement.contains("out[")) {
      String[] split = statement.split("[\\[\\]]");
      int i = Integer.parseInt(split[1]);
      return (-12-((currentLocal+i)*4))+"($fp)";
    }

     */
    else if (statement.matches("^\\[.*\\]$")) {
      if (statement.contains("+")) {
        String[] splitVal = statement.split("\\+");
        return (splitVal[1]+"("+splitVal[0]+")").replaceAll("[\\[\\]]","");
      }
      else {
        return statement.replaceAll("\\[", "0(").replaceAll("\\]", ")");
      }
    }

    else {
      return statement;
    }
  }

  public void printMIPS() {
    for (String line:MIPS) {
      System.out.println(line);
    }
    for (String line:AdditionalLines) {
      System.out.println(line);
    }
    System.out.println(finalCode);
  }


}