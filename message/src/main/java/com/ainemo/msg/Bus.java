package com.ainemo.msg;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

/**
 * Created by tony on 1/21/16.
 */
public class Bus {
    private static final String TAG = "Bus";
    private static final Logger logger = Logger.getLogger(TAG);
    private final ConcurrentHashMap<Integer, Set<MsgHandler>> handlers = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, Msg> stickyMsg = new ConcurrentHashMap<>();

    public void register(MsgHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("register handler cannot be null");
        }

        int[] interests = handler.getInterests();
        if (interests != null) {
            for (Integer interest : interests) {
                Set<MsgHandler> msgHandlers = handlers.get(interest);
                if (msgHandlers == null) {
                    CopyOnWriteArraySet<MsgHandler> created = new CopyOnWriteArraySet<>();
                    msgHandlers = handlers.putIfAbsent(interest, msgHandlers);
                    if (msgHandlers == null) {
                        msgHandlers = created;
                    }
                }
                msgHandlers.add(handler);
            }

            for (Integer interest : interests) {
                Msg msg = stickyMsg.get(interest);
                if (msg != null) {
                    handler.onMsg(msg);
                }
            }
        } else {
            throw new IllegalArgumentException("register handler which has no interest");
        }
    }

    public void unregister(MsgHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("unregister handler cannot be null");
        }

        int[] interests = handler.getInterests();
        if (interests == null) {
            for (Integer interest : interests) {
                Set<MsgHandler> msgHandlers = handlers.get(interest);
                if (msgHandlers != null) {
                    msgHandlers.remove(handler);
                }
            }
        } else {
            throw new IllegalArgumentException("unregister handler which has no interest");
        }
    }

    private void post(Msg msg) {
        post(msg, false);
    }

    public void postSticky(Msg msg) {
        post(msg, true);
    }

    private void post(Msg msg, boolean stick) {
        Integer what = msg.what();
        Set<MsgHandler> msgHandlers = handlers.get(what);
        if (msgHandlers != null && !msgHandlers.isEmpty()) {
            for (MsgHandler h : msgHandlers) {
                h.onMsg(msg);
            }
        } else {
            if (!stick) {
                logger.warning("post: dead event: " + msg);
            }
        }

        if (stick) {
            stickyMsg.put(what, msg);
        }
    }
}
