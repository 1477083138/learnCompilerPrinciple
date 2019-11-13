package com.haocoder.demo;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LiuHao
 * @date 2019/11/12 17:00
 * @description 词法分析
*/
public class LexicalAnalysis {
    /**
     * 主方法
     * @param args
     */
    public static void main(String args[]){
        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
        String code1 = "int a = 10";
        String code2 = "inta = 19;";
        String code3 = "int a = 1+2*3-4/5";
        String code4 = "int age >= 10";
        String code5 = "int age > 10";
        List<Token> tokens1 = lexicalAnalysis.tokenize(code1);
        List<Token> tokens2 = lexicalAnalysis.tokenize(code2);
        List<Token> tokens3 = lexicalAnalysis.tokenize(code3);
        List<Token> tokens4 = lexicalAnalysis.tokenize(code4);
        List<Token> tokens5 = lexicalAnalysis.tokenize(code5);

        print(code1,tokens1);
        print(code2,tokens2);
        print(code3,tokens3);
        print(code4,tokens4);
        print(code5,tokens5);
    }
    private StringBuffer tokenText = null;//临时保存token的文本
    private List<Token> tokens = null;//存放解析完成的token
    private SimpleToken token = null;//正在解析的token

    /**
     * 初始化有限自动机状态
     * @param ch
     * @return
     */
    public  DfaState initToken(char ch){
        if(tokenText.length() > 0){
            token.text = tokenText.toString();
            tokens.add(token);
            //在开始解析的时候，进入初始状态，某个Token解析完毕，也进入初始状态，这里先将解析完成的token记录下来，然后建立一个新的token
            tokenText = new StringBuffer();
            token = new SimpleToken();
        }
        DfaState dfaState = DfaState.Initial;//自动机的初始状态
        if(isAlpha(ch)){
            if(ch == 'i'){
                dfaState = DfaState.Id_int1;
            }else{
                dfaState = DfaState.Id;
            }
            token.type = TokenType.Identifier;
            tokenText.append(ch);
        }else if(isDigit(ch)){
            dfaState = DfaState.IntLiteral;
            token.type = TokenType.IntLeteral;
            tokenText.append(ch);
        }else if(ch == '>'){
            dfaState = DfaState.GT;
            token.type = TokenType.GT;
            tokenText.append(ch);
        }else if(ch == '+'){
            dfaState = DfaState.Plus;
            token.type = TokenType.Plus;
            tokenText.append(ch);
        }else if(ch == '-'){
            dfaState = DfaState.Minus;
            token.type = TokenType.Minus;
            tokenText.append(ch);
        }else if(ch == '*'){
            dfaState = DfaState.Star;
            token.type = TokenType.Star;
            tokenText.append(ch);
        }else if(ch == '/'){
            dfaState = DfaState.Slash;
            token.type = TokenType.Slash;
            tokenText.append(ch);
        }else if(ch == ';'){
            dfaState = DfaState.SemiColon;
            token.type = TokenType.SemiColon;
            tokenText.append(ch);
        }else if(ch == '('){
            dfaState = DfaState.LeftParen;
            token.type = TokenType.LeftParen;
            tokenText.append(ch);
        }else if(ch == ')'){
            dfaState = DfaState.RightParen;
            token.type = TokenType.RightParen;
            tokenText.append(ch);
        }else if(ch == '='){
            dfaState = DfaState.Assignment;
            token.type = TokenType.Assignment;
            tokenText.append(ch);
        }else{
            dfaState = DfaState.Initial;
        }
        return dfaState;
    }


    /**
     * 词法分析：解析字符串，形成Token
     * 有限自动状态机，在不同状态中迁移
     * @param code
     * @return
     */
    public List<Token> tokenize(String code){
        tokens = new ArrayList<Token>();
        tokenText = new StringBuffer();
        token = new SimpleToken();
        CharArrayReader reader = new CharArrayReader(code.toCharArray());
        int chPos;
        char ch = 0;
        DfaState dfaState = DfaState.Initial;
        try {
            while ((chPos = reader.read()) != -1) {
                ch = (char) chPos;
                switch (dfaState){
                    case Initial:
                        dfaState = initToken(ch);//确定后续状态
                        break;
                    case Id:
                        if(isAlpha(ch) || isDigit(ch)){
                            tokenText.append(ch);
                        }else{
                            dfaState = initToken(ch);
                        }
                        break;
                    case GT:
                        if(ch == '='){
                            token.type = TokenType.GE;
                            dfaState = DfaState.GE;
                            tokenText.append(ch);
                        }else{
                            dfaState = initToken(ch);
                        }
                        break;
                    case GE:
                    case Assignment:
                    case Plus:
                    case Minus:
                    case Star:
                    case Slash:
                    case SemiColon:
                    case LeftParen:
                    case RightParen:
                        dfaState = initToken(ch);
                        break;
                    case IntLiteral:
                        if(isDigit(ch)){
                            tokenText.append(ch);
                        }else{
                            dfaState = initToken(ch);
                        }
                        break;
                    case Id_int1:
                        if(ch == 'n'){
                            dfaState = DfaState.Id_int2;
                            tokenText.append(ch);
                        }else if(isAlpha(ch) || isDigit(ch)){
                            dfaState = DfaState.Id;
                            tokenText.append(ch);
                        }else{
                            dfaState = initToken(ch);
                        }
                        break;
                    case Id_int2:
                        if(ch == 't'){
                            dfaState = DfaState.Id_int3;
                            tokenText.append(ch);
                        }else if(isAlpha(ch) || isDigit(ch)){
                            dfaState = DfaState.Id;
                            tokenText.append(ch);
                        }else{
                            dfaState = initToken(ch);
                        }
                        break;
                    case Id_int3:
                        if(isBlank(ch)){
                            token.type = TokenType.Int;
                            dfaState = initToken(ch);
                        }else{
                            dfaState = DfaState.Id;
                            tokenText.append(ch);
                        }
                        break;
                     default:
                }
            }
            //将最后一个token送进去(就是将最后一个token加入tokens中）
            if(tokenText.length() > 0){
                initToken(ch);
            }
        }catch (IOException e){
            e.fillInStackTrace();
        }
        return  tokens;
    }

    /**
     * 是否属于字母
     * @param ch
     * @return
     */
    public  boolean isAlpha(char ch){
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    /**
     * 是否属于数字
     * @param ch
     * @return
     */
    public  boolean isDigit(char ch){
        return ch >= '0' && ch <= '9' ;
    }

    /**
     * 是否是空格
     * @param ch
     * @return
     */
    public boolean isBlank(char ch){
        return ch == ' ' || ch == '\t' || ch == '\n';
    }

    /**
     * 有限自动机的所有状态
     * */
    private enum DfaState{
        Initial,Id,GT,GE,IntLiteral,
        Plus,Minus,Star,Slash,
        SemiColon,// ;
        LeftParen,// (
        RightParen,// )
        Assignment,// =
        Id_int1,
        Id_int2,
        Id_int3
    }

    /**
     * 简单Token类型
     */
    private class SimpleToken implements Token{
        public String text;
        public TokenType type;

        public String getText() {
            return text;
        }

        public TokenType getType() {
            return type;
        }

        @Override
        public String toString(){
            return type+"\t"+text;
        }
    }

    /**
     * 打印公式和tokens
     * @param code
     * @param tokens
     */
    public static void print(String code,List<Token> tokens){
        System.out.println("SCRIPT:"+code);
        System.out.println("TOKENS:");
        tokens.stream().forEach(token -> System.out.println(token));
        System.out.println();
    }
}
