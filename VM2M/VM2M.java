import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.IOException;
import java.io.PrintStream;


public class VM2M {

  public static void V2VM() throws IOException {

    Scanner scanner = new Scanner(System.in);
    ArrayList<String> inputLines = new ArrayList<>();
    String inputRaw = "";
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      line = line.replaceAll("\\{.*?\\}","");
      line = line.replaceAll("\\s"," ");
      inputRaw+=line+'\n';
      inputLines.add(line);
    }
    scanner.close();

    CodeConverter table = new CodeConverter();
    table.debug = true;
    table.vaporMProgram=inputLines;
    table.convertCode();

    //System.out.println("# Vars: "+table.vars.size());
    //table.printVarArr();

    table.printMIPS();

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


