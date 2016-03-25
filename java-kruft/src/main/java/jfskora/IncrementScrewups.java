package jfskora;

public class IncrementScrewups
{
    public static void main(String[] args) {
        int i = 0;
        int j = 0;
        System.out.println("i=" + i + ", j=" + j); // i=0, j=0
        i = i++;
        System.out.println("i=" + i + ", j=" + j); // i=0, j=0
        j = i++;
        System.out.println("i=" + i + ", j=" + j); // i=1, j=0
        i += 1;
        System.out.println("i=" + i + ", j=" + j); // i=2, j=0
        j += i;
        System.out.println("i=" + i + ", j=" + j); // i=2, j=2
    }
}
