package com.janbina.vebtree

import org.junit.Test
import org.junit.Assert.*
import kotlin.reflect.KClass


class Tests {

    @Test
    fun testTreeCreation() {
        val tree = VebTree<Long>(256)

        // tree is empty
        assertNull(tree.max())
        assertNull(tree.min())

        // finding successors returns null
        assertNull(tree.successor(10))
        // finding predecessor returns null
        assertNull(tree.predecessor(10))

        // requesting largest possible tree is OK
        val tree2 = VebTree<Long>(65536)
        tree2.insert(65535, 0)

        // requesting too large tree throws
        testException(IllegalArgumentException::class) {
            VebTree<Long>(65536 + 1)
        }
    }

    @Test
    fun testTreeBounds() {
        val treeSize = 256
        val tree = VebTree<Int>(treeSize)

        // test that insertion of invalid values fails
        testException(IllegalArgumentException::class) { tree.insert(-1, 0) }
        testException(IllegalArgumentException::class) { tree.insert(treeSize, 0) }

        // test insertion of minimal possible key
        tree.insert(0, 1)
        testNonNullAndValue(tree.min(), 0, 1)

        // test insertion if maximal possible key
        tree.insert(treeSize - 1, 1)
        testNonNullAndValue(tree.max(), treeSize - 1, 1)
    }

    @Test
    fun testInsertion() {
        val tree = VebTree<Int>(256)

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
        val tree = VebTree<Int>(256)

        tree.insert(20, 1000)

        // test that successor finds inserted value
        testNonNullAndValue(tree.successor(19), 20, 1000)

        // test that successor returns null for key >= max.key
        assertNull(tree.successor(20))
        assertNull(tree.successor(30))
    }

    @Test
    fun testPredecessor() {
        val tree = VebTree<Int>(256)

        tree.insert(20, 1000)

        // test that predecessor finds inserted value
        testNonNullAndValue(tree.predecessor(21), 20, 1000)

        // test that predecessor returns null for key <= max.key
        assertNull(tree.predecessor(20))
        assertNull(tree.predecessor(10))
    }

    @Test
    fun testFullTree() {
        val treeSize = 256
        val tree = VebTree<Int>(treeSize)

        val minKey = 0
        val maxKey = treeSize - 1

        for (i in minKey..maxKey) {
            tree.insert(i, i * i)
        }

        // Test start -> end traversal using successor
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


        // Test end -> start traversal using predecessor
        current = tree.max()
        currentKey = maxKey
        testNonNullAndValue(current, currentKey, currentKey * currentKey)
        while (true) {
            current = tree.predecessor(currentKey)
            currentKey--
            if (currentKey < minKey) {
                assertNull(current)
                break
            }

            testNonNullAndValue(current, currentKey, currentKey * currentKey)
        }
    }

    @Test
    fun testDeletion() {
        val tree = VebTree<Int>(256)

        // Insert and delete one value, check that tree is empty
        tree.insert(20, 20)
        testNonNullAndValue(tree.successor(19), 20, 20)
        tree.delete(20)
        assertNull(tree.min())
        assertNull(tree.max())
        assertNull(tree.successor(19))

        // Insert and delete multiple values, check that values are actually deleted and min/max updated correctly
        for (i in 0..20) {
            tree.insert(i, i * i)
        }
        for (i in 0..19) {
            tree.delete(i)
            testNonNullAndValue(tree.min(), i + 1, (i + 1) * (i + 1))
            testNonNullAndValue(tree.successor(i - 1), i + 1, (i + 1) * (i + 1))
            assertNull(tree.predecessor(i + 1))
        }
        testNonNullAndValue(tree.min(), 20, 20 * 20)
        testNonNullAndValue(tree.max(), 20, 20 * 20)
        tree.delete(20)
        assertNull(tree.min())
        assertNull(tree.max())

        for (i in 0..20) {
            tree.insert(i, i * i)
        }
        for (i in 20 downTo 1) {
            tree.delete(i)
            testNonNullAndValue(tree.max(), i - 1, (i - 1) * (i - 1))
            testNonNullAndValue(tree.predecessor(i + 1), i - 1, (i - 1) * (i - 1))
            assertNull(tree.successor(i - 1))
        }
        testNonNullAndValue(tree.min(), 0, 0 * 0)
        testNonNullAndValue(tree.max(), 0, 0 * 0)
        tree.delete(0)
        assertNull(tree.min())
        assertNull(tree.max())

        // delete from the center of a block
        for (i in 0..20) {
            tree.insert(i, i * i)
        }
        val center = 10
        tree.delete(center)
        for (i in 1..9) {
            tree.delete(center + i)
            tree.delete(center - i)
            testNonNullAndValue(tree.predecessor(center), (center - i - 1), (center - i - 1) * (center - i - 1))
            testNonNullAndValue(tree.successor(center), (center + i + 1), (center + i + 1) * (center + i + 1))
            testNonNullAndValue(tree.min(), 0, 0)
            testNonNullAndValue(tree.max(), 20, 20 * 20)
        }
        tree.delete(0)
        testNonNullAndValue(tree.min(), 20, 20 * 20)
        testNonNullAndValue(tree.max(), 20, 20 * 20)
        tree.delete(20)
        assertNull(tree.min())
        assertNull(tree.max())
    }

    private fun <E> testNonNullAndValue(actual: VebTree.Node<E>?, expectedKey: Int, expectedValue: E) {
        assertNotNull(actual)
        actual!!
        assertEquals(expectedKey, actual.key)
        assertEquals(expectedValue, actual.value)
    }

    private fun testException(expectedClass: KClass<out Exception> = Exception::class, action: () -> Unit) {
        try {
            action()
            fail("Exception should have been thrown but was not")
        } catch (e : Exception) {
            if (expectedClass != Exception::class && e::class != expectedClass) {
                fail("Exception class should have been $expectedClass, but was ${e::class}")
            }
        }
    }
}