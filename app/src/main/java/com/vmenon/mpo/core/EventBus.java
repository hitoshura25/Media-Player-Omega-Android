package com.vmenon.mpo.core;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class EventBus {
    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object event) {
        bus.onNext(event);
    }

    public Subscription subscribe(Action1<Object> action) {
        return bus.subscribe(action);
    }
}
