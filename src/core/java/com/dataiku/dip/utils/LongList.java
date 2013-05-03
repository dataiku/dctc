package com.dataiku.dip.utils;

import java.util.Iterator;

/**
 * Mutable array of longs
 */
public class LongList implements Iterable<Long>{
    private transient long[] data;
    private int size;

    public LongList(int initialCapacity) {
        this.data = new long[initialCapacity];
    }

    public LongList() {
        this(10);
    }

    /** Copy constructor. Copies the data */
    public LongList(LongList other) {
        this.size = other.size();
        this.data = new long[other.capacity()];
        System.arraycopy(other.getAllData(), 0, data, 0, other.capacity());
    }


    /**
     * Get the raw pointer to all elements.
     * @warning this return all elements in the array, ie more than the size. You must not read past size()
     */
    public long[] getAllData() {
        return data;
    }

    /**
     * Get a copy of valid elements. 
     */
    public long[] getValidDataCopy() {
        long[] newData = new long[size];
        System.arraycopy(newData, 0, data, 0, size);
        return newData;
    }

    /** Get an element, with bounds check */
    public long get(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
        }
        return data[index];
    }

    /** Get an element, without bounds check */
    public long getUnsafe(int index) {
        return data[index];
    }

    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return data.length;
    }

    public void setSize(int newSize) {
        if (newSize > capacity()) {
            throw new IndexOutOfBoundsException("Trying to set size to " + newSize + " but capacity only " + capacity());
        }
        size = newSize;
    }


    /** Sets an element at a given position, with bounds check. @return the old value */
    public long set(int index, long element) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
        }

        long previous = data[index];
        data[index] = element;
        return previous;
    }

    public void add(long o) {
        if (size >= data.length) {
            grow(size + 1);
        }
        data[size++] = o;
    }

    /** Geometric grow */
    public void grow(int minCapacity) {
        int oldCapacity = data.length;
        if (minCapacity > oldCapacity) {
            long[] oldData = data;
            int newCapacity = (oldCapacity * 3)/2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            data = new long[newCapacity];
            System.arraycopy(oldData, 0, data, 0, size);
        }
    }

    public void growExact(int capacity) {
        int oldCapacity = data.length;
        if (oldCapacity >= capacity) return;

        long[] oldData = data;
        data= new long[capacity];
        System.arraycopy(oldData, 0, data, 0, size);
    }

    public void clear() {
        size = 0;
    }

    @Override
    public Iterator<Long> iterator() {
        return new Iterator<Long>() {
            int nextIndex = 0;
            @Override
            public boolean hasNext() {
                return nextIndex < size();
            }

            @Override
            public Long next() {
                return data[nextIndex++];
            }

            @Override
            public void remove() {
            }
        };
    }
}
