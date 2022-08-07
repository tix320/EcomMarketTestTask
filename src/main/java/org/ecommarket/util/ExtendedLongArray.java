package org.ecommarket.util;

import java.util.Arrays;

public class ExtendedLongArray {

	private final long[] arr;

	private int index;

	public ExtendedLongArray(int size) {
		this.arr = new long[size];
		this.index = 0;
	}

	public void add(long value) {
		arr[index++] = value;
	}

	public void removeFirstItems(int upToIndex) {
		if (upToIndex == index) {
			index = 0;
		}
		else {
			int newSize = index - upToIndex;
			System.arraycopy(arr, upToIndex, arr, 0, newSize);
			index = newSize;
		}
	}

	public int binarySearch(long value) {
		return Arrays.binarySearch(arr, 0, index, value);
	}

	public int size() {
		return index;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0; i < index; i++) {
			b.append(arr[i]);
			b.append(", ");
		}

		return b.append(']').toString();
	}
}
