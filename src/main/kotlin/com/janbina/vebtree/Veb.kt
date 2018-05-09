package com.janbina.vebtree

interface Veb<E> {
    fun insert(key: Int, value: E)
    fun delete(key: Int)
    fun min(): VebTree.Node<E>?
    fun max(): VebTree.Node<E>?
    fun successor(key: Int): VebTree.Node<E>?
    fun predecessor(key: Int): VebTree.Node<E>?
}