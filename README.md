Read Write Lock
=================================================

For this homework assignment, you will create (1) a custom read write lock and (2) a thread safe indexed set using that lock.

See the Javadoc comments in [`ReadWriteLock.java`](src/ReadWriteLock.java) and [`ThreadSafeIndexedSet.java`](src/ThreadSafeIndexedSet.java) for template code and additional details.

## Requirements ##

The official name of this homework is `ReadWriteLock`. This should be the name you use for your Eclipse Java project and the name you use when running the homework test script.

You must pass all unit tests when running the `/home/public/cs212/homework` script on the lab computers to receive a 100% on this homework assignment.

## Hints ##

Below are some hints that may help with this homework assignment:

- Look at the lecture notes for discussion on the custom read write lock.

- Decide whether a block of operations are read operations, write operations, or mixed (and hence need a write lock) **with respect to the shared data**.

- The provided tests are not fool-proof. They try to recreate conditions that will cause issues if your lock or set are incorrectly implemented, but just because the tests pass does not mean your implementations are correct.

You are not required to use these hints in your solution. There may be multiple approaches to solving this homework.
