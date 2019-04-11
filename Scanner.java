package com.interpreter.nex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static TokenType.*;

class Scanner{
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

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
            // a comment goes till the end of the line
            while(peek() != '\n' && !isAtEnd()) advance();
          }
          else{
            addToken(SLASH)
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
      default:
          Nex.error(line, "Unexpected character");
          break;
    }
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

  private void addToken(TokenType type){
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal){
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}
