import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Attempts to test the {@link ReadWriteLock} and {@link ThreadSafePathSet}.
 * These tests are not 100% accurate. They attempt to create threads in such a
 * way that problems will occur if the implementation is incorrect, but the
 * tests are inexact.
 */
@RunWith(Suite.class)
@SuiteClasses({ ReadWriteLockTest.LockTest.class, ThreadSafeIndexedSetTest.class })
public class ReadWriteLockTest {

	/** Specifies how long a worker thread should sleep. */
	private static final long WORKER_SLEEP = 1000;

	/**
	 * Specifies how long to wait before starting a new worker. Must be less
	 * than {@link #WORKER_SLEEP}.
	 */
	private static final long OFFSET_SLEEP = (long) (WORKER_SLEEP * 0.5);

	private static final String FORMAT = "Expected:%n%s%nActual:%n%s%n";

	public static class LockTest {
		/**
		 * Tests that two threads are able to simultaneously acquire the read
		 * lock without any exceptions. Should also finish in less than 200
		 * milliseconds if both threads are able to execute simultaneously.
		 *
		 * @throws InterruptedException
		 */
		@Test(timeout = (long) (WORKER_SLEEP * 1.75))
		public void testTwoReaders() throws InterruptedException {

			ReadWriteLock lock = new ReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread reader1 = new Thread(new ReadWorker(lock, buffer));
			Thread reader2 = new Thread(new ReadWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Read Lock\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");
			expected.append("Read Unlock\n");

			reader1.start();
			reader2.start();
			reader2.join();
			reader1.join();

			Assert.assertEquals(String.format(FORMAT, expected, buffer), expected.toString(), buffer.toString());
		}

		/**
		 * Tests that two threads are NOT able to simultaneously acquire the
		 * write lock without any exceptions. Should also finish in over 200
		 * milliseconds if both threads are able to execute simultaneously.
		 *
		 * @throws InterruptedException
		 */
		@Test(timeout = (long) (WORKER_SLEEP * 2.75))
		public void testTwoWriters() throws InterruptedException {
			ReadWriteLock lock = new ReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread writer1 = new Thread(new WriteWorker(lock, buffer));
			Thread writer2 = new Thread(new WriteWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");

			writer1.start();
			writer2.start();
			writer2.join();
			writer1.join();

			Assert.assertEquals(String.format(FORMAT, expected, buffer), expected.toString(), buffer.toString());
		}

		/**
		 * Tests that two threads are NOT able to simultaneously acquire the
		 * read lock and write lock without any exceptions. Should also finish
		 * in over 200 milliseconds if both threads are able to execute
		 * simultaneously.
		 *
		 * @throws InterruptedException
		 */
		@Test(timeout = (long) (WORKER_SLEEP * 2.75))
		public void testReaderWriter() throws InterruptedException {
			ReadWriteLock lock = new ReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread reader = new Thread(new ReadWorker(lock, buffer));
			Thread writer = new Thread(new WriteWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");

			reader.start();

			// wait a little bit before starting next thread
			Thread.sleep(OFFSET_SLEEP);
			writer.start();

			writer.join();
			reader.join();

			Assert.assertEquals(String.format(FORMAT, expected, buffer), expected.toString(), buffer.toString());
		}

		/**
		 * Tests that two threads are NOT able to simultaneously acquire the
		 * read lock and write lock without any exceptions. Should also finish
		 * in over 200 milliseconds if both threads are able to execute
		 * simultaneously.
		 *
		 * @throws InterruptedException
		 */
		@Test(timeout = (long) (WORKER_SLEEP * 2.75))
		public void testWriterReader() throws InterruptedException {
			ReadWriteLock lock = new ReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread writer = new Thread(new WriteWorker(lock, buffer));
			Thread reader = new Thread(new ReadWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");

			writer.start();

			// wait a little bit before starting next thread
			Thread.sleep(OFFSET_SLEEP);
			reader.start();

			reader.join();
			writer.join();

			Assert.assertEquals(String.format(FORMAT, expected, buffer), expected.toString(), buffer.toString());
		}

		/**
		 * Tests that two threads are NOT able to simultaneously acquire the
		 * read lock and write lock without any exceptions, but multiple threads
		 * may acquire read locks (even if a writer is waiting). Should also
		 * finish in over 200 milliseconds if all threads are able to execute
		 * properly.
		 *
		 * @throws InterruptedException
		 */
		@Test(timeout = (long) (WORKER_SLEEP * 2.75))
		public void testMultiReadFirst() throws InterruptedException {
			ReadWriteLock lock = new ReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread reader1 = new Thread(new ReadWorker(lock, buffer));
			Thread reader2 = new Thread(new ReadWorker(lock, buffer));

			Thread writer1 = new Thread(new WriteWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Read Lock\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");
			expected.append("Read Unlock\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");

			reader1.start();
			reader2.start();

			// wait a little bit before starting next thread
			Thread.sleep(OFFSET_SLEEP);
			writer1.start();

			reader2.join();
			writer1.join();
			reader1.join();

			Assert.assertEquals(String.format(FORMAT, expected, buffer), expected.toString(), buffer.toString());
		}

		/**
		 * Tests that two threads are NOT able to simultaneously acquire the
		 * read lock and write lock without any exceptions, but multiple threads
		 * may acquire read locks (even if a writer is waiting). Should also
		 * finish in over 200 milliseconds if all threads are able to execute
		 * properly.
		 *
		 * @throws InterruptedException
		 */
		@Test(timeout = (long) (WORKER_SLEEP * 2.75))
		public void testMultiWriteFirst() throws InterruptedException {
			ReadWriteLock lock = new ReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread writer1 = new Thread(new WriteWorker(lock, buffer));

			Thread reader1 = new Thread(new ReadWorker(lock, buffer));
			Thread reader2 = new Thread(new ReadWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");
			expected.append("Read Lock\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");
			expected.append("Read Unlock\n");

			writer1.start();

			// wait a little bit before starting next thread
			Thread.sleep(OFFSET_SLEEP);
			reader1.start();
			reader2.start();

			reader2.join();
			reader1.join();
			writer1.join();

			Assert.assertEquals(String.format(FORMAT, expected, buffer), expected.toString(), buffer.toString());
		}
	}

	private static class ReadWorker implements Runnable {

		private final StringBuffer buffer;
		private final ReadWriteLock lock;

		public ReadWorker(ReadWriteLock lock, StringBuffer buffer) {
			this.lock = lock;
			this.buffer = buffer;
		}

		@Override
		public void run() {
			lock.lockReadOnly();
			buffer.append("Read Lock\n");

			try {
				Thread.sleep(WORKER_SLEEP);
			}
			catch (Exception ex) {
				buffer.append("Read Error\n");
			}

			buffer.append("Read Unlock\n");
			lock.unlockReadOnly();
		}
	}

	private static class WriteWorker implements Runnable {

		private final StringBuffer buffer;
		private final ReadWriteLock lock;

		public WriteWorker(ReadWriteLock lock, StringBuffer buffer) {
			this.lock = lock;
			this.buffer = buffer;
		}

		@Override
		public void run() {
			lock.lockReadWrite();
			buffer.append("Write Lock\n");

			try {
				Thread.sleep(WORKER_SLEEP);
			}
			catch (Exception ex) {
				buffer.append("Write Error\n");
			}

			buffer.append("Write Unlock\n");
			lock.unlockReadWrite();
		}
	}
}
