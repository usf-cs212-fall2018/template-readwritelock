import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;

/**
 * A thread-safe version of {@link IndexedSet} using a read/write lock.
 *
 * @param <E>
 *            element type
 * @see IndexedSet
 * @see ReadWriteLock
 */
public class ThreadSafeIndexedSet<E> extends IndexedSet<E> {

	private ReadWriteLock lock;

	public ThreadSafeIndexedSet() {
		this(false);
	}

	public ThreadSafeIndexedSet(boolean sorted) {
		super(sorted);
		lock = new ReadWriteLock();
	}

	@Override
	public boolean add(E element) {
		// TODO
		return false;
	}

	@Override
	public boolean addAll(Collection<E> elements) {
		// TODO
		return false;
	}

	@Override
	public int size() {
		// TODO
		return -1;
	}

	@Override
	public boolean contains(E element) {
		// TODO
		return false;
	}

	@Override
	public E get(int index) {
		// TODO
		return null;
	}

	@Override
	public Set<E> unsortedCopy() {
		// TODO
		return null;
	}

	@Override
	public SortedSet<E> sortedCopy() {
		// TODO
		return null;
	}

	@Override
	public String toString() {
		lock.lockReadOnly();

		try {
			return super.toString();
		}
		finally {
			lock.unlockReadOnly();
		}
	}	
}
