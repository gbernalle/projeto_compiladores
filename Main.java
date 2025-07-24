import Sintatico.SyntaticAnalysis;
import lexical.LexicalAnalysis;

public class Main {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java file " + args[0]);
      return;
    }
    try (LexicalAnalysis l = new LexicalAnalysis(args[0])) {

      SyntaticAnalysis s = new SyntaticAnalysis(l);
      s.start();

    } catch (Exception e) {
      System.err.println("Internal error: " + e.getMessage());
    }
  }
  
}
