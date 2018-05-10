package com.janbina.vebtree

class VebTree<E>(size: Int) : Veb<E> {

    private val k: Int
    private val u: Int
    private val createClusterFunc: () -> Veb<E>
    private val createSummaryFunc: () -> Veb<Boolean>
    private val clusters: Array<Veb<E>?>
    private var summary: Veb<Boolean>?

    private var min: Node<E>? = null
    private var max: Node<E>? = null

    init {
        // The size of the tree must be in form of 2^(2^n), so we will be able
        // to square it all the way down to 2.
        // We now want to check size requested by caller and pick the closest bigger value
        // which satisfies that formula.
        // To stay in Java's Integer, the only possible values for size are {4, 16, 256, 65536},
        // if we will use Long for keys, there would be one extra (4294967296).
        k = when {
            size <= 4 -> 2
            size <= 16 -> 4
            size <= 256 -> 8
            size <= 65536 -> 16
            else -> throw IllegalArgumentException("Size of the tree must not be greater than 65536")
        }

        u = 1 shl k
        val subtreeK = k / 2
        val subtreeSize = 1 shl subtreeK

        if (subtreeK == 1) {
            createClusterFunc = { VebNode() }
            createSummaryFunc = { VebNode() }
            clusters = Array(subtreeSize, { null })
            summary = null
        } else {
            createClusterFunc = { VebTree(subtreeSize) }
            createSummaryFunc = { VebTree(subtreeSize) }
            clusters = Array(subtreeSize, { null })
            summary = null
        }
    }

    override fun insert(key: Int, value: E) {
        if (!isKeyInRange(key)) {
            throw IndexOutOfBoundsException()
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

        if (clusters[keyHigh]?.min() == null) {
            insertToSummary(keyHigh)
        }

        insertToCluster(keyHigh, low(key), value)
    }

    private fun insertToCluster(clusterId: Int, key: Int, value: E) {
        if (clusters[clusterId] == null) {
            clusters[clusterId] = createClusterFunc()
        }
        clusters[clusterId]!!.insert(key, value)
    }

    private fun insertToSummary(key: Int) {
        if (summary == null) {
            summary = createSummaryFunc()
        }
        summary!!.insert(key, true)
    }

    override fun delete(key: Int) {
        if (!isKeyInRange(key)) return

        val lMin = min ?: return

        if (key == lMin.key) {
            val summaryMin = summary?.min()
            if (summaryMin == null) {
                min = null
                max = null
                return
            }

            val newMin = clusters[summaryMin.key]!!.min()!!.recomputeKey(summaryMin.key)
            min = newMin
            deleteAfterMin(newMin.key)
        } else {
            deleteAfterMin(key)
        }
    }

    private fun deleteAfterMin(key: Int) {
        val keyHigh = high(key)

        clusters[keyHigh]?.delete(low(key))

        if (clusters[keyHigh]?.min() == null) {
            // TODO: do we want to delete once allocated tree?
            clusters[keyHigh] = null
            summary?.delete(keyHigh)
        }

        val lMax = max!!

        if (key == lMax.key) {
            val summaryMax = summary?.max()
            if (summaryMax == null) {
                // TODO: do we want to delete once allocated tree?
                summary = null
                max = min
            } else {
                max = clusters[summaryMax.key]!!.max()!!.recomputeKey(summaryMax.key)
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

        val clusterMax = clusters[keyHigh]?.max()?.recomputeKey(keyHigh)

        if (clusterMax != null && key < clusterMax.key) {
            return clusters[keyHigh]?.successor(low(key))?.recomputeKey(keyHigh)
        }

        val targetCluster = summary?.successor(keyHigh)?.key ?: return null

        return clusters[targetCluster]?.min()?.recomputeKey(targetCluster)
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

        val clusterMin = clusters[keyHigh]?.min()?.recomputeKey(keyHigh)

        if (clusterMin != null && key > clusterMin.key) {
            return clusters[keyHigh]?.predecessor(low(key))?.recomputeKey(keyHigh)
                    // if we did not find in target cluster, it is because we are looking for min,
                    // which is not stored alongside other elements
                    ?: clusterMin
        }

        val targetCluster = summary?.predecessor(keyHigh)?.key ?: return min

        return clusters[targetCluster]?.max()?.recomputeKey(targetCluster)
    }

    fun getCapacity() = u

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