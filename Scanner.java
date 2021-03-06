package com.interpreter.jnex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.interpreter.jnex.TokenType.*;

class Scanner{
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;
  private static final Map<String, TokenType> keywords;

  // hashmap that holds the reserved keywords
  static {
    keywords = new HashMap<>();
    keywords.put("and", AND);
    keywords.put("class",  CLASS);
    keywords.put("else",   ELSE);
    keywords.put("false",  FALSE);
    keywords.put("for",    FOR);
    keywords.put("fun",    FUN);
    keywords.put("if",     IF);
    keywords.put("nil",    NIL);
    keywords.put("or",     OR);
    keywords.put("print",  PRINT);
    keywords.put("return", RETURN);
    keywords.put("super",  SUPER);
    keywords.put("this",   THIS);
    keywords.put("true",   TRUE);
    keywords.put("var",    VAR);
    keywords.put("while",  WHILE);
  }

  // constructor
  Scanner(String source){
    this.source = source;
  }

  // loop to fill list with generated tokens
  List<Token> scanTokens(){
    while(!isAtEnd()){
      //the beginning of the next lexeme
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  // check lexemes
  private void scanToken(){
    char c = advance();
    switch(c){
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;
      case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
      case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
      case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
      case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
      case '/':
          if(match('/')){
              // a line comment goes till the end of the line
              while(peek() != '\n' && !isAtEnd()) advance();
          }
          else if(match('*')){
            // block comment check
            while(peek() != '*' && peekNext() != '/' && !isAtEnd()) advance();
          }
          else{
            addToken(SLASH);
          }
          break;
      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;
      case '\n':
        line++;
        break;
      case '"':
        string();break;
      default:
        if(isDigit(c)){
          number();
        }
        else if(isAlpha(c)){
          identifier();
        }
        else{
          Nex.error(line, "Unexpected character");
        }
        break;
    }
  }

  // check if char is identifier
  private void identifier(){
    while(isAlphaNumeric(peek())) advance();

    // see if the identifier is a reserved word
    String text = source.substring(start, current);

    TokenType type = keywords.get(text);
    if(type == null) type = IDENTIFIER;
    addToken(type);
  }

  // store number token
  private void number(){
    while(isDigit(peek())) advance();

    // check if number is decimal
    if(peek() == '.' && isDigit(peekNext())){
      // Consume the '.'
      advance();

      while(isDigit(peek())) advance();
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  // store string token
  private void string(){
    while(peek() != '"' && !isAtEnd()){
      if(peek() == '\n') line++;
      advance();
    }

    // unterminated string
    if(isAtEnd()){
      Nex.error(line, "Unterminated string");
      return;
    }

    // consume the closing "
    advance();

    // trim the quotes away from the value
    String value = source.substring(start + 1, current -1);
    addToken(STRING, value);
  }
  // check if equal sign exists
  private boolean match(char expected){
    if(isAtEnd()) return false;
    if(source.charAt(current) != expected) return false;

    current++;
    return true;
  }

  // check if scanner is at end of file
  private boolean isAtEnd(){
    return current >= source.length();
  }

  // helpers
  private char advance(){
    current++;
    return source.charAt(current - 1);
  }

  private char peek(){
    if(isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char peekNext(){
    if(current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c){
    return (c >= 'a' && c <= 'z') ||
           (c >= 'A' && c <= 'Z') ||
            c == '_';
  }

  private boolean isAlphaNumeric(char c){
    return isAlpha(c) || isDigit(c);
  }
  // check if character is number
  private boolean isDigit(char c){
    return c >= '0' && c <= '9';
  }

  private void addToken(TokenType type){
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal){
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}
