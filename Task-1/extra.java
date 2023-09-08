import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class StudentManagementSystem {

    public static void main(String[] args) throws IOException {
        String studentFileName = "Student.csv";
        String classFileName = "Class.csv";
        String addressFileName = "Address.csv";

        // Read CSV files
        String[][] studentData = readCSV(studentFileName);
        String[][] classData = readCSV(classFileName);
        String[][] addressData = readCSV(addressFileName);

        // Define criteria
        String className = "A";
        String gender = "F";
        int minAge = 10;
        int maxAge = 20;
        String city = "indore";

        // Fetch and display students based on criteria
        System.out.println("Students in Class " + className + ":");
        for (String[] student : studentData) {
            if (student[2].equals(className)) {
                System.out.println(student[0] + "\t" + student[1]);
            }
        }

        System.out.println("\nFemale Students:");
        for (String[] student : studentData) {
            if (student[4].equals(gender)) {
                System.out.println(student[0] + "\t" + student[1]);
            }
        }

        System.out.println("\nStudents aged " + minAge + " to " + maxAge + ":");
        for (String[] student : studentData) {
            int age = Integer.parseInt(student[5]);
            if (age >= minAge && age <= maxAge) {
                System.out.println(student[0] + "\t" + student[1]);
            }
        }

        System.out.println("\nStudents in " + city + ":");
        for (String[] student : studentData) {
            for (String[] address : addressData) {
                if (student[0].equals(address[3]) && address[2].equals(city)) {
                    System.out.println(student[0] + "\t" + student[1]);
                    break;
                }
            }
        }
    }

    private static String[][] readCSV(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        int numRows = 0;
        int numCols = 0;

        // Determine the number of rows and columns in the CSV
        while ((line = reader.readLine()) != null) {
            numRows++;
            String[] row = line.split(",");
            numCols = Math.max(numCols, row.length);
        }

        // Create a 2D array to store the CSV data
        String[][] data = new String[numRows][numCols];

        // Reset the reader to the beginning of the file
        reader.close();
        reader = new BufferedReader(new FileReader(fileName));

        // Read the CSV data into the array
        int rowIdx = 0;
        while ((line = reader.readLine()) != null) {
            String[] row = line.split(",");
            for (int colIdx = 0; colIdx < row.length; colIdx++) {
                data[rowIdx][colIdx] = row[colIdx];
            }
            rowIdx++;
        }

        reader.close();
        return data;
    }
}
