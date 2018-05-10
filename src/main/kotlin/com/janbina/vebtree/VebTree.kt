package com.janbina.vebtree

class VebTree<E>(private val k: Int) : Veb<E> {

    private val u = 1 shl k
    private val clusters: Array<Veb<E>>
    private val summary: Veb<Boolean>

    private var min: Node<E>? = null
    private var max: Node<E>? = null

    init {
        val subtreeSize = k / 2
        val subtreeCount = 1 shl subtreeSize
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
        val x = Node(key, value)

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

        val keyHigh = high(key)

        if (clusters[keyHigh].min() == null) {
            summary.insert(keyHigh, true)
        }

        clusters[keyHigh].insert(low(key), value)
    }

    override fun delete(key: Int) {
        val lMin = min ?: return

        if (key == lMin.key) {
            val summaryMin = summary.min()
            if (summaryMin == null) {
                min = null
                max = null
                return
            }

            val newMin = clusters[summaryMin.key].min()!!.recomputeKey(summaryMin.key)
            min = newMin
            deleteAfterMin(newMin.key)
        } else {
            deleteAfterMin(key)
        }
    }

    private fun deleteAfterMin(key: Int) {
        val keyHigh = high(key)

        clusters[keyHigh].delete(low(key))

        if (clusters[keyHigh].min() == null) {
            summary.delete(keyHigh)
        }

        val lMax = max!!

        if (key == lMax.key) {
            val summaryMax = summary.max()
            if (summaryMax == null) {
                max = min
            } else {
                max = clusters[summaryMax.key].max()!!.recomputeKey(summaryMax.key)
            }
        }
    }

    override fun min() = min

    override fun max() = max

    override fun successor(key: Int): Node<E>? {
        val lMin = min

        if (lMin == null || key < lMin.key) {
            return min
        }

        val keyHigh = high(key)

        val clusterMax = clusters[keyHigh].max()?.recomputeKey(keyHigh)

        if (clusterMax != null && key < clusterMax.key) {
            return clusters[keyHigh].successor(low(key))?.recomputeKey(keyHigh)
        }

        val targetCluster = summary.successor(keyHigh)?.key ?: return null

        return clusters[targetCluster].min()?.recomputeKey(targetCluster)
    }

    override fun predecessor(key: Int): Node<E>? {
        val lMin = min

        if (lMin == null || key <= lMin.key) {
            return null
        }

        if (key > max!!.key) {
            return max
        }

        val keyHigh = high(key)

        val clusterMin = clusters[keyHigh].min()?.recomputeKey(keyHigh)

        if (clusterMin != null && key > clusterMin.key) {
            return clusters[keyHigh].predecessor(low(key))?.recomputeKey(keyHigh)
                    // if we did not find in target cluster, it is because we are looking for min,
                    // which is not stored alongside other elements
                    ?: clusterMin
        }

        val targetCluster = summary.predecessor(keyHigh)?.key ?: return min

        return clusters[targetCluster].max()?.recomputeKey(targetCluster)
    }

    private fun index(high: Int, low: Int) = (high shl (k / 2)) + low

    private fun low(key: Int) = key and ((1 shl (k / 2)) - 1)

    private fun high(key: Int) = key ushr (k / 2)

    private fun isKeyInRange(key: Int) = key in (0 until u)

    data class Node<E>(
            val key: Int,
            val value: E
    )

    private fun <E> Node<E>.recomputeKey(high: Int): Node<E> {
        return Node(index(high, key), value)
    }
}