package Semantico;

import java.util.HashMap;
import java.util.Map;
import lexical.TokenType;

public class SymbolTable {
  
  private Map<String, TokenType> table = new HashMap<>();

  public void declare(String name, TokenType type, int line) {
    String id = name.toLowerCase(); // linguagem não é case-sensitive
    if (table.containsKey(id)) {
      System.err.printf("Erro na linha %d: Variável '%s' já declarada.\n", line, name);
      System.exit(1);
    }
    table.put(id, type);
  }

  public boolean isDeclared(String name) {
    return table.containsKey(name.toLowerCase());
  }

  public TokenType getType(String name, int line) {
    String id = name.toLowerCase();
    if (!table.containsKey(id)) {
      System.err.printf("Erro na linha %d: Variável '%s' não foi declarada.\n", line, name);
      System.exit(1);
    }
    return table.get(id);
  }
  
}
