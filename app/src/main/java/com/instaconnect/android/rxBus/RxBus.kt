package com.instaconnect.android.rxBus

import android.util.Log
import com.instaconnect.android.rxBus.RxBus
import io.reactivex.subjects.PublishSubject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import java.util.HashMap

class RxBus private constructor() {
    private val TAG = RxBus::class.java.name
    private val sSubjectMap: MutableMap<String, PublishSubject<Any>> = HashMap()
    private val sSubscriptionsMap: MutableMap<Any, CompositeDisposable> = HashMap()

    /**
     * Get the subject or create it if it's not already in memory.
     */
    private fun getSubject(subjectCode: String): PublishSubject<Any> {
        var subject = sSubjectMap[subjectCode]!!
        if (subject == null) {
            subject = PublishSubject.create()
            subject.subscribeOn(AndroidSchedulers.mainThread())
            subject.observeOn(AndroidSchedulers.mainThread())
            sSubjectMap[subjectCode] = subject
        }
        return subject
    }

    /**
     * Get the CompositeSubscription or create it if it's not already in memory.
     */
    private fun getCompositeSubscription(`object`: Any): CompositeDisposable {
        var compositeSubscription = sSubscriptionsMap[`object`]
        if (compositeSubscription == null) {
            compositeSubscription = CompositeDisposable()
            sSubscriptionsMap[`object`] = compositeSubscription
        }
        return compositeSubscription
    }

    /**
     * Subscribe to the specified subject and listen for updates on that subject. Pass in an object to associate
     * your registration with, so that you can unsubscribe later.
     * <br></br><br></br>
     * **Note:** Make sure to call [RxBus.unregister] to avoid memory leaks.
     */
    fun <T> subscribe(
        subject: String,
        lifecycle: Any,
        classType: Class<T>?,
        action: Consumer<in T>
    ): Disposable {
        val disposable = getSubject(subject).ofType(classType).subscribe(action)
        getCompositeSubscription(lifecycle).add(disposable)
        return disposable
    }

    /**
     * Unregisters this object from the bus, removing all subscriptions.
     * This should be called when the object is going to go out of memory.
     */
    fun unregister(lifecycle: Any) {
        //We have to remove the composition from the map, because once you unsubscribe it can't be used anymore
        val compositeSubscription = sSubscriptionsMap.remove(lifecycle)
        compositeSubscription?.clear()
    }

    /**
     * Publish an object to the specified subject for all subscribers of that subject.
     */
    fun publish(subject: String, message: Any) {
        getSubject(subject).onNext(message)
        Log.d(TAG, "publish: subject: $subject message:$message")
    }

    companion object {
        private var rxBus: RxBus? = null
        val instance: RxBus?
            get() {
                if (rxBus == null) rxBus = RxBus()
                return rxBus
            }
    }
}