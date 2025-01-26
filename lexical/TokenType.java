package lexical;

public enum TokenType {
  //Specials
  UNEXPECTED_EOF,
  INVALID_TOKEN,
  END_OF_FILE,
          
  //Symbols
  ASSIGN,    // =
  COMMA,     // ,
  OP_ROUNDBRACK, // (
  CL_ROUNDBRACK, // )
  SEMICOLON, // ;
  
  // Operadores relacionais
  EQUAL, // ==
  NOT_EQUAL, // !=
  LOWER, // <
  LOWER_EQUAL, // <=
  GREATER, // >
  GREATER_EQUAL, // >=
  
 // Logical operators
  OR, // ||
  AND, // &&
  NOT, // !

  // Arithmetic operators
  ADD, // +
  SUB, // -
  MUL, // *
  DIV, // /
  MOD, // %
  
  // Keywords
  DO, // do
  ELSE, // else
  END, // end
  EXIT, //exit
  IF, // if
  INT, // int
  FLOAT, //floaT
  PRINT, //print
  SCAN, //scan
  START, //start
  STRING, //string
  THEN, // then
  WHILE, // while
  
  // Others
  NUMBER, //digitos
  ID,
  INTEGER_CONST,
  FLOAT_CONST,
  LITERALS // {} literais
}
