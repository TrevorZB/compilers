import java.util.*;
import java.io.*;
import java_cup.runtime.*;  // defines Symbol

/**
 * This program is to be used to test the C-- scanner.
 * This version is set up to test all tokens, but you are required to test 
 * other aspects of the scanner (e.g., input that causes errors, character 
 * numbers, values associated with tokens)
 */
public class P2 {
    public static void main(String[] args) throws IOException {
                                           // exception may be thrown by yylex
        // test all tokens
        testAllTokens();
        CharNum.num = 1;
    
        // ADD CALLS TO OTHER TEST METHODS HERE
        // testing of various string literals
        testValidStringLiterals(); 
        CharNum.num = 1;

        // testing of error messages from invalid string literals
        testInvalidStringLiterals(); 
        CharNum.num = 1;

        // testing invalid int literals and invalid identifiers
        testInvalidIntLiterals();
        CharNum.num = 1;

        // testing of outputted char and line nums
        testCharLineNums();
        CharNum.num = 1;

        // testing identifiers and comments
        testIDComments();
        CharNum.num = 1;

    }

    /**
     * testIDComments
     *
     * Open and read from file testIDComments.in
     * Write tokens to testIDComments.out
     * Compare outputted token values in testIDComments.out
     * to the expected values in testIDCommentsExpected.txt.
     * Using diff should result in these two files being identical.
     */
    private static void testIDComments() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("testIDComments.in");
            outFile = new PrintWriter(new FileWriter("testIDComments.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File testIDComments.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("testIDComments.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
                case sym.ID:
                    outFile.println(((IdTokenVal)token.value).idVal);
                break;
                default:
                    outFile.println("UNKNOWN TOKEN");
            }
            token = scanner.next_token();
        }
        outFile.close();
    }

    /**
     * testCharLineNums
     *
     * Open and read from file charLineNums.in
     * Write line nums and char nums to charLineNums.out
     * Compare outputted line nums and char nums values in charLineNums.out
     * to the expected num values in charLineNumsExpected.txt.
     * Using diff should result in these two files being identical.
     */
    private static void testCharLineNums() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("charLineNums.in");
            outFile = new PrintWriter(new FileWriter("charLineNums.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File charLineNums.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("charLineNums.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
                case sym.STRUCT: // tests parent class TokenVal
                    outFile.println("Linenum: " + ((TokenVal)token.value).linenum); 
                    outFile.println("Charnum: " + ((TokenVal)token.value).charnum);
                    outFile.println("");
                break;
                case sym.STRINGLITERAL:  // tests child class StrLitTokenVal
                    outFile.println("Linenum: " + ((StrLitTokenVal)token.value).linenum);
                    outFile.println("Charnum: " + ((StrLitTokenVal)token.value).charnum);
                    outFile.println("");
                break; 
                default:
                    outFile.println("UNKNOWN TOKEN");
            }
            token = scanner.next_token();
        }
        outFile.close();
    }

    /**
     * testInvalidStringLiterals
     *
     * Open and read bad string literals from file invalidStrLits.in
     * Write errors to invalidStrLits.out
     * Compare outputted errors in invalidStrLits.out to the expected
     * error messages in invalidStrLitsExpected.txt. Using diff should
     * result in these two files being identical.
     */
    private static void testInvalidStringLiterals() throws IOException {
        // open input and output files
        FileReader inFile = null;
        File outFile = null;
        PrintStream oldErr = null;
        try {
            inFile = new FileReader("invalidStrLits.in");
            outFile = new File("invalidStrLits.out");
            FileOutputStream fos = new FileOutputStream(outFile);
            // save original stderr location
            oldErr = System.err;
            // send error messages for this testing function to the invalidStrLits.out file
            System.setErr(new PrintStream(fos));
        } catch (FileNotFoundException ex) {
            System.err.println("File invalidStrLits.in not found.");
            System.exit(-1);
        }

        // iterate through tokens, this will produce error messages for bad string literals
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            token = scanner.next_token();
        }

        // set error output back to error stream
        System.setErr(oldErr);
    }

    /**
     * testInvalidIntLiterals
     *
     * Open and read bad int literals and identifiers from file invalidIntLits.in
     * Write errors to invalidIntLits.out
     * Compare outputted errors in invalidIntLits.out to the expected
     * error messages in invalidIntLitsExpected.txt. Using diff should
     * result in these two files being identical.
     */
    private static void testInvalidIntLiterals() throws IOException {
        // open input and output files
        FileReader inFile = null;
        File outFile = null;
        PrintStream oldErr = null;
        try {
            inFile = new FileReader("invalidIntLits.in");
            outFile = new File("invalidIntLits.out");
            FileOutputStream fos = new FileOutputStream(outFile);
            // save original stderr location
            oldErr = System.err;
            // send error messages for this testing function to the invalidIntLits.out file
            System.setErr(new PrintStream(fos));
        } catch (FileNotFoundException ex) {
            System.err.println("File invalidIntLits.in not found.");
            System.exit(-1);
        }

        // iterate through tokens, this will produce error messages for bad int literals
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
                case sym.INTLITERAL:  
                    System.err.println(((IntLitTokenVal)token.value).intVal);
                break;
                case sym.ID:
                    System.err.println(((IdTokenVal)token.value).idVal);
                break;
            }
            token = scanner.next_token();
        }

        // set error output back to error stream
        System.setErr(oldErr);
    }

    /**
     * testValidStringLiterals
     *
     * Open and read from file strLits.in
     * Write string literal token values to strLits.out
     * All tokens are variations of valid string literals. The diff command
     * should result in the output file being identical to the input file.
     */
    private static void testValidStringLiterals() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("strLits.in");
            outFile = new PrintWriter(new FileWriter("strLits.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File strLits.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("strLits.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
                case sym.STRINGLITERAL: 
                    outFile.println(((StrLitTokenVal)token.value).strVal);
                break; 
                default:
                    outFile.println("UNKNOWN TOKEN");
            }
            token = scanner.next_token();
        }
        outFile.close();
    }

    /**
     * testAllTokens
     *
     * Open and read from file allTokens.txt
     * For each token read, write the corresponding string to allTokens.out
     * If the input file contains all tokens, one per line, we can verify
     * correctness of the scanner by comparing the input and output files
     * (e.g., using a 'diff' command).
     */
    private static void testAllTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allTokens.in");
            outFile = new PrintWriter(new FileWriter("allTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File allTokens.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("allTokens.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
            case sym.BOOL:
                outFile.println("bool"); 
                break;
            case sym.INT:
                outFile.println("int");
                break;
            case sym.VOID:
                outFile.println("void");
                break;
            case sym.TRUE:
                outFile.println("true"); 
                break;
            case sym.FALSE:
                outFile.println("false"); 
                break;
            case sym.STRUCT:
                outFile.println("struct"); 
                break;
            case sym.CIN:
                outFile.println("cin"); 
                break;
            case sym.COUT:
                outFile.println("cout");
                break;				
            case sym.IF:
                outFile.println("if");
                break;
            case sym.ELSE:
                outFile.println("else");
                break;
            case sym.WHILE:
                outFile.println("while");
                break;
            case sym.RETURN:
                outFile.println("return");
                break;
            case sym.ID:
                outFile.println(((IdTokenVal)token.value).idVal);
                break;
            case sym.INTLITERAL:  
                outFile.println(((IntLitTokenVal)token.value).intVal);
                break;
            case sym.STRINGLITERAL: 
                outFile.println(((StrLitTokenVal)token.value).strVal);
                break;    
            case sym.LCURLY:
                outFile.println("{");
                break;
            case sym.RCURLY:
                outFile.println("}");
                break;
            case sym.LPAREN:
                outFile.println("(");
                break;
            case sym.RPAREN:
                outFile.println(")");
                break;
            case sym.SEMICOLON:
                outFile.println(";");
                break;
            case sym.COMMA:
                outFile.println(",");
                break;
            case sym.DOT:
                outFile.println(".");
                break;
            case sym.WRITE:
                outFile.println("<<");
                break;
            case sym.READ:
                outFile.println(">>");
                break;				
            case sym.PLUSPLUS:
                outFile.println("++");
                break;
            case sym.MINUSMINUS:
                outFile.println("--");
                break;	
            case sym.PLUS:
                outFile.println("+");
                break;
            case sym.MINUS:
                outFile.println("-");
                break;
            case sym.TIMES:
                outFile.println("*");
                break;
            case sym.DIVIDE:
                outFile.println("/");
                break;
            case sym.NOT:
                outFile.println("!");
                break;
            case sym.AND:
                outFile.println("&&");
                break;
            case sym.OR:
                outFile.println("||");
                break;
            case sym.EQUALS:
                outFile.println("==");
                break;
            case sym.NOTEQUALS:
                outFile.println("!=");
                break;
            case sym.LESS:
                outFile.println("<");
                break;
            case sym.GREATER:
                outFile.println(">");
                break;
            case sym.LESSEQ:
                outFile.println("<=");
                break;
            case sym.GREATEREQ:
                outFile.println(">=");
                break;
			case sym.ASSIGN:
                outFile.println("=");
                break;
			default:
				outFile.println("UNKNOWN TOKEN");
            } // end switch

            token = scanner.next_token();
        } // end while
        outFile.close();
    }
}
