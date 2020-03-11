import static java.lang.System.exit;

import java.security.InvalidKeyException;
import java.util.HashMap;
import syntaxtree.Goal;

public class J2V {

  public static void runPrettyPrinter(Goal root) {
    PrettyPrinter<String, String> pprinter = new PrettyPrinter<>();
    pprinter.visit(root, "");
  }

  public static void J2V(MiniJavaParser parser) throws ParseException {
    Goal root = parser.Goal();


    //runPrettyPrinter(root);

    /*  Make vapor visitor
        > Add to string node by node
        > start with "func Main()\n"
        > Keep java pretty printer for testing
        > Ditch everything else in HW2
        > Assume input is good
        *** Only allow binary operations
           > Operator chains must be lowered to binary ops
           > "a = 1+2+3;" becomes "temp = 2 + 3; a = temp + 1"
           > Multiple vapor lines can be made from a single Java line

    VaporVis vs = new VaporVis();
    vs.debug = true;
    */

    SymbolTable symbolTable = new SymbolTable();

    ClassVisitor tableBuilder = new ClassVisitor();

    tableBuilder.visit(root, symbolTable);

    symbolTable.setClassInheritance();

    symbolTable.calculateClassSizes();

    symbolTable.makeClassConsts();

    //System.out.println(symbolTable);

    VaporVisitor vapor = new VaporVisitor();
    vapor.debug = true;
    String output = vapor.visit(root, symbolTable);
    //System.out.println(program);
    String program = symbolTable.classConsts+'\n';
    for (MethodBlock block : symbolTable.blocks.values()) {
      program += block.toString() + "\n\n";
    }
    System.out.println(program);
    //System.out.println(symbolTable.blocks.get("Main"));
  }

  public static void main(String[] args) throws ParseException {

    MiniJavaParser parser = new MiniJavaParser(System.in);
    J2V(parser);

  }
}


