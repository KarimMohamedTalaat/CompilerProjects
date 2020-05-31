import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Main {

    // buffer writers for the 2 output files.
    public static BufferedWriter writer1, writer2 ;

    public static void main(String[] args) {

        try{
            // the input names for the test file, output file.
            // "TAC" -> Three Address Code.
            String testFilePath  = "testCases//goodParser.cl";
            String outputFileTAC =    "output//goodParser.cl" + "-TAC";
            String astOutput     =    "output//goodParser.cl" + "-lex";

            // create files with the given names above.
            File file1 = new File(outputFileTAC);
            File file2 = new File(astOutput);
            file1.delete();
            file2.delete();

            // the two output files are ready to be written.
            writer1 = new BufferedWriter(new FileWriter(outputFileTAC, true));
            writer2 = new BufferedWriter(new FileWriter(astOutput, true));

            // run the cool lexer & cool parser for the input cool program with an error listener.
            FileInputStream fis = new FileInputStream(new File(testFilePath));
            ANTLRInputStream input = new ANTLRInputStream(fis);
            lex lexer = new lex(input);

            // lexer instance extracted tokens found in the input file.
            // CommonTokenStream divides token streams into tokens to be able to access each single token.
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);

            // tokens.fill() -> gets all tokens from lexer to EOF()
            // getTokens() -> Given a start and stop index, return a List of all tokens, null if no found.
            tokenStream.fill();
            List <Token> allTokens = tokenStream.getTokens();

            // to detect if there is an error found in lexemes.
            // if there is an error in any token, no output file will be created for the input file.
            Boolean err = false ;
            System.out.println();
            for (int i = 0 ; i < allTokens.size() ; i++){
                // type = 1, this indicates an error ->
                // in lex.java you can find a final variable (ERROR = 1).
                if (allTokens.get(i).getType() == 1) {
                    err = true ;
                    System.out.println("ERROR, Character "
                            +allTokens.get(i).getText()
                            + " is invalid in line "
                            +allTokens.get(i).getLine());
                }
            }

            if(!err){
                writeLexerOutput(astOutput, allTokens, allTokens.size());
            }

            // parser uses tokens.
            parse parser = new parse(tokenStream);
            parser.removeErrorListeners();
            parser.addErrorListener(new ThrowingErrorListener());

            // assign the parser program to the tree to be viewed later (tree = prog).
            parse.ProgramContext prog = parser.program();

            // check if there are any syntax errors to take the decision of viewing the tree or not.
            int NoOfErrors = parser.getNumberOfSyntaxErrors();
            System.out.println("No. of syntax errors = " + NoOfErrors + ".");
            if(NoOfErrors > 0) {
                System.out.println("The syntax tree cannot be viewed due to some syntax errors.");
                System.exit(0);
            }

            // if there are no syntax errors a tree viewer displays the code hierarchical structure as a tree,
            // given the tree itself and rules names.
            TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), prog);
            // opens the JFrame on which the tree was illustrated.
            viewer.open();
            System.out.println("The output of the syntax tree was opened in an external window.");

            writer2.write(prog.value.getString(""));
            prog.value.gen();

            // printing three address code and write it to the output file.
            for(String s: AST.prog3AdCode){
                //System.out.println(s);
                writer1.write(s + "\n");
            }
            System.out.println("Three Address Code file generated in the output directory.");
            writer1.close();
            writer2.close();
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private static void writeLexerOutput(String fileName, List<Token> tokens, int noOfLines) {
        File file = new File(fileName);
        FileWriter fr = null;
        BufferedWriter br = null;
        String dataWithNewLine="" ;
        try{
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);
            for(int i = noOfLines; i>0; i--){
                dataWithNewLine+=
                        "Line : " + tokens.get(noOfLines-i).getLine() +
                                "\nType : " + lex.getTokenName(tokens.get(noOfLines-i).getType()) +
                                //", Type : " + tokens.get(noOfLines-i).getType() +
                                "\nValue : " + tokens.get(noOfLines-i).getText();
                br.write(dataWithNewLine);
                br.newLine();
                dataWithNewLine = "" ;
            }
            System.out.println("Lexical analysis is done, tokens file was generated in the output directory.");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}