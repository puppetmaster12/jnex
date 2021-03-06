package com.interpreter.jnex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Nex
{
    static boolean hadError = false;

    public static void main(String[] args) throws IOException{
      if(args.length > 1){
        System.out.println("Usage: jnex [script]");
        System.exit(64);
      } else if(args.length == 1){
        runFile(args[0]);
      } else {
        runPrompt();
      }
    }

    // Runfile function
    public static void runFile(String path) throws IOException{
      byte[] bytes = Files.readAllBytes(Paths.get(path));
      run(new String(bytes, Charset.defaultCharset()));

      // Indicate error in exit code
      if(hadError) System.exit(65);
    }

    // Interactive loop
    public static void runPrompt() throws IOException{
      InputStreamReader input = new InputStreamReader(System.in);
      BufferedReader reader = new BufferedReader(input);

      for(;;){
        System.out.print("> ");
        run(reader.readLine());
        hadError = false;
      }
    }

    // run function
    private static void run(String source){
      Scanner scanner = new Scanner(source);
      List<Token> tokens = scanner.scanTokens();

      // Print out tokens
      for(Token token : tokens){
        System.out.println(token);
      }
    }

    // Error handling
    static void error(int line, String message){
      report(line, "", message);
    }
    // Error report function
    private static void report(int line, String where, String message){
      System.err.println("[line " + line + "] Error" + where + ": " + message);
    }
}
