import static java.lang.System.exit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;


public class V2VM {

  public static VaporProgram parseVapor(StringReader in, PrintStream err)
      throws IOException
  {
    Op[] ops = {
        Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
        Op.PrintIntS, Op.HeapAllocZ, Op.Error,
    };
    boolean allowLocals = true;
    //$s0..$s7, $t0..$t8, $a0..$a3, $v0, $v1.
/*
  Strings only in error statements
  GOTO can only lead to code labels

   The register $t9 is reserved for the next assignment.

    $s0..$s7: general use callee-saved
    $t0..$t8: general use caller-saved
    $a0..$a3: reserved for argument passing
    $v0: returning a result from a call
    $v0, $v1: can also be used as temporary registers for loading values from the stack

 */

    String[] registers = new String[]{"$s0","$s1","$s2","$s3","$s4","$s5","$s6","$s7","$t0","$t1","$t2","$t3","$t4","$t5","$t6","$t7","$t8","$a0","$a1","$a2","$a3","$v0","$v1"};
    boolean allowStack = false;

    VaporProgram program;
    try {
      program = VaporParser.run(in, 1, 1,
          java.util.Arrays.asList(ops),
          allowLocals, registers, allowStack);
    }
    catch (ProblemException ex) {
      err.println(ex.getMessage());
      return null;
    }

    return program;
  }

  public static void V2VM() throws IOException {

    Scanner scanner = new Scanner(System.in);
    ArrayList<String> inputLines = new ArrayList<>();
    String inputRaw = "";
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      line = line.replaceAll("\\s"," ");
      inputRaw+=line+'\n';
      inputLines.add(line);
    }
    scanner.close();
    /*
    inputRaw = inputRaw.replaceAll("\n","newlnchar");
    inputRaw = inputRaw.replaceAll("\\s"," ");
    inputRaw = inputRaw.replaceAll("newlnchar", "\n");*/

    /*
    for (String line:inputLines) {
      System.out.println(line);
    }*/

    StringReader programReader = new StringReader(inputRaw);
    inputRaw=null;
    VaporProgram program = parseVapor(programReader, System.err);
    VaporVisitor visitor = new VaporVisitor();
    //visitor.debug = true;
    SymbolTable table = new SymbolTable();
    table.vaporProgram=inputLines;
    String programString = visitor.visit(program, table);

    table.convertCode();

    //System.out.println("# Vars: "+table.vars.size());
    //table.printVarArr();

    for (CodeBlock block : table.codeBlocks) {
      System.out.println(block);
      System.out.println();
    }

    //System.out.println("Raw Program:");
    //System.out.println(programString);
  }

  //$a0 - $a3: input/output arguments
  //in[] : overflow input arguments
  //out[] : overflow output arguments
  //$v0: return value register

  public static void main(String[] args) throws IOException {


    V2VM();

  }
}


