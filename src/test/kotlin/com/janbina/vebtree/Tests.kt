package com.janbina.vebtree

import org.junit.Test
import org.junit.Assert.*
import kotlin.reflect.KClass


class Tests {

    @Test
    fun testTreeCreation() {
        val tree = VebTree<Long>(8)

        // tree is empty
        assertNull(tree.max())
        assertNull(tree.min())

        // finding successors returns null
        assertNull(tree.successor(10))
    }

    @Test
    fun testTreeBounds() {
        val treeSize = 8
        val tree = VebTree<Int>(treeSize)

        // test that insertion of invalid values fails
        testException(IllegalArgumentException::class) { tree.insert(-1, 0) }
        testException(IllegalArgumentException::class) { tree.insert(1 shl treeSize, 0) }

        // test insertion of minimal possible key
        tree.insert(0, 1)
        testNonNullAndValue(tree.min(), 0, 1)

        // test insertion if maximal possible key
        tree.insert((1 shl treeSize) - 1, 1)
        testNonNullAndValue(tree.max(), (1 shl treeSize) - 1, 1)
    }

    @Test
    fun testInsertion() {
        val tree = VebTree<Int>(8)

        tree.insert(20, 1000)

        // test that max exists and its key and value are right
        testNonNullAndValue(tree.max(), 20, 1000)

        // test that min exists and its key and value are right
        testNonNullAndValue(tree.min(), 20, 1000)

        // test that min is updated correctly
        tree.insert(19, 500)
        testNonNullAndValue(tree.min(), 19, 500)
        tree.insert(18, 250)
        testNonNullAndValue(tree.min(), 18, 250)

        //test that max is not affected by previous insertions
        testNonNullAndValue(tree.max(), 20, 1000)

        // test that max is updated correctly
        tree.insert(21, 1500)
        testNonNullAndValue(tree.max(), 21, 1500)
        tree.insert(22, 2000)
        testNonNullAndValue(tree.max(), 22, 2000)

        // test that min is not affected by previous insertions
        testNonNullAndValue(tree.min(), 18, 250)
    }

    @Test
    fun testSuccessor() {
        val tree = VebTree<Int>(8)

        tree.insert(20, 1000)

        // test that successor finds inserted value
        testNonNullAndValue(tree.successor(19), 20, 1000)

        // test that successor returns null for key >= max.key
        assertNull(tree.successor(20))
        assertNull(tree.successor(30))
    }

    @Test
    fun testFullTree() {
        val treeSize = 8
        val tree = VebTree<Int>(treeSize)

        val minKey = 0
        val maxKey = (1 shl treeSize) - 1

        for (i in minKey..maxKey) {
            tree.insert(i, i * i)
        }

        var current = tree.min()
        var currentKey = 0
        testNonNullAndValue(current, currentKey, currentKey * currentKey)

        while (true) {
            current = tree.successor(currentKey)
            currentKey++
            if (currentKey > maxKey) {
                assertNull(current)
                break
            }

            testNonNullAndValue(current, currentKey, currentKey * currentKey)
        }
    }

    private fun <E> testNonNullAndValue(actual: Pair<Int, E>?, expectedKey: Int, expectedValue: E) {
        assertNotNull(actual)
        actual!!
        assertEquals(expectedKey, actual.first)
        assertEquals(expectedValue, actual.second)
    }

    private fun testException(expectedClass: KClass<out Exception> = Exception::class, action: () -> Unit) {
        try {
            action()
            fail("Exception should have been thrown but was not")
        } catch (e : Exception) {
            if (e::class != expectedClass) {
                fail("Exception class should have been $expectedClass, but was ${e::class}")
            }
        }
    }
}