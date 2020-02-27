import static java.lang.System.exit;

import java.security.InvalidKeyException;
import java.util.HashMap;
import syntaxtree.Goal;

public class Typecheck {

  public static void runPrettyPrinter(Goal root) {
    PrettyPrinter<String, String> pprinter = new PrettyPrinter<>();
    pprinter.visit(root, "");
  }

  public static void typeCheck(MiniJavaParser parser) throws ParseException {
    Goal root = parser.Goal();


    //runPrettyPrinter(root);

    SymbolTable symbolTable = new SymbolTable();

    TableBuilder<String, SymbolTable> tableBuilder = new TableBuilder<>();

    tableBuilder.visit(root, symbolTable);


    //symbolTable.printSorted();


    TypeVisitor<String, SymbolTable> typeVisitor = new TypeVisitor<>();

    //System.out.println(symbolTable.getClasses());

    if (!symbolTable.hasValidInheritance()) {
      System.out.println("Type error");
      exit(-1);
    }

    typeVisitor.visit(root, symbolTable);

    //System.out.println(success);


    System.out.println("Program type checked successfully");
  }

  public static void main(String[] args) throws ParseException {

    MiniJavaParser parser = new MiniJavaParser(System.in);
    typeCheck(parser);


  }
}


