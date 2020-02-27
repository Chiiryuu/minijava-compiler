//
// Generated by JTB 1.3.2
//

import static java.lang.System.exit;

import java.util.Enumeration;
import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionRest;
import syntaxtree.FalseLiteral;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameterRest;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.Node;
import syntaxtree.NodeList;
import syntaxtree.NodeListOptional;
import syntaxtree.NodeOptional;
import syntaxtree.NodeSequence;
import syntaxtree.NodeToken;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.Type;
import syntaxtree.TypeDeclaration;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJVisitor;

public class TypeVisitor<R, A> implements GJVisitor<R, A> {

   //public int blockCount = 0;

   public void relax(A argu) {
      if (argu instanceof SymbolTable) {
         ((SymbolTable)(argu)).relaxScope();
      }
   }

   public void constrict(A argu, String newScope) {
      if (argu instanceof SymbolTable) {
         ((SymbolTable)(argu)).constrictScope(newScope);
      }
   }

   public boolean classExtends(A argu, String class1, String class2) {
     return ((SymbolTable)(argu)).classExtends(class1,class2);
   }

   public void throwTypeError() {
      System.out.println("Type error");
      exit(-1);
   }

   public void throwTypeError(String thrown) {
      System.out.println("Type error");
      //System.out.println(thrown);
      exit(-1);
   }

   public TokenEntry getSymbol(A argu, String symbolPath) {
      SymbolTable table = (SymbolTable)(argu);
      if (!table.table.containsKey(symbolPath))
         throwTypeError("No symbol "+symbolPath);
      return table.table.get(symbolPath);
   }

   public TokenEntry getValidMethodToken(A argu, String className, String methodName) {
      TokenEntry method = ((SymbolTable)(argu)).getValidMethodToken(className,methodName);
      if (method==null)
         throwTypeError("No valid method "+methodName+" in scope "+className);
      return method;
   }

   public R getVarType(A argu, String varName, String scope) {
      TokenEntry var = ((SymbolTable)(argu)).getBestSymbol(varName, scope);
      if (var==null) {
         throwTypeError("Null variable "+varName+" in "+scope);
      }
      if (var.type.equals("Method"))
         //return (R) (var.symbol+":"+var.returnType+"x"+"".join(",",var.inputs));
         return (R) var.symbol;
      if (var.type.equals("Class"))
         return (R) var.symbol;
      return (R) var.type;
   }

   public R getVarType(A argu, String varName) {
      TokenEntry var = ((SymbolTable)(argu)).getBestSymbol(varName);
      if (var==null) {
         throwTypeError("Null variable "+varName+" in "+((SymbolTable)(argu)).scope);
      }
      if (var.type.equals("Method"))
         //return (R) (var.symbol+":"+var.returnType+"x"+"".join(",",var.inputs));
         return (R) var.symbol;
      if (var.type.equals("Class"))
         return (R) var.symbol;
      return (R) var.type;
   }

   public String getRelaxedScope(A argu) {
      if (argu instanceof SymbolTable) {
         return ((SymbolTable)(argu)).getRelaxedScope();
      }
      return "";
   }

   public R getCurrentClass(A argu) {
      return (R)((SymbolTable) argu).getCurrentClass();
   }

   public Boolean typeEquals(R param1, R param2) {
      String a = (String) param1;
      String b = (String) param2;
      if (a==null || b == null) {
         throwTypeError("Null typeEquals");
      }


      else if (a.equals("Class") && b.equals("Class")) {
         return true;
      }
      else {
         return a.equals(b);
      }
      return false;
   }


   public R visit(NodeToken n, A argu) {
      R _ret= (R) n.toString();
      return _ret;
   }

   public R visit(NodeList n, A argu) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeListOptional n, A argu) {
      if ( n.present() ) {
         R _ret=null;
         String result = "";
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            result += (String) e.nextElement().accept(this,argu)+",";
            _count++;
         }
         //System.out.println("NLO: "+result.substring(0, result.length() - 1));
         return (R) result.substring(0, result.length() - 1);
      }
      else
         return null;
   }

   public R visit(NodeOptional n, A argu) {
      if ( n.present() ) {
         return n.node.accept(this, argu);
      }
      else
         return null;
   }

   public R visit(NodeSequence n, A argu) {
      R _ret=null;
      int _count=0;NLO:
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }


   /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
   public R visit(Goal n, A argu) {
      R _ret=null;
      //blockCount = 0;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      _ret = (R) "success";
      return _ret;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
   public R visit(MainClass n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String id = (String) n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      constrict(argu,id);



      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      n.f10.accept(this, argu);

      constrict(argu,"main");

      String argsName = (String) n.f11.accept(this, argu);
      n.f12.accept(this, argu);
      n.f13.accept(this, argu);



      n.f14.accept(this, argu);
      n.f15.accept(this, argu);
      n.f16.accept(this, argu);

      relax(argu);

      n.f17.accept(this, argu);
      relax(argu);
      return _ret;
   }

   /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public R visit(TypeDeclaration n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public R visit(ClassDeclaration n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String id = (String) n.f1.accept(this, argu);
      constrict(argu, id);

      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);

      relax(argu);

      return _ret;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
   public R visit(ClassExtendsDeclaration n, A argu) {
      R _ret=null;

      n.f0.accept(this, argu);
      String id = (String) n.f1.accept(this, argu);


      n.f2.accept(this, argu); //Extends

      String parent = (String) n.f3.accept(this, argu);

      constrict(argu, id);

      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);

      relax(argu);

      return _ret;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public R visit(VarDeclaration n, A argu) {
      R _ret=null;
      String type = (String) n.f0.accept(this, argu);
      String name = (String) n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   public R visit(MethodDeclaration n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String returnType = (String) n.f1.accept(this, argu);
      String name = (String) n.f2.accept(this, argu);
      n.f3.accept(this, argu);


      constrict(argu,name);

      String inputs = (String) n.f4.accept(this, argu);

      relax(argu);

      n.f5.accept(this, argu);


      constrict(argu,name);

      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      String actualReturnType = (String) n.f10.accept(this, argu);
      n.f11.accept(this, argu);
      n.f12.accept(this, argu);

      if (!actualReturnType.equals(returnType)) {
         if (!classExtends(argu, actualReturnType, returnType)) {
            throwTypeError("Invalid return type");
         }
      }


      relax(argu);
      return _ret;
   }

   /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
   public R visit(FormalParameterList n, A argu) {
      R _ret=(R)"";
      String param1 = (String) n.f0.accept(this, argu);
      String param2 = (String) n.f1.accept(this, argu);
      if (param2 != null && param2.length()>0)
         param1+=","+param2;

      return (R)param1;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public R visit(FormalParameter n, A argu) {
      R _ret=(R)"";
      String type = (String) n.f0.accept(this, argu);
      String name = (String) n.f1.accept(this, argu);
      return (R)type;
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public R visit(FormalParameterRest n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String type = (String) n.f1.accept(this, argu);
      return (R) type;
   }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public R visit(Type n, A argu) {
      R _ret=null;
      _ret = n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public R visit(ArrayType n, A argu) {
      R _ret=(R)"Array";
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "boolean"
    */
   public R visit(BooleanType n, A argu) {
      R _ret=(R)"Boolean";
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "int"
    */
   public R visit(IntegerType n, A argu) {
      R _ret=(R)"Int";
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
   public R visit(Statement n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public R visit(Block n, A argu) {
      R _ret=null;
      //constrict(argu,"block"+blockCount);
      //++blockCount;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      //relax(argu);
      return _ret;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public R visit(AssignmentStatement n, A argu) {
      R _ret=(R) "Void";
      String type1 = (String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String type2 = (String) n.f2.accept(this, argu);
      n.f3.accept(this, argu);

      if (!type1.equals(type2)) {
         if (!classExtends(argu, type1, type2)) {
            throwTypeError("Failed Assignment");
         }
      }

      return _ret;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
   public R visit(ArrayAssignmentStatement n, A argu) {
      R _ret=null;
      String var = (String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String pos = (String) n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      String val = (String) n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      if (!var.equals("Array") || !pos.equals("Int") || !val.equals("Int")) {
         throwTypeError("Bad Array Assignment");
      }

      return _ret;
   }

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
   public R visit(IfStatement n, A argu) {
      R _ret=(R)"Void";
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String type = (String) n.f2.accept(this, argu);
      if (!type.equals("Boolean"))
         throwTypeError("Bad if-statement; "+type);
      n.f3.accept(this, argu);

      n.f4.accept(this, argu);

      n.f5.accept(this, argu);

      n.f6.accept(this, argu);

      return _ret;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public R visit(WhileStatement n, A argu) {
      R _ret=(R)"Void";
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String type = (String) n.f2.accept(this, argu);
      if (!type.equals("Boolean"))
         throwTypeError("Bad While Statement");
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public R visit(PrintStatement n, A argu) {
      R _ret=(R)"Void";
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String type = (String) n.f2.accept(this, argu);
      if (!type.equals("Int"))
         throwTypeError("Bad print");
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
   public R visit(Expression n, A argu) {
      R _ret=null;
      _ret = n.f0.accept(this, argu);
      //System.out.println(((SymbolTable)argu).scope+", "+_ret);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
   public R visit(AndExpression n, A argu) {
      R _ret=(R) "Boolean";
      String type1 = (String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String type2 = (String) n.f2.accept(this, argu);
      if (!type1.equals("Boolean") || !type2.equals("Boolean"))
         throwTypeError("Bad AND");
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public R visit(CompareExpression n, A argu) {
      R _ret=(R) "Boolean";
      String type1 = (String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String type2 = (String) n.f2.accept(this, argu);
      if (!type1.equals("Int") || !type2.equals("Int"))
         throwTypeError("Bad less than");
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public R visit(PlusExpression n, A argu) {
      R _ret=(R) "Int";
      String type1 = (String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String type2 = (String) n.f2.accept(this, argu);
      if (!type1.equals("Int") || !type2.equals("Int"))
         throwTypeError("Bad plus");
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public R visit(MinusExpression n, A argu) {
      R _ret=(R) "Int";
      String type1 = (String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String type2 = (String) n.f2.accept(this, argu);
      if (!type1.equals("Int") || !type2.equals("Int"))
         throwTypeError("Bad minus");
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public R visit(TimesExpression n, A argu) {
      R _ret=(R) "Int";
      String type1 = (String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String type2 = (String) n.f2.accept(this, argu);
      if (!type1.equals("Int") || !type2.equals("Int"))
         throwTypeError("Bad times");
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   //TODO: detect out of bounds array
   public R visit(ArrayLookup n, A argu) {
      R _ret=(R)"Int";
      String type = (String) n.f0.accept(this, argu);
      if (!type.equals("Array"))
         throwTypeError("Bad array lookup, ArrType: "+type);
      n.f1.accept(this, argu);
      String index = (String) n.f2.accept(this, argu);
      if (!index.equals("Int"))
         throwTypeError("Bad array lookup, index: "+index);
      n.f3.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public R visit(ArrayLength n, A argu) {
      R _ret=(R)"Int";
      String type = (String) n.f0.accept(this, argu);
      if (!type.equals("Array"))
         throwTypeError("Bad array length");
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public R visit(MessageSend n, A argu) {
      R _ret=null;
      String className = (String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      SymbolTable table = (SymbolTable) argu;
      String tempScope = table.scope;
      table.scope = className;
      String methodName = (String) n.f2.accept(this, argu);
      table.scope = tempScope;
      n.f3.accept(this, argu);
      String inputString = (String) n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      TokenEntry method = getValidMethodToken(argu, className, methodName);
      String[] inputList = {};
      if (inputString != null)
         inputList = inputString.split(",");
      if (inputList.length != method.inputs.length)
         throwTypeError("Bad method call");
      for (int i=0; i<inputList.length;i++) {
         if (!inputList[i].equals(method.inputs[i])) {
            if (!classExtends(argu, inputList[i], method.inputs[i])) {
               throwTypeError("Wrong method arguments: " + inputList[i] + ", " + method.inputs[i]);
            }
         }
      }

      _ret = (R) method.returnType;


      return _ret;
   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public R visit(ExpressionList n, A argu) {
      R _ret=(R)"";
      String param1 = (String) n.f0.accept(this, argu);
      String param2 = (String) n.f1.accept(this, argu);
      _ret = (R) param1;
      if (param2 != null)
         _ret = (R) (param1 + "," + param2);

      return _ret;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public R visit(ExpressionRest n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String type = (String) n.f1.accept(this, argu);
      return (R) type;
   }

   /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
   public R visit(PrimaryExpression n, A argu) {
      R _ret=null;
      _ret = n.f0.accept(this, argu);
      //System.out.println("Primary: "+_ret);
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n, A argu) {
      R _ret=(R)"Int";
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "true"
    */
   public R visit(TrueLiteral n, A argu) {
      R _ret=(R) "Boolean";
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "false"
    */
   public R visit(FalseLiteral n, A argu) {
      R _ret=(R) "Boolean";
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Identifier n, A argu) {
      R _ret=null;
      String varName = (String) n.f0.accept(this, argu);
      _ret = getVarType(argu, varName);
      //System.out.println("Id: "+_ret);
      return _ret;
   }

   /**
    * f0 -> "this"
    */
   public R visit(ThisExpression n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      _ret = getCurrentClass(argu);
      if (_ret == null)
         throwTypeError("Bad this statement");
      return _ret;
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public R visit(ArrayAllocationExpression n, A argu) {
      R _ret=(R)"Array";
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public R visit(AllocationExpression n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      _ret = n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      //System.out.println("Alloc: "+_ret);
      return _ret;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public R visit(NotExpression n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      _ret = n.f1.accept(this, argu);
      if (!typeEquals((R)"Boolean", _ret))
         throwTypeError("Bad NOT");
      return _ret;
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public R visit(BracketExpression n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      _ret = n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

}
