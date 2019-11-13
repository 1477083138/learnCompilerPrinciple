package com.haocoder.demo;
/**
 *
 * @author LiuHao
 * @date 2019/11/13 14:00
 * @description token
*/
public interface Token {
    /**
     * 返回token文本内容
     * @return
     */
    String getText();

    /**
     * 返回token类型
     * @return
     */
    TokenType getType();


}
