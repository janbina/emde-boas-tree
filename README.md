Van Emde Boas tree
==================

Implementation of Van Emde Boas tree structure in Kotlin. The tree stores m-bit integer keys each with associated value of arbitrary type. It performs all operations in O(log log M) time, where M = 2^m is the maximum number of elements that can be stored in the tree. 

Supported operations:
  - **insert(key, value)** - inserts (key, value) pair to the tree. If there was already value with same key in the tree, it will be replaced.
  - **delete(key)** - deletes key and its value from tree
  - **min()** - returns minimal key in the tree with associated value, or null if the tree is empty
  - **max()** - returns maximal key in the tree with associated value, or null if the tree is empty 
  - **successor(key)** - returns minimal key bigger than provided key and its value, null if there is no bigger key in the tree
  - **predecessor(key)** - returns maximal key smaller than provided key and its value, null if there is no smaller key in the tree
