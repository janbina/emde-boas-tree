package com.janbina.vebtree

fun main(args: Array<String>) {

    val tree = VebTree<Long>(8)

    assert(tree.max() == null)
    assert(tree.min() == null)
    assert(tree.successor(10) == null)

    tree.insert(20, 1000)

    assert(tree.max()!!.first == 20)
    assert(tree.max()!!.second == 1000L)

    assert(tree.min()!!.first == 20)
    assert(tree.min()!!.second == 1000L)

    assert(tree.successor(19)!!.first == 20)
    assert(tree.successor(19)!!.second == 1000L)

    assert(tree.successor(20) == null)
}