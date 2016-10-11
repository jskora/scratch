package jfskora.reflection;

public class SummerDemo {
    public static void main(String[] args) {
        Summer sum = new Summer("dogs");
        sum.add(2);
        sum.add(3);
        System.out.println(sum.getTotal());
    }
}
