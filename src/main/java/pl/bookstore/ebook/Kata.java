package pl.bookstore.ebook;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Kata {
    public static void main(String[] args) {
        int[] ints = new int[2];
        ints[1] = 3;
        IntStream.of(ints).forEach(System.out::println);
    }
}
