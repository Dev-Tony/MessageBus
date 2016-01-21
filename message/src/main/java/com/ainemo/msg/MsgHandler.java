package com.ainemo.msg;

/**
 * Created by tony on 1/21/16.
 */
public interface MsgHandler {

    int[] getInterests();

    void onMsg(Msg msg);
}
