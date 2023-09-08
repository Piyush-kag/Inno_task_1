import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

import java.io.IOException;

public class StudentFilter {

    public static void main(String[] args) throws IOException {
        String studentFileName = "Student.csv";
        String classFileName = "Class.csv";
        String addressFileName = "Address.csv";

        // Read CSV files
        String[][] studentData = readCSV(studentFileName);
        String[][] classData = readCSV(classFileName);
        String[][] addressData = readCSV(addressFileName);

        // Define criteria
        String targetPincode = "482002"; // Replace with your desired pincode
        String className = "A";
        String gender = "F";
        int minAge = 10;
        int maxAge = 20;
        String city = "indore";

        // Fetch and display students based on criteria

        // 1-Pincode

        System.out.println("Filtered Students:");
        Arrays.stream(studentData, 1, studentData.length) // Start from index 1 to skip the header row
                .filter(student -> {
                    String studentId = student[0];
                    String studentPincode = findStudentPincode(studentId, addressData);
                    return studentPincode != null && studentPincode.equals(targetPincode) &&
                            student.length > 4 && student[4].equals(filterGender) &&
                            filterByAge(student, filterMinAge, filterMaxAge) &&
                            filterByClass(student, filterClass);
                })
                .forEach(student -> System.out.println(student[0] + "\t" + student[1]));
    }

    // 6

    System.out.println("Students in Class "+className+":");Arrays.stream(studentData).filter(student->student.length>2&&student[2].equals(className)).forEach(student->System.out.println(student[0]+"\t"+student[1]));

    //
    System.out.println("\nFemale Students:");Arrays.stream(studentData).filter(student->student.length>4&&student[4].equals(gender)).forEach(student->System.out.println(student[0]+"\t"+student[1]));

    System.out.println("\nStudents aged "+minAge+" to "+maxAge+":");Arrays.stream(studentData,1,studentData.length).filter(student->student.length>5).filter(student->

    private static String findStudentPincode(String studentId, String[][] addressData) {
        return null;
    }

    {
            int age = Integer.parseInt(student[5]);
            return age >= minAge && age <= maxAge;
        }).forEach(student->System.out.println(student[0]+"\t"+student[1]));

    System.out.println("\nStudents in "+city+":");Arrays.stream(studentData).flatMap(student->Arrays.stream(addressData).filter(address->student.length>0&&address.length>2&&student[0].equals(address[3])&&address[2].equals(city)).map(address->student[0]+"\t"+student[1])).forEach(System.out::println);

    // 4

    }

    // Pincode Method
    private static boolean filterByAge(String[] student, int minAge, int maxAge) {
        int age = Integer.parseInt(student.length > 5 ? student[5] : "0");
        return age >= minAge && age <= maxAge;
    }

    private static boolean filterByClass(String[] student, String filterClass) {
        String studentClassId = student.length > 2 ? student[2] : "";
        return studentClassId.equals(filterClass);
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
