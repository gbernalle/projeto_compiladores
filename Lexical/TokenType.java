package Lexical;

public enum TokenType {
  //Specials
  UNEXPECTED_EOF,
  INVALID_TOKEN,
  END_OF_FILE,
          
  //Symbols
  SEMICOLON, // ;
  ASSIGN,    // =
  COMMA,     // ,
  OPEN_PARENTHESES, // (
  CLOSE_PARENTHESES, // )
  EXCLAMATION, // !
  OPEN_BRACKETS, // {
  CLOSE_BRACKETS, // }

  //Logical operators
  EQUAL, // ==
  NOT_EQUAL, // !=
  LOWER, // <
  LOWER_EQUAL, // <=
  GREATER, // >
  GREATER_EQUAL, // >=

  // Arithmetic operators
  ADD, // +
  SUB, // -
  MUL, // *
  DIV, // /
  MOD, // %
  OR, // ||
  AND, // &&

  // Keywords
  START, //start
  EXIT, //exit
  WHILE, // while
  DO, // do
  IF, // if
  THEN, // then
  ELSE, // else
  SCAN, //scan
  PRINT, //print
  
  // Others
  INT, // int
  FLOAT, //floaT
  STRING, //string
  NUMBER //digitos
}
