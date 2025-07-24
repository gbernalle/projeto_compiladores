package Sintatico;

import Semantico.SymbolTable;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

public class SyntaticAnalysis {

  private LexicalAnalysis lex;
  private Lexeme current;
  private SymbolTable semTable = new SymbolTable();

  public SyntaticAnalysis(LexicalAnalysis lex) {
    this.lex = lex;
    this.current = lex.nextToken();
  }

  private void advance() {
    current = lex.nextToken();
  }

  private void eat(TokenType type) {
    if (type == current.type) {
      advance();
    } else {
      showError();
    }
  }

  public void start() {
    procProgram();
    eat(TokenType.END_OF_FILE);
  }

  private void showError() {
    System.out.printf("%02d: ", lex.getLine());

    switch (current.type) {
      case INVALID_TOKEN:
        System.out.printf("Lexema invalido [%s]\n", current.token);
        break;
      case UNEXPECTED_EOF:
      case END_OF_FILE:
        System.out.printf("Fim de arquivo inesperado\n");
        break;
      default:
        System.out.printf("Lexema nao esperado [%s]\n", current.token);
        break;
    }

    System.exit(1);
  }

  // program::= program [decl-list] begin stmt-list end
  private void procProgram() {
    eat(TokenType.PROGRAM);
    if (current.type == TokenType.INT || current.type == TokenType.FLOAT || current.type == TokenType.CHAR) {
      procDecList();
    }
  
    eat(TokenType.BEGIN);  
    procStmtList();
    eat(TokenType.END);
  }

  // decl-list::= decl {decl}
  private void procDecList() {
    procDecl();
    while (current.type == TokenType.INT || current.type == TokenType.FLOAT || current.type == TokenType.CHAR) {
      procDecl();
    }
  }

  // decl::= type “:” ident-list “;”
  private void procDecl() {
    TokenType type = current.type;
    procType();
    eat(TokenType.TWOPOINTS);
    procIdentList(type);
    eat(TokenType.SEMICOLON);
  }

  // ident-list::= identifier {"," identifier}
  private void procIdentList(TokenType type) {
    String id = current.token;
    eat(TokenType.ID);
    semTable.declare(id, type, lex.getLine()); // registrar na tabela

    while (current.type == TokenType.COMMA) {
      eat(TokenType.COMMA);
      id = current.token;
      eat(TokenType.ID);
      semTable.declare(id, type, lex.getLine());
    }
  }

  // type::=int|float|char
  private void procType() {
      switch (current.type) {
      case INT:
        eat(TokenType.INT);
        break;
      case FLOAT:
        eat(TokenType.FLOAT);
        break;
      case CHAR:
        eat(TokenType.CHAR);
        break;
      default:
        showError();
        break;
      }
    }

  // stmt-list::= stmt {";" stmt}
  private void procStmtList() {
    procStmt();
    // Espera por vírgula
    while (current.type == TokenType.SEMICOLON) {
      eat(TokenType.SEMICOLON);
      procStmt();
    }
  }

  // stmt::= assign-stmt | if-stmt | while-stmt | repeat-stmt |read-stmt|write-stmt
  private void procStmt() {
    switch (current.type) {
      case ID:
        procAssignStmt();
        break;
      case IF:
        procIfStmt();
        break;
      case WHILE:
        procWhileStmt();
        break;
      case REPEAT:
        procRepeatStmt();
        break;
      case IN:
        procReadStmt();
        break;
      case OUT:
        procWriteStmt();
        break;
      default:
        showError();
        break;
    }
  }

  // assign-stmt::= identifier "=" simple_expr
  private void procAssignStmt() {
    String id = current.token;
    eat(TokenType.ID);

    TokenType varType = semTable.getType(id, lex.getLine()); // verifica declaração
    eat(TokenType.ASSIGN);
    TokenType exprType = procSimpleExpr();

    if (!typesCompatible(varType, exprType)) {
      System.err.printf("Erro na linha %d: Incompatibilidade de tipos em '%s = %s'\n", lex.getLine(), varType,
          exprType);
      System.exit(1);
    }
  }

  private boolean typesCompatible(TokenType varType, TokenType exprType) {
    if (varType == exprType)
      return true;

    // char + int → int: permitido na expressão, mas só pode ser atribuído se a
    // variável for int
    if ((varType == TokenType.INT) && (exprType == TokenType.CHAR))
      return true;
    if ((varType == TokenType.INT) && (exprType == TokenType.FLOAT))
      return false;
    if ((varType == TokenType.FLOAT) && (exprType == TokenType.INT))
      return true;

    // char e float são incompatíveis entre si
    if ((varType == TokenType.FLOAT && exprType == TokenType.CHAR) ||
        (varType == TokenType.CHAR && exprType == TokenType.FLOAT))
      return false;

    return false;
  }

  // if-stmt::= if condition then [decl-list] stmt-list end 
  //           |if condition then[decl-list]stmt-list else declaration stmt-list end
  private void procIfStmt() {
    eat(TokenType.IF);
    TokenType condType = procCondition();

    if (condType != TokenType.INT) {
      System.err.printf("Erro na linha %d: Condição do IF deve ser relacional (tipo inteiro).\n", lex.getLine());
      System.exit(1);
    }

    eat(TokenType.THEN);

    if (isType(current.type)) {
      procDecList();
    }

    procStmtList();

    if (current.type == TokenType.ELSE) {
      eat(TokenType.ELSE);
      if (isType(current.type)) {
        procDecList();
      }
      procStmtList();
    }

    eat(TokenType.END);
  }  

  // condition::= expression 
  private TokenType procCondition() {
    return procExpression();
  }

  private boolean isType(TokenType type) {
    return type == TokenType.INT || type == TokenType.FLOAT || type == TokenType.CHAR;
  }

  // repeat-stmt ::= repeat [decl-list] stmt-list stmt-suffix
  private void procRepeatStmt() {
    eat(TokenType.REPEAT);

    if (isType(current.type)) {
      procDecList();
    }

    procStmtList();
    procStmtSuffix();
  }
  
  // stmt-suffix ::= until condition 
  private void procStmtSuffix() {
    eat(TokenType.UNTIL);
    TokenType condType = procCondition();

    if (condType != TokenType.INT) {
      System.err.printf("Erro na linha %d: Condição do UNTIL deve ser relacional (tipo inteiro).\n", lex.getLine());
      System.exit(1);
    }
  }

  // while-stmt ::= stmt-prefix [decl-list] stmt-list end
  private void procWhileStmt() {
    TokenType condType = procStmtPrefix();

    if (condType != TokenType.INT) {
      System.err.printf("Erro na linha %d: Condição do WHILE deve ser relacional (tipo inteiro).\n", lex.getLine());
      System.exit(1);
    }

    if (isType(current.type)) {
      procDecList();
    }

    procStmtList();
    eat(TokenType.END);
  }

  // stmt-prefix ::= while condition do
  private TokenType procStmtPrefix(){
    eat(TokenType.WHILE);
    TokenType condType = procCondition();
    eat(TokenType.DO);
    return condType;
  }

  // read-stmt ::= in "(" identifier ")"
  private void procReadStmt() {
    eat(TokenType.IN);
    eat(TokenType.OP_ROUNDBRACK);

    String id = current.token;
    eat(TokenType.ID);

    if (!semTable.isDeclared(id)) {
      System.err.printf("Erro na linha %d: Variável '%s' não declarada para leitura.\n", lex.getLine(), id);
      System.exit(1);
    }

    eat(TokenType.CL_ROUNDBRACK);
  }

  // write-stmt ::= out "(" writable ")"
  private void procWriteStmt() {
    eat(TokenType.OUT);
    eat(TokenType.OP_ROUNDBRACK);
    procWritable();
    eat(TokenType.CL_ROUNDBRACK);
  }

  // writable ::= simple-expr | literal
  private void procWritable() {
    if (current.type == TokenType.LITERALS) {
      procLiteral();
    } else {
      procSimpleExpr(); // já verifica tipos internamente
    }
  }

  // expression::= simple-expr | simple-expr relop simple-expr
  private TokenType procExpression() {
    TokenType left = procSimpleExpr();
    if (current.type == TokenType.EQUAL || current.type == TokenType.GREATER ||
        current.type == TokenType.GREATER_EQUAL || current.type == TokenType.LOWER ||
        current.type == TokenType.LOWER_EQUAL || current.type == TokenType.NOT_EQUAL) {

      TokenType op = current.type;
      procRelOp();
      TokenType right = procSimpleExpr();

      // Relacionais precisam de tipos compatíveis
      if (!typesCompatible(left, right)) {
        System.err.printf("Erro na linha %d: Tipos incompatíveis em expressão relacional: %s %s %s\n",
            lex.getLine(), left, op, right);
        System.exit(1);
      }

      return TokenType.INT; // Considerando booleano como int (0 ou 1)
    }
    return left;
  }

  // simple-expr::= term | simple-expr addop term 
  private TokenType procSimpleExpr() {
    TokenType left = procTerm();
    while (current.type == TokenType.ADD || current.type == TokenType.SUB || current.type == TokenType.OR) {
      TokenType op = current.type;
      procAddOp();
      TokenType right = procTerm();
      left = resolveResultType(left, right, op);
    }
    return left;
  }

  private TokenType resolveResultType(TokenType left, TokenType right, TokenType op) {
    if ((left == TokenType.FLOAT && right == TokenType.FLOAT) ||
        (left == TokenType.FLOAT && right == TokenType.INT) ||
        (left == TokenType.INT && right == TokenType.FLOAT)) {
      return TokenType.FLOAT;
    }

    if (left == TokenType.INT && right == TokenType.INT)
      return TokenType.INT;

    if ((left == TokenType.CHAR && right == TokenType.INT) ||
        (left == TokenType.INT && right == TokenType.CHAR) ||
        (left == TokenType.CHAR && right == TokenType.CHAR)) {
      return TokenType.INT;
    }

    if ((left == TokenType.CHAR && right == TokenType.FLOAT) ||
        (left == TokenType.FLOAT && right == TokenType.CHAR)) {
      System.err.printf("Erro na linha %d: Tipos incompatíveis em operação: %s %s %s\n",
          lex.getLine(), left, op, right);
      System.exit(1);
    }

    System.err.printf("Erro na linha %d: Operação inválida: %s %s %s\n", lex.getLine(), left, op, right);
    System.exit(1);
    return null;
  }
  
  // term::= factor-a|term mulop factor-a
  private TokenType procTerm() {
    TokenType left = procFatorA();
    while (current.type == TokenType.MUL || current.type == TokenType.DIV || current.type == TokenType.AND) {
      TokenType op = current.type;
      procMulOp();
      TokenType right = procFatorA();
      left = resolveResultType(left, right, op);
    }
    return left;
  } 

  // fator-a::= factor | "!" factor | "-" factor
  private TokenType procFatorA() {
    if (current.type == TokenType.NOT) {
      eat(TokenType.NOT);
      return procFactor();
    } else if (current.type == TokenType.SUB) {
      eat(TokenType.SUB);
      return procFactor(); // o sinal negativo não muda o tipo
    } else {
      return procFactor();
    }
  }

  // factor::= identifier | constant | "(" expression ")"
  private TokenType procFactor() {
    TokenType result;
    switch (current.type) {
      case ID:
        String id = current.token;
        eat(TokenType.ID);
        result = semTable.getType(id, lex.getLine()); // verificação de declaração
        break;

      case INTEGER_CONST:
        eat(TokenType.INTEGER_CONST);
        result = TokenType.INT;
        break;

      case FLOAT_CONST:
        eat(TokenType.FLOAT_CONST);
        result = TokenType.FLOAT;
        break;

      case CHAR_CONST:
        eat(TokenType.CHAR_CONST);
        result = TokenType.CHAR;
        break;

      case OP_ROUNDBRACK:
        eat(TokenType.OP_ROUNDBRACK);
        result = procExpression(); // já retorna o tipo da subexpressão
        eat(TokenType.CL_ROUNDBRACK);
        break;

      default:
        showError();
        return null; // só para compilador parar de reclamar
    }
    return result;
  }
  
  // relop ::= "==" | ">" | ">=" | "<" | "<=" | "!="
  private void procRelOp() {
    switch (current.type) {
      case EQUAL:
        eat(TokenType.EQUAL);
        break;
      case GREATER:
        eat(TokenType.GREATER);
        break;
      case GREATER_EQUAL:
        eat(TokenType.GREATER_EQUAL);
        break;
      case LOWER:
        eat(TokenType.LOWER);
        break;
      case LOWER_EQUAL:
        eat(TokenType.LOWER_EQUAL);
        break;
      case NOT_EQUAL:
        eat(TokenType.NOT_EQUAL);
        break;
      default:
        showError();
        break;
    }
  }
  
  // addop ::= "+" | "-" | ||
  private void procAddOp() {
    switch (current.type) {
      case ADD:
        eat(TokenType.ADD);
        break;
      case SUB:
        eat(TokenType.SUB);
        break;
      case OR:
        eat(TokenType.OR);
        break;
      default:
        showError();
        break;
    }
  }
  
  // mulop ::= "*" | "/" | &&
  private void procMulOp() {
    switch (current.type) {
      case MUL:
      eat(TokenType.MUL);
      break;
      case DIV:
      eat(TokenType.DIV);
      break;
      case AND:
      eat(TokenType.AND);
      break;
      default:
      showError();
      break;
    }
  }
  
/*   // constant ::= integer_const | float_const | char_const
  private void procConstant() {
    switch (current.type) {
      case INTEGER_CONST:
        eat(TokenType.INTEGER_CONST);
        break;
      case FLOAT_CONST:
        eat(TokenType.FLOAT_CONST);
        break;
      case CHAR_CONST:
        eat(TokenType.CHAR_CONST);
        ;
        break;
      default:
        showError();
        break;
    }
  } */
  
  private void procLiteral() {
    eat(TokenType.LITERALS);
  }

}
