package lexical;

public enum TokenType {
  //Specials
  UNEXPECTED_EOF,
  INVALID_TOKEN,
  END_OF_FILE,
          
  //Symbols
  SEMICOLON, // ;
  ASSIGN,    // =
  COMMA,     // ,
  OP_ROUNDBRACK, // (
  CL_ROUNDBRACK, // )
  EXCLAMATION, // !
  OP_CURLYBRACK, // {
  CL_CURLYBRACK, // }
  UNDERSCORE, // _

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
