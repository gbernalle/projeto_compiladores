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

  public Lexeme nextToken() {
    Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);
    int state = 1; 
    while (state != 16 && state != 17 && state != 18) {
      int c = getc();
      switch (state) {
        //#region Orquestrador de chamadas de fluxo
        case 1:
          if (c == ' ' || c == '\t' || c == '\r'){
            state = 1;
          } else if(c == '\n'){
            line++;
            state = 1;
          } else if(c == '%'){
            lex.token += (char) c;
            state = 2;
          } else if(c == '{'){
            lex.token += (char) c;
            state = 3;
          }

          // Arithmetic operators flow
          else if (c == '+' || c == '*' || c == '-' || c == '/') {
            lex.token += (char) c;
            state = 16;
          }

          // Logic operators
          else if (c == '&') {
            lex.token += (char) c;
            state = 4;
          } else if (c == '!') {
            lex.token += (char) c;
            state = 14;
          } else if ( c == '|') {
            lex.token += (char) c;
            state = 5;
          }

            // Relational operator except ==
          else if (c == '>') { // GREATER or GREATER_EQUAL
            lex.token += (char) c;
            state = 6;
          } else if (c == '<') { // LOWER or LOWER_EQUAL or NOT_EQUAL
            lex.token += (char) c;
            state = 7;
          }

          // Symbols
          else if (c == '=') { // EQUAL or ASSIGN
            lex.token += (char) c;
            state = 8;
          } else if (c == ',' || c == ';' || c == ':' || c == '(' || c == ')') {
            lex.token += (char) c;
            state = 16;
          }

          // Zebras - Keywords or ids
          else if (c == '_' || Character.isLetter(c)) {
            lex.token += (char) c;
            state = 9;
          }

          // Zebras - Literals
          else if (c == '"') {
            lex.token += (char) c;
            state = 10;
          }

          else if (c == '\'') {
            lex.token += (char) c;
            state = 15;
          }

          // Zebras - Numbers
          else if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 11;
          }

          // end of file or invalid token
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
          break;
        
        case 2: // Comentário única linha
          if (c == '\n') {
            line++;
            state = 1;
          } else if(c == -1) {
            lex.type = TokenType.END_OF_FILE;
            state = 18;
          } else {
            state = 2;
          }
          break;
        case 3: // Comentário de múltiplas linhas
          if (c == '\n'){
            line++;
            state = 3;
          }else if (c == '}') {
            state = 1;
          } else if(c == -1) {
            lex.type = TokenType.END_OF_FILE;
            state = 18;
          } else {
            state = 3;
          }
          break;

        case 4: // Fluxo de &&
          if (c == '&') {
            lex.token += (char) c;
            state = 16;
          } else {
            if (c == -1) {
              lex.type = TokenType.END_OF_FILE;
              state = 17;
            } else {
              lex.type = TokenType.INVALID_TOKEN;
              state = 17;
            }
          }
          break;
        case 5: // Fluxo de OR
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
        case 6: // Greater or greater equal
          if (c == '=') {
            lex.token += (char) c;
          } else {
            if (c != -1)
              ungetc(c);
          }
          state = 16;
          break;
        case 7: // Lower or lower equal or not equal
          if (c == '=') {
            lex.token += (char) c;
          } else if (c == '>') {
            lex.token += (char) c;
          } else {
            if (c != -1)
              ungetc(c);
          }
          state = 16;
          break;
        case 8: // Equal or assign flow
          if (c == '=') {
            lex.token += (char) c;
          } else {
            if (c != -1)
              ungetc(c);
          }
          state = 16;
          break;
        case 9: // Keywords or ids flow
          if (c == '_' || Character.isLetter(c) || Character.isDigit(c) || c == '$') {
            lex.token += (char) c;
            state = 9;
          } else {
            if (c != -1)
              ungetc(c);
            state = 16  ;
          }
          break;
        case 10:// begin of a literal
          if (c == '"') {
            lex.token += (char) c;
            lex.type = TokenType.LITERALS;
            state = 18;
          } else if (c == -1 || c == '\n') { // erro: string não fechada
            lex.type = TokenType.UNEXPECTED_EOF;
            state = 17;
          } else {
            lex.token += (char) c;
            state = 10;
          }
          break;
        case 11: // Numerical flow
          if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 11;
          } else if (c == '.') { // its a float
            lex.token += (char) c;
            state = 12;
          } else {
            if (c != -1)
              ungetc(c);
            lex.type = TokenType.INTEGER_CONST;
            state = 18;
          }
          break;
        case 12: // After dot float flow
          if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 13;
          } else {
            if (c == -1) {
              lex.type = TokenType.UNEXPECTED_EOF;
              state = 17;
            } else {
              lex.token += (char) c;
              lex.type = TokenType.INVALID_TOKEN;
              state = 17;
            }
          }
          break;
        case 13: // Decimal float flow
          if (Character.isDigit(c)) {
            lex.token += (char) c;
            state = 13;
          } else {
            if (c != -1)
              ungetc(c);
            lex.type = TokenType.FLOAT_CONST;
            state = 18;
          }
          break;
        case 14:
          if (c == '=') {
            lex.token += (char) c;
            state = 16;
          } else {
            if (c != -1)
              ungetc(c);
            lex.type = TokenType.NOT;
            state = 16;
          }
          break;
        case 15:
            if (c == '\'') {
              lex.token += (char) c;
              lex.type = TokenType.CHAR_CONST;
              state = 18;
            } else if (c == -1 || c == '\n') { // erro: string não fechada
              lex.type = TokenType.UNEXPECTED_EOF;
              state = 17;
            } else {
              lex.token += (char) c;
              state = 15;
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
