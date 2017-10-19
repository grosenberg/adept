package net.certiv.adept.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Multimap implemented as a TreeMap with ArraySet instance values
 *
 * @param <K>
 * @param <V>
 */
public class TreeMultimap<K, V> {

	private final TreeMap<K, ArraySet<V>> map;
	private final Comparator<? super V> valComp;

	public TreeMultimap() {
		this(null, null);
	}

	public TreeMultimap(Comparator<? super K> keyComp) {
		this(keyComp, null);
	}

	public TreeMultimap(Comparator<? super K> keyComp, Comparator<? super V> valComp) {
		this.map = new TreeMap<>(keyComp);
		this.valComp = valComp;
	}

	/** Get as set. */
	public ArraySet<V> get(K key) {
		return map.get(key);
	}

	/** Get as list. */
	public List<V> asList(K key) {
		return new ArrayList<>(map.get(key));
	}

	public boolean put(K key, V value) {
		ArraySet<V> set = map.get(key);
		if (set == null) {
			set = new ArraySet<>(valComp);
			map.put(key, set);
		}
		return set.add(value);
	}

	public boolean put(K key, Collection<V> values) {
		ArraySet<V> set = map.get(key);
		if (set == null) {
			set = new ArraySet<>(valComp);
			map.put(key, set);
		}
		return set.addAll(values);
	}

	public K firstKey() {
		return map.firstKey();
	}

	public K lastKey() {
		return map.lastKey();
	}

	/** Returns {@code true} if a value set for the given key exists. */
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	/** Returns {@code true} if at least one of the value sets contains the given value. */
	public boolean containsValue(V value) {
		return map.containsValue(value);
	}

	/** Returns {@code true} if the value set for the given key contains the given value. */
	public boolean contains(K key, V value) {
		if (!map.containsKey(key)) return false;
		return map.get(key).contains(value);
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Set<V> values() {
		Set<V> results = new ArraySet<>(valComp);
		for (Set<V> set : map.values()) {
			results.addAll(set);
		}
		return results;
	}

	public int keySize() {
		return map.size();
	}

	public int valueSize() {
		return values().size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<V> remove(K key) {
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}
}
