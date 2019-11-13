package com.haocoder.demo;


/**
 *
 * @author LiuHao
 * @date 2019/11/13 14:06
 * @description  token类型
*/
public enum TokenType {
    GE,// >=
    GT,// >
    EQ,// =
    LE,// <=
    LT,// <

    Plus,// +
    Minus,// -
    Star,// *
    Slash,// /

    SemiColon,// ;
    LeftParen,// (
    RightParen,// )
    Assignment,// =

    Int,
    Identifier,//标识符
    IntLeteral,//数字字面量
    StringLiteral//字符字面量

}
