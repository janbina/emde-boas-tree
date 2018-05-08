package com.janbina.vebtree

class VebTree<E>(private val k: Int) : Veb<E> {

    private val u = 1 shl k
    private val clusters: Array<Veb<E>>
    private val summary: Veb<Boolean>

    private var min: Pair<Int, E>? = null
    private var max: Pair<Int, E>? = null

    init {
        val subtreeCount = k
        val subtreeSize = k / 2
        if (subtreeSize == 1) {
            clusters = Array(subtreeCount, { VebNode<E>() })
            summary = VebNode()
        } else {
            clusters = Array(subtreeCount, { VebTree<E>(subtreeSize) })
            summary = VebTree(subtreeSize)
        }
    }

    override fun insert(key: Int, value: E) {
        if (!isKeyInRange(key)) {
            throw IllegalArgumentException()
        }

        val lMin = min
        val lMax = max
        val x = key to value

        if (lMin == null) {
            min = x
            max = x
            return
        }

        // now lMax must be also nonnull
        lMax!!

        if (key < lMin.key) {
            min = x
            insert(lMin.key, lMin.value)
            return
        }

        if (key == lMin.key) {
            min = x;
            return
        }

        if (key >= lMax.key) {
            max = x
        }

        if (clusters[high(key)].min() == null) {
            summary.insert(high(key), true)
        }

        clusters[high(key)].insert(low(key), value)
    }

    override fun delete(key: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun min() = min

    override fun max() = max

    override fun successor(key: Int): Pair<Int, E>? {
        val lMin = min

        if (lMin == null || key < lMin.key) {
            return min
        }

        val clusterMax = clusters[high(key)].max()

        if (clusterMax != null && key < clusterMax.key) {
            return clusters[high(key)].successor(low(key))
        }

        val targetCluster = summary.successor(high(key))?.key ?: return null

        return clusters[targetCluster].min()
    }

    override fun predecessor(key: Int): Pair<Int, E>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun index(high: Int, low: Int) = (high shl (k / 2)) + low

    private fun low(key: Int) = key and ((1 shl (k / 2)) - 1)

    private fun high(key: Int) = key ushr (k / 2)

    private fun isKeyInRange(key: Int) = key in (0 until u)

    private val <A, B> Pair<A, B>.key: A
        get() = first

    private val <A, B> Pair<A, B>.value: B
        get() = second
}