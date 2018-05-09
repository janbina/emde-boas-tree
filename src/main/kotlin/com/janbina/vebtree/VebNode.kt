package com.janbina.vebtree

class VebNode<E> : Veb<E> {

    private var min: VebTree.Node<E>? = null
    private var max: VebTree.Node<E>? = null

    override fun insert(key: Int, value: E) {
        if (!isKeyInRange(key)) {
            throw IllegalArgumentException()
        }

        val lMin = min
        val lMax = max
        val x = VebTree.Node(key, value)

        if (lMin == null) {
            min = x
            max = x
            return
        }

        // now lMax must be also nonnull
        lMax!!

        if (lMin.key == key && lMax.key == key) {
            min = x
            max = x
        } else if (key == 0) {
            min = x
        } else if (key == 1) {
            max = x
        }
    }

    override fun delete(key: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun min() = min

    override fun max() = max

    override fun successor(key: Int): VebTree.Node<E>? {
        // this can succeed only if we are looking for successor of 0 and our max is nonnull with key 1
        val lMax = max
        if (key == 0 && lMax != null && lMax.key == 1) {
            return max
        }
        return null
    }

    override fun predecessor(key: Int): VebTree.Node<E>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun isKeyInRange(key: Int) = (key == 0 || key == 1)

    private val <A, B> Pair<A, B>.key: A
        get() = first

    private val <A, B> Pair<A, B>.value: B
        get() = second
}