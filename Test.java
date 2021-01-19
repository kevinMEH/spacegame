import java.util.Scanner;

public class Test {

    public static void main(String[] args) { // Test
        System.out.println("Choose the index of the action to add to queue:");
        Scanner scanner = new Scanner(System.in);
        int responseIndex;
        try {
            responseIndex = scanner.nextInt();
            scanner.close();
            System.out.println(responseIndex);
        } catch (Exception e) {
            System.out.println("ERROR: Not a Number.");
            scanner.close();
        }
    }

}
