package com.sxps.xsps.pojo;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    /** 无数据成功 */
    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        return r;
    }

    /** 携带数据成功（字符串、对象、列表都能放） */
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    /** 失败 */
    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }
}
