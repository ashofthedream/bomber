package ashes.of.trebuchet.manager.utils;


public class ArrayUtils {

    /**
     * Swap element from second index to first in array
     *
     * @param a     first index
     * @param b     second index
     * @param array array
     */
    public static void swap(int a, int b, int... array) {
        checkBounds(a, b, array.length);

        int temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }

    /**
     * Swap element from second index to first in array
     *
     * @param a     first index
     * @param b     second index
     * @param array array
     */
    public static void swap(int a, int b, long... array) {
        checkBounds(a, b, array.length);

        long temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }


    /**
     * Swap element from second index to first in array
     *
     * @param a     first index
     * @param b     second index
     * @param array array
     */
    public static void swap(int a, int b, double... array) {
        checkBounds(a, b, array.length);

        double temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }

    /**
     * Swap element from second index to first in array
     *
     * @param a     first index
     * @param b     second index
     * @param array array
     */
    public static <T> void swap(int a, int b, T... array) {
        checkBounds(a, b, array.length);

        T temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }


    private static void checkBounds(int a, int b, int length) {
        if (a >= length || b >= length)
            throw new IllegalArgumentException();
    }

}
