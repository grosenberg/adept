package net.certiv.adept.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import net.certiv.adept.unit.HashMultilist;

public class Utils {

	public static boolean notEmpty(List<? extends Object> elems) {
		return elems != null && !elems.isEmpty();
	}

	/** To initialize from an array {@code int[] { value1, value2, ...} } */
	public static HashSet<Integer> toSet(int[] in) {
		HashSet<Integer> set = new HashSet<>();
		for (int element : in) {
			set.add(element);
		}
		return set;
	}

	/** To initialize from an array {@code int[][] { { key, value1, value2, ...}, ... } } */
	public static HashMultilist<Integer, Integer> toMap(int[][] in) {
		HashMultilist<Integer, Integer> map = new HashMultilist<>();
		for (int[] element : in) {
			int key = element[0];
			for (int jdx = 1; jdx < element.length; jdx++) {
				map.put(key, element[jdx]);
			}
		}
		return map;
	}

	/** Returns the concatenation of the two given arrays. */
	public static <T> T[] concatenate(final T[] a, final T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, result, 0, aLen);
		System.arraycopy(b, 0, result, aLen, bLen);
		return result;
	}

	public static double[] toPrimitiveArray(final List<Double> values) {
		double[] result = new double[values.size()];
		for (int idx = 0; idx < result.length; idx++) {
			result[idx] = values.get(idx).doubleValue();
		}
		return result;
	}

	// ------------------------------------------------
	// For file modification checking

	public static long now() {
		return Date.from(Instant.now()).getTime();
	}

	public static long getLastModified(Path path) {
		try {
			FileTime time = Files.getLastModifiedTime(path);
			return Date.from(time.toInstant()).getTime();
		} catch (IOException e) {
			return 0;
		}
	}
}