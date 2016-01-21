package com.ainemo.msg;

import android.os.Bundle;

import java.util.LinkedList;

/**
 * Created by tony on 1/21/16.
 */
public final class Msg {
    public static final int POOL_MAX_SIZE = 50;
    private static final LinkedList<Msg> POOL = new LinkedList<>();
    public static final int DEFAULT_INT = Integer.MIN_VALUE;
    private int what;
    private int arg1;
    private int arg2;
    private Object obj;
    private Bundle data;

    private Msg(){}
    private Msg(int what, int arg1, int arg2, Object obj) {
        this.what = what;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.obj = obj;
    }

    public int what() {
        return what;
    }

    public int arg1() {
        return arg1;
    }

    public int arg2() {
        return arg2;
    }

    public Object obj() {
        return obj;
    }

    public Bundle data() {
        if (data == null) {
            data = new Bundle();
        }
        return data;
    }

    void recycle() {
        what = DEFAULT_INT;
        arg1 = DEFAULT_INT;
        arg2 = DEFAULT_INT;
        obj = null;
        data = null;
    }

    public static Msg obtain(int what) {
        return obtain(what, DEFAULT_INT, DEFAULT_INT, null);
    }
    public static Msg obtain(int what, int arg1) {
        return obtain(what, arg1, DEFAULT_INT, null);
    }
    public static Msg obtain(int what, int arg1, Object obj) {
        return obtain(what, arg1, DEFAULT_INT, obj);
    }
    public static Msg obtain(int what, int arg1, int arg2) {
        return obtain(what, arg1, arg2, null);
    }
    public static Msg obtain(int what, Object obj) {
        return obtain(what, DEFAULT_INT, DEFAULT_INT, obj);
    }
    public static Msg obtain(int what, int arg1, int arg2, Object obj) {
        Msg msg;
        synchronized (POOL) {
            msg = POOL.pop();
        }
        if (msg == null) {
            msg = new Msg();
        }
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        return msg;
    }
}
