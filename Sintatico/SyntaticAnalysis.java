package Sintatico;

import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

public class SyntaticAnalysis {

  private LexicalAnalysis lex;
  private Lexeme current;

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
        System.out.printf("Lexema inválido [%s]\n", current.token);
        break;
      case UNEXPECTED_EOF:
      case END_OF_FILE:
        System.out.printf("Fim de arquivo inesperado\n");
        break;
      default:
        System.out.printf("Lexema não esperado [%s]\n", current.token);
        break;
    }

    System.exit(1);
  }

  // program::=start[decl-list]stmt-list exit 
  private void procProgram() {
    eat(TokenType.START);
    if (current.type == TokenType.INT || current.type == TokenType.FLOAT || current.type == TokenType.STRING) {
      procDecList();
    }
    procStmtList();
    eat(TokenType.EXIT);
  }

  // decl-list::=decl{decl}
  private void procDecList() {
    procDecl();
    while (current.type == TokenType.INT || current.type == TokenType.FLOAT || current.type == TokenType.STRING) {
      procDecl();
    }
  }

  // decl::= type ident-list";"
  private void procDecl() {
    procType();
    procIdentList();
    eat(TokenType.SEMICOLON);
  }

  // ident-list::=identifier{"," identifier}
  private void procIdentList() {
    procIdentifier();
    // Espera por vírgula
    while (current.type == TokenType.COMMA) {
      eat(TokenType.COMMA);
      procIdentifier();
    }
  }

  // type::=int|float|string
  private void procType() {
      switch (current.type) {
      case TokenType.INT:
        eat(TokenType.INT);
        break;
      case TokenType.FLOAT:
        eat(TokenType.FLOAT);
        break;
      case TokenType.STRING:
        eat(TokenType.STRING);
        break;
      default:
        showError();
        break;
      }
    }

  // stmt-list::=stmt{stmt}
  private void procStmtList() {
    procStmt();
    // Verifica se tem stmt
    while(current.type == TokenType.ID || current.type == TokenType.IF ||
		      current.type == TokenType.DO || current.type == TokenType.SCAN ||
          current.type == TokenType.PRINT) {
            procStmt();
       }
  }

  // stmt::=assign-stmt";"|if-stmt|while-stmt|read-stmt";"|write-stmt";"
  private void procStmt() {
    switch (current.type) {
      case TokenType.ID:
        procAssignStmt();
        eat(TokenType.SEMICOLON);
        break;
      case TokenType.IF:
        procIfStmt();
        break;
      case TokenType.DO:
        procWhileStmt();
        break;
      case TokenType.SCAN:
        procReadStmt();
        eat(TokenType.SEMICOLON);
        break;
      case TokenType.PRINT:
        procWriteStmt();
        eat(TokenType.SEMICOLON);
        break;
      default:
        showError();
        break;
    }
  }

  // assign-stmt::=identifier"="simple_expr
  private void procAssignStmt() {
    procIdentifier();
    eat(TokenType.ASSIGN);
    procSimpleExpr();
  }

  // if-stmt::=if condition then stmt-list end|if condition then stmt-list else stmt-list end 
  private void procIfStmt() {
    eat(TokenType.IF);
    procCondition();
    eat(TokenType.THEN);
    procStmtList();
    switch (current.type) {
      case TokenType.END:
        eat(TokenType.END);
        break;
      case TokenType.ELSE:
        eat(TokenType.ELSE);
        procStmtList();
        eat(TokenType.END);
      default:
        showError();
        break;
    }
  }

  // condition::=expression 
  private void procCondition() {
    procExpression();
  }

  // while-stmt::=do stmt-list stmt-sufix
  private void procWhileStmt() {
    eat(TokenType.DO);
    procStmtList();
    procStmtSufix();
  }

  // stmt-sufix::=while condition end 
  private void procStmtSufix() {
    eat(TokenType.WHILE);
    procCondition();
    eat(TokenType.END);
  }

  // read-stmt::=scan"("identifier")"
  private void procReadStmt() {
    eat(TokenType.SCAN);
    eat(TokenType.OP_ROUNDBRACK);
    procIdentifier();
    eat(TokenType.CL_ROUNDBRACK);
  }

  // write-stmt::=print"("writable")"
  private void procWriteStmt() {
    eat(TokenType.PRINT);
    eat(TokenType.OP_ROUNDBRACK);
    procWritable();
    eat(TokenType.CL_ROUNDBRACK);
  }

  // writable::=simple-expr|literal
  private void procWritable() {
    switch (current.type) {
      case TokenType.LITERALS:
        procLiteral();
        break;
      default:
        procSimpleExpr();
        break;
    }
  }
  // expression::=simple-expr|simple-expr relop simple-expr
  private void procExpression() {
    procSimpleExpr();
    switch (current.type) {
      case TokenType.EQUAL:
      case TokenType.GREATER:
      case TokenType.GREATER_EQUAL:
      case TokenType.LOWER:
      case TokenType.LOWER_EQUAL:
      case TokenType.NOT_EQUAL:
        procRelOp();
        procSimpleExpr();
        break;
      default:
        showError();
        break;
    }
  }

  // simple-expr::=term|simple-expr addop term 
  private void procSimpleExpr() {
    switch (current.type) {
      case TokenType.NOT:
      case TokenType.SUB:
      case TokenType.ID:
      case TokenType.INTEGER_CONST:
      case TokenType.FLOAT_CONST:
      case TokenType.LITERALS:
      case TokenType.OP_ROUNDBRACK:
        procTerm();
        break;
      default:
        procSimpleExpr();
        procAddOp();
        procTerm();
        break;
    }
  }
  // term::=factor-a|term mulop factor-a
  private void procTerm() {
    switch (current.type) {
      case TokenType.NOT:
      case TokenType.SUB:
      case TokenType.ID:
      case TokenType.INTEGER_CONST:
      case TokenType.FLOAT_CONST:
      case TokenType.LITERALS:
      case TokenType.OP_ROUNDBRACK:
        procFatorA();
        break;
      default:
        procTerm();
        procMulOp();
        procFatorA();
        break;
    }
  }

  // fator-a::=factor|"!"factor|"-"factor
  private void procFatorA() {
    switch (current.type) {
      case TokenType.NOT:
        eat(TokenType.NOT);
        procFactor();
        break;
      case TokenType.SUB:
        eat(TokenType.SUB);
        procFactor();
      default:
        procFactor();
        break;
    }
  }

  // factor::=identifier|constant|"("expression")"
  private void procFactor() {
    switch (current.type) {
      case TokenType.ID:
        procIdentifier();
        break;
      case TokenType.INTEGER_CONST:
      case TokenType.FLOAT_CONST:
      case TokenType.LITERALS:
        procConstant();
        break;
      case TokenType.OP_ROUNDBRACK:
        eat(TokenType.OP_ROUNDBRACK);
        procExpression();
        eat(TokenType.CL_ROUNDBRACK);
        break;
      default:
        showError();
        break;
    }
  }
  
  private void procIdentifier() {
    eat(TokenType.ID);
  }

  private void procRelOp() {
    switch (current.type) {
      case TokenType.EQUAL:
        eat(TokenType.EQUAL);
        break;
      case TokenType.GREATER:
        eat(TokenType.GREATER);
        break;
      case TokenType.GREATER_EQUAL:
        eat(TokenType.GREATER_EQUAL);
        break;
      case TokenType.LOWER:
        eat(TokenType.LOWER);
        break;
      case TokenType.LOWER_EQUAL:
        eat(TokenType.LOWER_EQUAL);
        break;
      case TokenType.NOT_EQUAL:
        eat(TokenType.NOT_EQUAL);
        break;
      default:
        showError();
        break;
    }
  }

  private void procAddOp() {
    switch (current.type) {
      case TokenType.ADD:
        eat(TokenType.ADD);
        break;
      case TokenType.SUB:
        eat(TokenType.SUB);
        break;
      case TokenType.OR:
        eat(TokenType.OR);
        break;
      default:
        showError();
        break;
    }
  }
  
  private void procMulOp() {
    switch (current.type) {
      case TokenType.MUL:
        eat(TokenType.MUL);
        break;
      case TokenType.DIV:
        eat(TokenType.DIV);
        break;
      case TokenType.MOD:
        eat(TokenType.MOD);
        break;
      case TokenType.AND:
        eat(TokenType.AND);
        break;
      default:
        showError();
        break;
    }
  }

  private void procConstant() {
    switch (current.type) {
      case TokenType.INTEGER_CONST:
        eat(TokenType.INTEGER_CONST);
        break;
      case TokenType.FLOAT_CONST:
        eat(TokenType.FLOAT_CONST);
        break;
      case TokenType.LITERALS:
        procLiteral();
        break;
      default:
        showError();
        break;
    }
  }

  private void procLiteral() {
    eat(TokenType.LITERALS);
  }
}
