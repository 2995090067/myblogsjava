package com.zy.common.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义返回消息
 */
@Data
public class Result implements Serializable {
    private int code;//自定义返回200-500X
    private String msg;//自定义返回消息
    private Object data; //返回数据

//     返回的静态方法
    public static Result success(Object data){
        return success(200,"操作成功!git...",data);
    }

//     操作成功
    public static Result success(int code,String msg,Object data){
        Result r=new Result();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
//    操作失败的
    public static Result finallys(String msg){
        return finallys(400,msg,null);
    }

    public static Result finallys(String msg,Object data){
        return finallys(400,msg,data);
    }

    public static Result finallys(int code,String msg,Object data){
        Result r=new Result();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
}
