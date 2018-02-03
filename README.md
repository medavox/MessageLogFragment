# MessageLogFragment
Provides a Fragment which lists all the messages you send it, from any thread or class.

# Installing

 Step 1. Add the JitPack repository to your build file
```gradle
allprojects {
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency
```gradle
dependencies {
    compile 'com.github.medavox:messagelogfragment:-SNAPSHOT'
}
```

# How To Use

## 1. Add the `MessageLogFragment` to your layout xml:

```xml
<fragment android:name="com.medavox.message_log_fragment.MessageLogFragment"
    android:id="@+id/message_log"
    android:layout_weight="1"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

## 2. Initialise the Log before posting any messages.

It's recommended that you call `UiLog.init(Context)` this at the earliest possible opportunity
(eg in the onCreate() method of your main activity):

```java
@Override
public void onCreate(Bundle savedInstanceState) {
    //...
    UiLog.init(this);
}
```

UiLog only keeps a [`WeakReference`](https://docs.oracle.com/javase/8/docs/api/index.html?java/lang/ref/WeakReference.html)
to the context object you pass it. 
This avoids memory leaks, but it does mean you'll need to re-initialise it, should the `Context` instance you gave it become `null`.

## 3. Make calls to `UiLog.post(String)` to post messages to your fragment.

```java
UiLog.post("this is a message");
```

This static method can be called from any thread. 

The UiLog class adds the message to a thread-safe 
[`ConcurrentLinkedQueue`](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ConcurrentLinkedQueue.html)
and notifies the MessageLogFragment using a LocalBroadcast when there is new data available.
