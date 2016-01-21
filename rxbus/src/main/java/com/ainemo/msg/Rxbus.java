package com.ainemo.msg;

import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * Created by tony on 1/21/16.
 */
public class Rxbus {
    private static final String TAG = "Rxbus";

    private final ConcurrentHashMap<Integer, Subject<Msg, Msg>> mSubjects = new ConcurrentHashMap<>();

    public Observable<Msg> getObservable(Integer interest) {
        if (interest == null) {
            throw new IllegalArgumentException("register interest is null");
        }
        return getObservableInternal(interest);
    }

    private Subject<Msg, Msg> getObservableInternal(Integer interest) {
        Subject<Msg, Msg> subject = mSubjects.get(interest);
        if (subject == null) {
            BehaviorSubject<Msg> created = BehaviorSubject.create();
            subject = mSubjects.putIfAbsent(interest, created);
            if (subject == null) {
                subject = created;
            }
        }
        return subject;
    }

    public void post(Msg msg) {
        int what = msg.what();
        Subject<Msg, Msg> subject = getObservableInternal(what);
        subject.onNext(msg);
    }
}
