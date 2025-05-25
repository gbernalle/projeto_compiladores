package lexical;

import java.util.Map;
import java.util.HashMap;
public class SymbolTable {
  private Map<String, TokenType> st;
  
  public SymbolTable() {
    st = new HashMap<String, TokenType>();

    //Symbols
    st.put(";", TokenType.SEMICOLON);
    st.put(":", TokenType.TWOPOINTS);
    st.put(",", TokenType.COMMA);
    st.put("=", TokenType.ASSIGN);
    st.put("_", TokenType.STRING);
    st.put("(", TokenType.OP_ROUNDBRACK);
    st.put(")", TokenType.CL_ROUNDBRACK);
    
    // Logic operators
    st.put("!", TokenType.NOT);
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
    st.put("program", TokenType.PROGRAM);
    st.put("begin", TokenType.BEGIN);
    st.put("int", TokenType.INT);
    st.put("float", TokenType.FLOAT);
    st.put("char", TokenType.CHAR);
    st.put("if", TokenType.IF);
    st.put("then", TokenType.THEN);
    st.put("end", TokenType.END);
    st.put("else", TokenType.ELSE);
    st.put("repeat", TokenType.REPEAT);
    st.put("until", TokenType.UNTIL);
    st.put("while", TokenType.WHILE);
    st.put("do", TokenType.DO);
    st.put("in", TokenType.IN);
    st.put("out", TokenType.OUT);

    // Others
    st.put("\"", TokenType.LITERALS);
    st.put("'", TokenType.SQUOTES);
    st.put("{", TokenType.OP_BRACKETS);
    st.put("}", TokenType.CL_BRACKETS);
  }
  
  public boolean contains(String token) {
    return st.containsKey(token);
  }

  public TokenType find(String token) {
    return this.contains(token) ? st.get(token) : TokenType.ID;
  }
}
