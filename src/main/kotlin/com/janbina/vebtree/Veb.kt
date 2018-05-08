package com.janbina.vebtree

interface Veb<E> {
    fun insert(key: Int, value: E)
    fun delete(key: Int)
    fun min(): Pair<Int, E>?
    fun max(): Pair<Int, E>?
    fun successor(key: Int): Pair<Int, E>?
    fun predecessor(key: Int): Pair<Int, E>?
}