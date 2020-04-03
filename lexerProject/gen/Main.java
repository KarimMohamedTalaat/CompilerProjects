import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        String inputFilePath = "", outputFilePath = "";

        // asking for the input file path :
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter the input file path (testCases/inputFileName.cl) ..");
        inputFilePath = reader.nextLine();

        // generate the output file name.
        outputFilePath = inputFilePath.substring(inputFilePath.indexOf("/") + 1);
        outputFilePath = outputFilePath.substring(0, outputFilePath.indexOf("."));
        outputFilePath = "output/"+outputFilePath;

        // read all input file contents as CharStream object, then send it to the lexer instance.
        CharStream input = CharStreams.fromFileName(inputFilePath);
        Cool_lexer lexer = new Cool_lexer(input);

        // lexer instance extracted tokens found in the input file.
        // CommonTokenStream divides token streams into tokens to be able to access each single token.
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // tokens.fill() -> gets all tokens from lexer to EOF()
        // getTokens() -> Given a start and stop index, return a List of all tokens, null if no found.
        tokens.fill();
        List <Token> allTokens = tokens.getTokens();

        // to detect if there is an error found in lexemes.
        // if there is an error in any token, no output file will be created for the input file.
        Boolean err = false ;
        System.out.println();
        for (int i = 0 ; i < allTokens.size() ; i++){
            // type = 49, this indicates an error ->
            // in Cool_lexer.java you can find a final variable (ERROR=49).
            if (allTokens.get(i).getType() == 49) {
                err = true ;
                System.out.println("ERROR, Character "
                        +allTokens.get(i).getText()
                        + " is invalid in line "
                        +allTokens.get(i).getLine());
            }
        }
        if(!err){
            writeLexerOutput(outputFilePath+".cl-lex",allTokens, allTokens.size());
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
                                "\nType : " + Cool_lexer.getTokenName(tokens.get(noOfLines-i).getType()) +
                                //", Type : " + tokens.get(noOfLines-i).getType() +
                                "\nValue : " + tokens.get(noOfLines-i).getText() ;
                br.write(dataWithNewLine);
                br.newLine();
                dataWithNewLine = "" ;
            }
            System.out.println("\n**Lexical analysis is done, tokens file was generated in the output directory.**");
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
