package com.janbina.vebtree

/**
 * VebNode is VebTree without any subtrees.
 * There are not any clusters nor summary, just min and max.
 */
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
        val lMin = min ?: return
        val lMax = max ?: return

        if (key == lMin.key) {
            if (key == lMax.key) {
                min = null
                max = null
            } else {
                min = max
            }
        } else if (key == lMax.key) {
            max = min
        }
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
        // this can succeed only if we are looking for predecessor of 1 and our min is nonnull with key 0
        val lMin = min
        if (key == 1 && lMin != null && lMin.key == 0) {
            return min
        }
        return null
    }


    private fun isKeyInRange(key: Int) = (key == 0 || key == 1)
}