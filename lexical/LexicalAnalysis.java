package lexical;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class LexicalAnalysis implements AutoCloseable {
  
  private int line;
  private SymbolTable st;
  private PushbackInputStream input; //Cria um buffer para armazenar os lexemas vindos do arquivo

  public LexicalAnalysis(String filename) {
    try {
      input = new PushbackInputStream(new FileInputStream(filename));
    } catch (Exception e) {
      throw new LexicalException("Unable to open file: " + filename);
    }

    st = new SymbolTable();
    line = 1;
  }
  
  public void close() {
    try {
      input.close();
    } catch (Exception e) {
      throw new LexicalException("Unable to close file");
    }
  }

  public int getLine() {
    return this.line;
  }

  /*
   * DICIONARIO:
   * 
   * START_STATE 1
   * COMMENT_STATE 2
   * MULTI_LINE_COMMENT_STATE 3;
   * MULTI_LINE_END_COMMENT_STATE 4
   * ONE_LINE_COMMENT_STATE 5
   * AND_LINE_STATE 6
   * OR_LINE_STATE 7
   * EQUAL_ASSIGN_LINE_STATE 8
   * GREATER_GREATER_EQUAL_LINE_STATE 9
   * LOWER_LOWER_EQUAL_LINE_STATE 10
   * KEYWORDS_IDS_LINE_STATE 11
   * NUMBER_LINE_STATE 12
   * FLOAT_DOT_STATE 13
   * FLOAT_DECIMAL_STATE 14
   * LITERAL_LINE_STATE 15
   * ST_FIND_STATE 16
   * ERROR_STATE 17
   * DEFAULT_END_STATE 18
   */

  public Lexeme nextToken() {
    Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);
    int state = 1; 
    while (state != 18 && state != 17 && state != 16) {
      int c = getc();
      switch (state) {
        case 1: // Start_state
        
          //Comentário e variável inutilizadas
          if (c == ' ' || c == '\t' || c == '\r') {
            state =  1;
          } else if (c == '\n') {
            line++;
            state = 1;
          } else if (c == '/') { // Primeira aparição, pode ser comentário
            lex.token += (char) c;
            state = 2;
          }

          // Operadores Aritméticos
          else if (c == '+' || c == '*' || c == '-' || c == '%') {
            lex.token += (char) c;
            state = 16; // Find_state
          }

          // Operadores lógicos

          else if (c == '&') {
            lex.token += (char) c;
            state = 6; // AND_State
          } else if (c == '!') {
            lex.token += (char) c;
            state = 16; // Find_State
          } else if (c == '|') {
            lex.token += (char) c;
            state = 7; // OR_State
          }
          
          // Operadores relacionais

          else if (c == '>') {
            lex.token += (char) c;
            state = 9;
          } else if (c == '<') {
            lex.token += (char) c;
            state = 10;
          }

          // Symbols

          else if (c == '=') { //Igual ou atribuição
            lex.token += (char) c;
            state = 8;
          } else if (c == ',' || c == '(' || c == ')' || c == ';' || c == ':') {
            lex.token += (char) c;
            state = 16;
          }

          // Ids ou keywords
          else if (Character.isLetter(c) || c == '_') {
						lex.token += (char) c;
						state = 11;
          } else if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 12;
          }

          // Literais
          else if(c == '{'){
            lex.token += (char) c;
					  state = 15;
          }
          
          // Fim de arquivo ou token invalido
          else {
            if (c == -1) {
              lex.type = TokenType.END_OF_FILE;
              state = 18;
            } else {
              lex.token += (char) c;
              lex.type = TokenType.INVALID_TOKEN;
              state = 17;
            }
          }
          break; // Fim Tokens iniciais
      
        case 2: // Fluxo de comentário
          if (c == '*') {
            lex.token = "";
            state = 3;
          } else if (c == '/') {
            lex.token = "";
            state = 5;
          } else {
            if (c == -1) {
              lex.type = TokenType.UNEXPECTED_EOF;
              state = 17;
            } else { // Operador de Divisão
              ungetc(c);
              state = 18;
            }
          }
          break;
        
        case 3: // Fluxo de Comentário multiLinha
          if (c == '*') {
            state = 4;
          } else if (c == '\n') {
            line++;
            state = 3;
          } else if (c == -1) {
            lex.type = TokenType.END_OF_FILE;
            state = 18;
          } else {
            state = 3;
          }
          break;
        
        case 4: // Fluxo de Fim do comentário de múltiplas linhas
          if (c == '/') {
            state = 1;  
          } else if (c == -1) {
            lex.type = TokenType.END_OF_FILE;
            state = 18;
          } else {
            state = 3;
          }
          break;

        case 5: // Fluxo de Comentário de linha única
          if (c == '\n') {
            line++;
            state = 1;
          } else if (c == -1) {
            lex.type = TokenType.END_OF_FILE;
            state = 18;
          } else {
            state = 5;
          }
          break;

        case 6: // Fluxo And
          if (c == '&') {
            lex.token += (char) c;
            state = 16;
          } else {
            if (c == -1) {
              lex.type = TokenType.UNEXPECTED_EOF;
              state = 17;
            } else {
              lex.type = TokenType.INVALID_TOKEN;
              state = 17;
            }
          }
          break;

        case 7: // Fluxo OR
          if (c == '|') {
            lex.token += (char) c;
            state = 16;
          } else {
            if (c == -1) {
              lex.type = TokenType.UNEXPECTED_EOF;
              state = 17;
            } else {
              lex.type = TokenType.INVALID_TOKEN;
              state = 17;
            }
          }
          break;

        case 8: // Fluxo de igual ou atribuição
          if (c == '=') {
            lex.token += (char) c;
          } else {
            if (c != -1) {
              ungetc(c);
            }
          }
          state = 16;
          break;

        case 9: // Fluxo de Maior igual
          if (c == '=') {
            lex.token += (char) c;
          } else {
            if (c != -1) {
              ungetc(c);
            }
          }
          state = 16;
          break;

        case 10: // Fluxo de Menor igual
          if (c == '=') {
            lex.token += (char) c;
          } else {
            if (c != -1) {
              ungetc(c);
            }
          }
          state = 16;
          break;

        case 11: // Palavras reservadas e ids
          if (c == '_' || Character.isLetter(c) || Character.isDigit(c)) {
            lex.token += (char) c;
            state = 11;
          } else {
            if (c != -1) {
              ungetc(c);
            }
            state = 16;
          }
          break;

        case 15: // Fluxo de Literais
          if ((c >= 0 && c <= 9)
						|| (c >= 11 && c <= 122)
						|| (c == 124)
              || (c >= 126 && c <= 255)) {
            lex.token += (char) c;
            state = 15; 
          } else if (c == '}') {
            lex.token += (char) c;
            lex.type = TokenType.LITERALS;
            state = 18;
          } else {
            lex.type = TokenType.INVALID_TOKEN;
            state = 17;
          }
          break;

        case 12: // Fluxo de Números
          if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 12;
          } else if (c == '.') { // Número é Float
            lex.token += (char) c;
            state = 13;
          } else {
            if (c != -1) {
              ungetc(c);
            }
            lex.type = TokenType.INTEGER_CONST;
            state = 18;
          }
          break;

        case 13: // Fluxo float após ponto
          if (Character.isDigit(c)) { 
            lex.token += (char) c;
            state = 14;
          } else {
            if (c != -1) {
              lex.type = TokenType.UNEXPECTED_EOF;
              state = 17;
            } else {
              lex.token += (char) c;
              lex.type = TokenType.INVALID_TOKEN;
              state = 17;
            }
          }
          break;

        case 14: // Fluxo float
          if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 14;
          } else {
            if (c != -1) {
              ungetc(c);
            }
            lex.type = TokenType.FLOAT_CONST;
            state = 18;
          }
          break;     

        default:
          throw new LexicalException("Invalid State");
      }
    }

    if (state == 16) {
      lex.type = st.find(lex.token);
    }

    return lex;
  }

  private int getc() {
    try {
      return input.read();
    } catch (Exception e) {
      throw new LexicalException("Unable to read file");
    }
  }

  private void ungetc(int c) {
    if (c != -1) {
      try {
        input.unread(c);
      } catch (Exception e) {
        throw new LexicalException("Unable to ungetc");
      }
    }
  }

}
