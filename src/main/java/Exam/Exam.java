package Exam;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by JasonFitch on 10/30/2018.
 */
public class Exam {

    public static int recursion = 0;

    public static void main(String[] args) {

        fib(9);
        System.out.println(recursion);

        int result = circle(new Random(System.currentTimeMillis()).nextInt(2000000000) + 1);
        System.out.println(result);

        new HashSet<Integer>().add(1);
    }

    private static int circle(int rSquare) {
        class Point {
            Integer a = 0, b = 0;

            public Point(Integer a, Integer b) {
                this.a = a;
                this.b = b;
            }
        }

        Set<Point> result = new HashSet<>();
        for (int i = 0; i < (int) Math.sqrt(rSquare) + 1; i++) {
            for (int j = 0; j < (int) Math.sqrt(rSquare) + 1; j++) {
                if ((int) Math.pow(i, 2) + (int) Math.pow(j, 2) == rSquare) {
                    System.out.println("" + i + "," + j);
                    result.add(new Point(i, j));
                }
            }
        }
        return result.size();
    }

    private static int fib(int i) {
        ++recursion;

        if (0 == i)
            return 1;
        else if (1 == i)
            return 2;
        else
            return fib(i - 1) + fib(i - 2);
    }

}
