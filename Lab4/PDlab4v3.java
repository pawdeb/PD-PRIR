import java.util.Arrays;
import java.util.Random;

public class PDlab4v3 {

    public static void main(String[] args) {

        // liczby
        int ile = 100000;
        int max = 100;
        int[] arr = new int[ile];
        Random random = new Random();
        for (int i = 0; i < ile; i++) {
            arr[i] = random.nextInt(max);
        }

        // klasycznie
        long t1 = System.nanoTime();
        bubbleSort(arr);
        long t2 = System.nanoTime();
        long klasycznie = (t2 - t1) / 1000;
        System.out.println("Czas bąbelkowy klasycznie " + klasycznie + " ns dla " + ile + " liczb");

        // równolegle
        long t3 = System.nanoTime();
        Arrays.parallelSort(arr);
        long t4 = System.nanoTime();
        long parallel = (t4 - t3) / 1000;
        System.out.println("\nCzas parallel " + parallel + " ns dla " + ile + " liczb");

//        // bąbel równoległy
//        long t5 = System.nanoTime();
//        // fukncja
//        long t6 = System.nanoTime();
//        long parabab = (t6 - t5) / 1000;
//        System.out.println("\nCzas bąbelkowy równoległy " + parabab + " ns dla " + ile + " liczb");

    }

    public static void bubbleSort(int[] numbers) { /* Żródło https://javarevisited.blogspot.com/2014/08/bubble-sort-algorithm-in-java-with.html#ixzz7FCXjAezg */
        // System.out.printf("Unsorted array in Java :%s %n", Arrays.toString(numbers));
        for (int i = 0; i < numbers.length; i++) {
            for (int j = numbers.length - 1; j > i; j--) {
                if (numbers[j] < numbers[j - 1]) {
                    swap(numbers, j, j - 1);
                }
            }
        }
    }

    public static void swap(int[] array, int from, int to) { /* Żródło https://javarevisited.blogspot.com/2014/08/bubble-sort-algorithm-in-java-with.html#ixzz7FCXjAezg */
        int temp = array[from];
        array[from] = array[to];
        array[to] = temp;
    }

//    public static void ParaBubble(int ile, int[] arr) implements Runnable {
//        int[] result = new int[ile];
//        @Override
//        public void run() {
//            int hold;
//            if (Thread.currentThread() == t1) {
//                bubbleSort(arr);
//            }
//        }
//    }
//
//
//    public class ParaBubble implements Runnable {
//
//        public void run()
//        {
//            int hold;
//            if (Thread.currentThread() == t1) {
//                bubbleSort(arr);
//            }
//            else if (Thread.currentThread() == t2) {
//
//                while (num2 < num3) {  /* compare the two neighbors num2 and num3 */
//
//                    hold = num2;
//
//                    num2 = num3;
//
//                    num3 = hold;
//
//                    System.out.print(Thread.currentThread().getName() + " value is " + x);
//                }
//
//            }
//            else if (Thread.currentThread() == t3) {
//
//                while (num3 < num4) {  /* compare the two neighbors num3 and num4 */
//
//                    hold = num3;
//
//                    num3 = num4;
//
//                    num4 = hold;
//
//                    System.out.print(Thread.currentThread().getName() + " value is " + x);
//                }
//
//            }
//
//            else if (Thread.currentThread() == t4) {
//
//                while (num4 < num5) {  /* compare the two neighbors num4 and num5 */
//
//                    hold = num4;
//
//                    num4 = num5;
//
//                    num5 = hold;
//
//                    System.out.print(Thread.currentThread().getName() + " value is " + x);
//                }
//
//            }
//
//            }
//        }
//    }
//    }
//    }
}

