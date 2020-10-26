package com.company;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;


public class Main {

    private static final String input_file = "1.js";
    private static String textProgram;

    private static String readUsingFiles(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }


    public static void main(String[] args) throws IOException {

        textProgram = readUsingFiles(input_file);
        System.out.println(textProgram);

        Analyzer analyzer = new Analyzer();
        analyzer.Analysis(textProgram);


        ColorText colorText = new ColorText();


        for (Lex lex:analyzer.Lexemes) {
            switch(lex.id){
                case 1:
                    System.out.println(colorText.ANSI_YELLOW + "<" + lex.val + ">" + " | " + " <key word>");
                    break;
                case 2:
                    System.out.println(colorText.ANSI_BLUE + "<" + lex.val + ">" + " | " + " <delimiter>");
                    break;
                case 3:
                    System.out.println(colorText.ANSI_GREEN + "<" + lex.val + ">" + " | " + " <num>");
                    break;
                case 4:
                    System.out.println(colorText.ANSI_CYAN + "<" + lex.val + ">" + " | " + " <identifier>");
                    break;
                case 5:
                    System.out.println(colorText.ANSI_PURPLE + "<" + lex.val + ">" + " | " + " <comment>");
                    break;
                case 6:
                    System.out.println(colorText.ANSI_RESET + "<" + lex.val + ">" + " | " + " <operator>");
                    break;
                case 7:
                    System.out.println(colorText.ANSI_WHITE + "<" + lex.val + ">" + " | " + " <const string>");
                    break;
                case 8:
                    System.out.println(colorText.ANSI_WHITE + "<" + lex.val + ">" + " | " + " <const char>");
                    break;
                case 9:
                    System.out.println(colorText.ANSI_RED + "<" + lex.val + ">" + " | " + " <error>");
                    break;
                case 10:
                    System.out.println(colorText.ANSI_GREEN + "<" + lex.val + ">" + " | " + " <hex num>");
                    break;
                case 11:
                    System.out.println(colorText.ANSI_GREEN + "<" + lex.val + ">" + " | " + "<directive>");
                    break;

            }

        }

    }
}
