import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentFilter {

    public static void main(String[] args) throws IOException {
        String studentFileName = "Student.csv";
        String classFileName = "Class.csv";
        String addressFileName = "Address.csv";

        // Read CSV files
        String[][] studentData = readCSV(studentFileName);
        String[][] classData = readCSV(classFileName);
        String[][] addressData = readCSV(addressFileName);

        // 1-Pincode
        String targetPincode = "482002";

        List<String[]> studentsWithPincode = Arrays.stream(studentData)
                .filter(studentRow -> {
                    if (studentRow.length > 5) {
                        String studentId = studentRow[0];
                        return hasAddressWithPincode(studentId, targetPincode, addressData);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // Print the students with the specified pincode and their addresses
        if (studentsWithPincode.isEmpty()) {
            System.out.println("No students found with pincode: " + targetPincode);
        } else {
            System.out.println("Students with pincode " + targetPincode + ":");
            for (String[] student : studentsWithPincode) {
                System.out.println("Student ID: " + student[0] + " | Name: " + student[1]);
            }
        }

        System.out.println("\n");
        // 2-Find City

        String targetCity = "indore";
        List<String[]> studentsFromCity = Arrays.stream(studentData)
                .filter(studentRow -> {
                    if (studentRow.length > 5) {
                        String studentId = studentRow[0];
                        String studentCity = getCityByStudentId(studentId, addressData);

                        // Check if the student is from the target city
                        return targetCity.equals(studentCity);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // Print the students from the specified city
        if (studentsFromCity.isEmpty()) {
            System.out.println("No students found from " + targetCity);
        } else {
            System.out.println("Students from " + targetCity + ":");
            for (String[] student : studentsFromCity) {
                System.out.println("Student ID: " + student[0] + " | Name: " + student[1]);
            }
        }
        System.out.println("\n");

        // // 3-marks < 50 failed else passed Give ranks to highest mark
        // achievers.Highest marks - First, Third Highest marks - 3, Rest of all pass /
        // fail

        determinePassFailAndAssignRanks(studentData);
        System.out.println("\n");

        // 4-
        passedStudent(studentData);
        System.out.println("\n");
        // main method closing below
    }

    private static boolean hasAddressWithPincode(String studentId, String targetPincode, String[][] addressData) {
        long matchingAddresses = Arrays.stream(addressData)
                .filter(addressRow -> {
                    if (addressRow.length > 3) {
                        String addressPincode = addressRow[1];
                        String studentIdInAddress = addressRow[3];
                        return studentId.equals(studentIdInAddress) && targetPincode.equals(addressPincode);
                    }
                    return false;
                })
                .count();

        return matchingAddresses > 0;
    }

    // Method 2

    private static String getCityByStudentId(String studentId, String[][] addressData) {
        Optional<String> cityOptional = Arrays.stream(addressData)
                .filter(addressRow -> {
                    if (addressRow.length > 3) {
                        String studentIdInAddress = addressRow[3];
                        return studentId.equals(studentIdInAddress);
                    }
                    return false;
                })
                .map(addressRow -> addressRow[2]) // Extract city
                .findFirst();

        return cityOptional.orElse("");
    }

    // method-3
    private static int determinePassFailAndAssignRanks(String[][] studentData) {
        // Skip the header row if it exists and sort students by marks in ascending
        // order
        List<String[]> students = Arrays.stream(studentData)
                .skip(1) // Skip the header row
                .filter(studentRow -> {
                    if (studentRow.length > 3) {
                        int marks = Integer.parseInt(studentRow[3]);
                        return marks >= 0; // Filter out rows with invalid marks
                    }
                    return false;
                })
                .sorted(Comparator.comparingInt(studentRow -> Integer.parseInt(studentRow[3])))
                .collect(Collectors.toList());

        // Determine pass/fail based on a threshold (e.g., marks < 50)
        for (String[] student : students) {
            int marks = Integer.parseInt(student[3]);
            String studentName = student[1];
            String rank = assignPassFail(marks); // Assign 0 for "Fail" or 1 for "Pass"
            System.out.println("Student Name: " + studentName + " | Marks: " + marks + " | Result: " + rank);
        }
        return 0;
    }

    private static String assignPassFail(int marks) {
        return (marks < 50) ? "Fail" : "Pass";
    }

    // Method-4
    private static int passedStudent(String[][] studentData) {
        List<String[]> passedStudents = Arrays.stream(studentData)
                .skip(1) // Skip the header row
                .filter(studentRow -> {
                    if (studentRow.length > 3) {
                        int marks = Integer.parseInt(studentRow[3]);
                        return marks >= 50; // Filter out students who passed (marks >= 50)
                    }
                    return false;
                })
                .sorted(Comparator.comparingInt(studentRow -> Integer.parseInt(studentRow[3])))
                .collect(Collectors.toList());

        for (String[] student : passedStudents) {
            int marks = Integer.parseInt(student[3]);
            String studentName = student[1];
            String result = "Pass";
            System.out.println("Student Name: " + studentName + " | Marks: " + marks + " | Result: " + result);
        }
        return 0;
    }

    // Read CSV File Method
    private static String[][] readCSV(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        int numRows = 0;
        int numCols = 0;

        while ((line = reader.readLine()) != null) {
            numRows++;
            String[] row = line.split(",");
            numCols = Math.max(numCols, row.length);
        }

        String[][] data = new String[numRows][numCols];
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