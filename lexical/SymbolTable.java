package lexical;

import java.util.Map;
import java.util.HashMap;
public class SymbolTable {
  private Map<String, TokenType> st;
  
  public SymbolTable() {
    st = new HashMap<String, TokenType>();

    //Symbols
    st.put(";", TokenType.SEMICOLON);
    st.put(",", TokenType.COMMA);
    st.put("=", TokenType.ASSIGN);
    st.put("_", TokenType.STRING);
    st.put("(", TokenType.OP_ROUNDBRACK);
    st.put(")", TokenType.CL_ROUNDBRACK);
    st.put("{", TokenType.LITERALS);
    st.put("}", TokenType.LITERALS);
    st.put("!", TokenType.NOT);

    // Logic operators
    st.put("==", TokenType.EQUAL);
    st.put(">", TokenType.GREATER);
    st.put(">=", TokenType.GREATER_EQUAL);
    st.put("<", TokenType.LOWER);
    st.put("<=", TokenType.LOWER_EQUAL);
    st.put("!=", TokenType.NOT_EQUAL);

    // Arithmetic operators
    st.put("+", TokenType.ADD);
    st.put("-", TokenType.SUB);
    st.put("*", TokenType.MUL);
    st.put("/", TokenType.DIV);
    st.put("%", TokenType.MOD);

    //Logical operators
    st.put("||", TokenType.OR);
    st.put("&&", TokenType.AND);

    //Keywords
    st.put("start", TokenType.START);
    st.put("exit", TokenType.EXIT);
    st.put("while", TokenType.WHILE);
    st.put("do", TokenType.DO);
    st.put("if", TokenType.IF);
    st.put("then", TokenType.THEN);
    st.put("else", TokenType.ELSE);
    st.put("scan", TokenType.SCAN);
    st.put("print", TokenType.PRINT);
    st.put("int", TokenType.INT);
    st.put("float", TokenType.FLOAT);
    st.put("string", TokenType.STRING);
    st.put("scan", TokenType.SCAN);
  }
  
  public boolean contains(String token) {
    return st.containsKey(token);
  }

  public TokenType find(String token) {
    return this.contains(token) ? st.get(token) : TokenType.ID;
  }
}
