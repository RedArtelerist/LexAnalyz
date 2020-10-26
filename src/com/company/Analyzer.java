package com.company;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Arrays;


public class Analyzer {
    private final String[] Words = { "var", "const", "if", "else", "for", "while",
            "do", "break", "continue", "true", "false", "null", "switch",
            "case", "function", "return"};
    private final String[] Delimiter = { ".", ";", ",", "(", ")", "[", "]", "\"", "\'", "{", "}"};
    private final String[] Operators = { "+", "-", "*", "/", "%", "=", ">", "<",
                                        "+=", "-=", "*=", "/=", "++", "--", ">=","<=","=="," !=" };

    private final String[] sysHEX = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "a", "b", "c", "d", "e", "f"};
    public ArrayList<Lex> Lexemes = new ArrayList<>();
    private final String[] Directives = {"import"};

    public String[] TID = { "" };
    public String[] TNUM = { "" };


    private String buff = "";

    private final char[] smb = new char[1];
    private String dt;//
    private enum States {S, NUM, DLM, FIN, ID, ER, WITHEQ, PLS, MIN, DIV,
        COM, COM1, COM2, STRCONST, CHARCONST, HEXSYS, DIRECT};
        private States state = States.S;
    private StringReader sr;


    private void GetNext(){
        try{
            sr.read(smb, 0, 1);
        }
        catch (Exception e){
            e.getStackTrace();
        }

    }

    private void ClearBuff(){
        buff = "";
    }

    private void AddBuff(char symb){
        buff += symb;
    }

    private ArrayList SearchLex(String[] lexems){
        for(int i = 0; i < lexems.length; i++){
            if(lexems[i].equals(buff)){
                return new ArrayList(Arrays.asList(i, buff));
            }
        }
        return new ArrayList(Arrays.asList(-1, ""));
    }

    private boolean SearchHexSysLex(String[] lexemes, char smb){
        for(String s: lexemes){
            if(s.toLowerCase().equals(String.valueOf(smb)) || s.toUpperCase().equals(String.valueOf(smb)))
                return true;
        }
        return false;
    }

    public ArrayList PushLex(String[] lexems, String buff){
        for(int i = 0; i < lexems.length; i++){
            if(lexems[i].equals(buff)){
                return new ArrayList(Arrays.asList(-1, ""));
            }
        }
        lexems = Arrays.copyOf(lexems, lexems.length + 1);
        lexems[lexems.length - 1] = buff;
        return new ArrayList(Arrays.asList(lexems.length - 1, buff));
    }

    private void AddLex(ArrayList<Lex> lexemes, int key, int val, String lex){
        lexemes.add(new Lex(key, val, lex));
    }

    public void Analysis(String text){
        sr = new StringReader(text);
        while (state != States.FIN){
            switch(state){
                case S:
                    if(smb[0] == ' ' || smb[0] == '\n' || smb[0] == '\t' || smb[0] == '\0' || smb[0] == '\r'){
                        GetNext();
                    }
                    else if(Character.isLetter(smb[0])){
                        ClearBuff();
                        AddBuff(smb[0]);
                        state = States.ID;
                        GetNext();
                    }
                    else if(Character.isDigit(smb[0])){
                        ClearBuff();
                        AddBuff(smb[0]);
                        dt = String.valueOf(smb[0]);
                        GetNext();
                        state = States.NUM;
                    }
                    else if(smb[0] == '='){
                        state  = States.WITHEQ;
                        ClearBuff();
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else if(smb[0] == '!'){
                        state  = States.WITHEQ;
                        ClearBuff();
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else if(smb[0] == '+'){
                        state  = States.PLS;
                        ClearBuff();
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else if(smb[0] == '-'){
                        state  = States.MIN;
                        ClearBuff();
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else if(smb[0] == '/'){
                        state  = States.DIV;
                        ClearBuff();
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else if(smb[0] == '*'){
                        state  = States.WITHEQ;
                        ClearBuff();
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else if(smb[0] == '>'){
                        state  = States.WITHEQ;
                        ClearBuff();
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else if(smb[0] == '<'){
                        state  = States.WITHEQ;
                        ClearBuff();
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else if(smb[0] == '@'){
                        state = States.FIN;
                    }
                    else{
                        state = States.DLM;
                    }
                    break;
/** ----------------------------------- COM ------------------------------------------------------------------------- **/
                case COM:
                    if(smb[0] == '*'){
                        ClearBuff();
                        AddBuff(smb[0]);
                        state = States.COM1;
                    }
                    GetNext();
                    break;

                case COM1:
                    if(smb[0] == '/'){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 5, 1,  buff);
                        state = States.S;
                        GetNext();
                        break;
                    }
                    state = States.COM;
                    GetNext();
                    break;

                case COM2:
                    if(smb[0] == '\n' || smb[0] == '\0' || smb[0] == '\r'){
                        AddLex(Lexemes, 5, 0, buff);
                        ClearBuff();
                        state = States.S;
                        GetNext();
                        break;
                    }
                    AddBuff(smb[0]);
                    GetNext();
                    break;

/** ----------------------------------- ID ------------------------------------------------------------------------- **/

                case ID:
                    if(Character.isLetterOrDigit(smb[0])){
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else{
                        ArrayList srch = SearchLex(Words);
                        if ((int)srch.get(0) != -1) {
                            AddLex(Lexemes, 1, (int)srch.get(0), (String)srch.get(1));
                        }
                        else {
                            srch = SearchLex(Directives);
                            if((int)srch.get(0) != -1){
                                state = States.DIRECT;
                                AddBuff(smb[0]);
                                break;
                            }
                            else{
                                ArrayList j = PushLex(TID, buff);
                                AddLex(Lexemes, 4, (int) j.get(0), (String) j.get(1));
                            }
                        }
                        state = States.S;
                    }
                    break;
/** ----------------------------------- DIRECT ------------------------------------------------------------------------- **/

                case DIRECT:
                    GetNext();
                    if(smb[0] == '\n' || smb[0] == '\0' || smb[0] == '\r' ||
                            smb[0] ==';'){
                        AddLex(Lexemes, 11, 0, buff);

                        StringBuilder sb = new StringBuilder(text);
                        sb.delete(0, buff.length());
                        String str = sb.toString();
                        sr = new StringReader(str);

                        ClearBuff();
                        state = States.S;
                        GetNext();
                        break;
                    }
                    AddBuff(smb[0]);
                    break;

/** ----------------------------------- NUM ------------------------------------------------------------------------- **/

                case NUM:
                    if(Character.isDigit(smb[0])){
                        dt += smb[0];
                        GetNext();
                    }
                    else if(smb[0] == '.'){
                        dt += '.';
                        GetNext();
                    }
                    else if(smb[0] == 'x' || smb[0] == 'X'){
                        AddBuff(smb[0]);
                        state = States.HEXSYS;
                        GetNext();
                    }
                    else{
                        ArrayList j  = PushLex(TNUM, String.valueOf(dt));
                        AddLex(Lexemes, 3, (int)j.get(0), (String)j.get(1));
                        state = States.S;
                    }
                    break;

/** ----------------------------------- CONST CHAR AND STRING ------------------------------------------------------------------------- **/


                case CHARCONST:
                    if(smb[0] == '\''){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 8, 0, buff);
                        state = States.S;
                        ClearBuff();

                    }
                    AddBuff(smb[0]);
                    GetNext();
                    break;

                case STRCONST:
                    if(smb[0] == '\"'){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 7, 0, buff);
                        state = States.S;
                        ClearBuff();

                    }
                    AddBuff(smb[0]);
                    GetNext();
                    break;


/** ----------------------------------- HEX ------------------------------------------------------------------------- **/

                case HEXSYS:
                    if(SearchHexSysLex(sysHEX, smb[0])){
                        AddBuff(smb[0]);
                        GetNext();
                    }
                    else{
                        if(Character.isLetterOrDigit(smb[0])){
                            AddBuff(smb[0]);
                            state = States.ER;
                        }
                        else{
                            if(buff.startsWith("0x") || buff.startsWith("0X"))
                                AddLex(Lexemes, 10, 0, buff);
                            else
                                AddLex(Lexemes, 9, 0, buff);
                            ClearBuff();
                            state = States.S;
                        }
                    }
                break;

/** ----------------------------------- DLM ------------------------------------------------------------------------- **/

                case DLM:
                    ClearBuff();
                    AddBuff(smb[0]);

                    ArrayList r  = SearchLex(Delimiter);
                    if((int)r.get(0) != -1){
                        if(r.get(1).equals("\"")){
                            state = States.STRCONST;
                            GetNext();
                        }
                        else if(r.get(1).equals("\'")){
                            state = States.CHARCONST;
                            GetNext();
                        }
                        else{
                            AddLex(Lexemes, 2, (int)r.get(0), (String)r.get(1));
                            state = States.S;
                            GetNext();
                        }
                    }
                    else{
                        r = SearchLex(Operators);
                        if((int)r.get(0) != -1){
                            AddLex(Lexemes, 6, (int)r.get(0), (String)r.get(1));
                            state = States.S;
                            GetNext();
                        }
                        else{
                            state = States.ER;
                        }
                    }

                    break;

/** ----------------------------------- OPETATORS WITH =(+=, -=, /=, *=, ++, --) ------------------------------------------------------------------------- **/


                case WITHEQ:
                    if(smb[0] == '='){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 6, 4, buff);
                        ClearBuff();
                        GetNext();
                    }
                    else{
                        AddLex(Lexemes, 6, 4, buff);
                    }
                    state = States.S;
                    break;

                case PLS:
                    if(smb[0] == '='){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 6, 4, buff);
                        ClearBuff();
                        GetNext();
                    }
                    else if(smb[0] == '+'){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 6, 4, buff);
                        ClearBuff();
                        GetNext();
                    }
                    else {
                        AddLex(Lexemes, 6, 4, buff);
                    }
                    state = States.S;
                    break;

                case MIN:
                    if(smb[0] == '='){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 6, 4, buff);
                        ClearBuff();
                        GetNext();
                    }
                    else if(smb[0] == '-'){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 6, 4, buff);
                        ClearBuff();
                        GetNext();
                    }
                    else {
                        AddLex(Lexemes, 6, 4, buff);

                    }
                    state = States.S;
                    break;

                case DIV:
                    if(smb[0] == '='){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 6, 4, buff);
                        ClearBuff();
                        GetNext();
                        state = States.S;
                    }
                    else if(smb[0] == '*'){
                        AddBuff(smb[0]);
                        AddLex(Lexemes, 5, 0, buff);
                        state = States.COM;
                        GetNext();
                    }
                    else if(smb[0] == '/'){
                        AddBuff(smb[0]);
                        state = States.COM2;
                        GetNext();
                    }
                    else {
                        AddLex(Lexemes, 6, 4, buff);
                        state = States.S;
                    }
                    break;

                case ER:
                    GetNext();
                    if(smb[0] == ' ' || smb[0] == '\n' || smb[0] == '\t' || smb[0] == '\0' || smb[0] == '\r'){
                        AddLex(Lexemes, 9, 0, buff);
                        ClearBuff();
                        state = States.S;
                        break;
                    }
                    AddBuff(smb[0]);

                case FIN:
                    break;
            }
        }
    }
}
