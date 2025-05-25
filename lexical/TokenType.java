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
  TWOPOINTS, // :
  
  // Operadores relacionais
  EQUAL, // ==
  NOT_EQUAL, // !=
  LOWER, // <
  LOWER_EQUAL, // <=
  GREATER, // >
  GREATER_EQUAL, // >=
  
 // Logical operators
  OR, // ||
  NOT, // !
  
  // Arithmetic operators
  ADD, // +
  SUB, // -
  MUL, // *
  DIV, // /
  MOD, // %
  
  // Keywords
  PROGRAM, // program
  BEGIN, // begin
  INT, // int
  FLOAT, //floaT
  CHAR, //char
  IF, // if
  THEN, // then
  END, // end
  ELSE, // else
  REPEAT, // repeat
  UNTIL, //until
  WHILE, // while
  DO, // do
  IN, // in
  OUT, //out
  STRING, //string
  AND, // &&
  
  // Others
  NUMBER, //digitos
  ID,
  INTEGER_CONST,
  FLOAT_CONST,
  CHAR_CONST,
  SQUOTES,
  LITERALS,
  OP_BRACKETS, // {
  CL_BRACKETS // {
}
