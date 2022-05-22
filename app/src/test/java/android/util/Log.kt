@file:JvmName("Log")

package android.util

// Provide an implementation of Log to work in unit tests. A little hacky but...
// Source: https://stackoverflow.com/a/46793567

fun e(tag: String, msg: String, t: Throwable): Int {
    println("ERROR: $tag: $msg")
    return 0
}

fun e(tag: String, msg: String): Int {
    println("ERROR: $tag: $msg")
    return 0
}

fun w(tag: String, msg: String): Int {
    println("WARN: $tag: $msg")
    return 0
}
