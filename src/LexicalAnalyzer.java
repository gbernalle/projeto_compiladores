import java.util.HashMap;

public class LexicalAnalyzer {
  private String input;
  private int position;
  private int lineNumber;
  private HashMap<String, TokenType> symbolTable;

  public LexicalAnalyzer(String input) {
    this.input = input;
    this.position = 0;
    this.lineNumber = 1;
    this.symbolTable = new HashMap<>();
    prepopulateSymbolTable();
  }

  // public Token analyze() throws Exception {
    
  // }

  private void printSymbolTable() {
    System.out.println("");
    System.out.println("Tabela de simbolos");
    symbolTable.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
  }

  private void prepopulateSymbolTable() {
    // keywords
    symbolTable.put("start", TokenType.KEYWORD);
    symbolTable.put("exit", TokenType.KEYWORD);
    symbolTable.put("int", TokenType.KEYWORD);
    symbolTable.put("float", TokenType.KEYWORD);
    symbolTable.put("string", TokenType.KEYWORD);
    symbolTable.put("if", TokenType.KEYWORD);
    symbolTable.put("then", TokenType.KEYWORD);
    symbolTable.put("else", TokenType.KEYWORD);
    symbolTable.put("end", TokenType.KEYWORD);
    symbolTable.put("do", TokenType.KEYWORD);
    symbolTable.put("while", TokenType.KEYWORD);
    symbolTable.put("scan", TokenType.KEYWORD);
    symbolTable.put("print", TokenType.KEYWORD);
    // relops
    symbolTable.put("==", TokenType.RELOP);
    symbolTable.put(">", TokenType.RELOP);
    symbolTable.put(">=", TokenType.RELOP);
    symbolTable.put("<", TokenType.RELOP);
    symbolTable.put("<=", TokenType.RELOP);
    symbolTable.put("!=", TokenType.RELOP);
    // addops
    symbolTable.put("+", TokenType.ADDOP);
    symbolTable.put("-", TokenType.ADDOP);
    symbolTable.put("||", TokenType.ADDOP);
    // mulops
    symbolTable.put("*", TokenType.MULOP);
    symbolTable.put("/", TokenType.MULOP);
    symbolTable.put("%", TokenType.MULOP);
    symbolTable.put("&&", TokenType.MULOP);
  }

  private boolean isKeyword(String tokenValue) {
    return symbolTable.containsKey(tokenValue) && symbolTable.get(tokenValue) == TokenType.KEYWORD;
  }
}

class Token {
  private TokenType type;
  private String value;

  public Token(TokenType type, String value) {
    this.type = type;
    this.value = value;
  }

  public TokenType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }
}

enum TokenType {
  KEYWORD,
  RELOP,
  ADDOP,
  MULOP,
  CONSTANT,
  INTEGER_CONST,
  FLOAT_CONST,
  LITERAL,
  IDENTIFIER,
  LETTER,
  DIGIT,
  CARACTERE 
}