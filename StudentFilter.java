import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

        failedStudent(studentData);
        System.out.println("\n");

        classStudent(classData, studentData);
        System.out.println("\n");

        System.out.println("Student who got failed and Aged above 20:-");
        failedAndGreaterThan20(studentData);
        System.out.println("\n");

        deleteStudentAndAddress(addressData, studentData, "3", "Student.csv", "Address.csv");
        System.out.println("\n");

        deleteClassIfNoStudent(studentData, addressData, classData, "Class.csv");
        System.out.println("\n");

        System.out.println("female students first 1-9::-");
        readFemaleStudentsPage(1, 3, studentData);
        System.out.println("\n");

        System.out.println("female students first 7-8 order by Name:-");
        readFemaleStudentsSortedByNamePage(1, 9, studentData);
        System.out.println("\n");

        System.out.println("female students first 1-5 order by Marks:-");
        readFemaleStudentsSortedByMarksPage(1, 5, studentData);
        System.out.println("\n");

        System.out.println("female students first 9-50 order by marks::-");
        readFemaleStudentsSortedByMarksPageRange(1, 5, 1, studentData);
        System.out.println("\n");

    }

    // Method-1
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
            String result = "Passed";
            System.out.println("Student Name: " + studentName + " | Marks: " + marks + " | Result: " + result);
        }
        return 0;
    }

    // Method-5

    private static int failedStudent(String[][] studentData) {
        List<String[]> failedStudents = Arrays.stream(studentData)
                .skip(1) // Skip the header row
                .filter(studentRow -> {
                    if (studentRow.length > 3) {
                        int marks = Integer.parseInt(studentRow[3]);
                        return marks <= 50; // Filter out students who passed (marks >= 50)
                    }
                    return false;
                })
                .sorted(Comparator.comparingInt(studentRow -> Integer.parseInt(studentRow[3])))
                .collect(Collectors.toList());

        for (String[] student : failedStudents) {
            int marks = Integer.parseInt(student[3]);
            String studentName = student[1];
            String result = "Failed";
            System.out.println("Student Name: " + studentName + " | Marks: " + marks + " | Result: " + result);
        }
        return 0;
    }

    // Method-6--Find all student of class X (ex X = A). I can pass different
    // filters like gender, age, class, city, pincode
    private static void classStudent(String[][] classData, String[][] studentData) {
        List<String> classIdsA = Arrays.stream(classData)
                .skip(1)
                .filter(classRow -> "A".equals(classRow[1])) // Assuming class A is identified by "A" in column 1
                .map(classRow -> classRow[0]) // Extract class IDs
                .collect(Collectors.toList());

        // Collect students with class IDs in classIdsA from studentData
        List<String[]> studentsOfClassA = Arrays.stream(studentData)
                .skip(1) // Skip the header row
                .filter(studentRow -> classIdsA.contains(studentRow[2])) // Filter by class IDs
                .collect(Collectors.toList());

        // Display the students with the same class ID as class A
        System.out.println("Students of Class A:");
        for (String[] student : studentsOfClassA) {
            System.out.println("Student ID: " + student[0] + " | Name: " + student[1] + " | Class ID: " + student[2]);
        }
        // Now, classStuList contains all students of class X = A
    }

    // Method-8

    private static void failedAndGreaterThan20(String[][] studentData) {
        List<String[]> findFailedAndGT20 = Arrays.stream(studentData)
                .skip(1) // Skip the header row
                .filter(studentRow -> {
                    int marks = Integer.parseInt(studentRow[3]);
                    int age = Integer.parseInt(studentRow[5]);
                    return marks < 50 && age > 20;
                })
                .collect(Collectors.toList());

        for (String[] student : findFailedAndGT20) {
            System.out.println("Student ID: " + student[0] + " | Name: " + student[1] +
                    " | Age: " + student[5] + " | Marks: " + student[3]);
        }
    }

    // Method-9- I should be able to delete student. After that it should delete the
    // respective obj from Address & Student.
    private static void deleteStudentAndAddress(String[][] addressData, String[][] studentData,
            String studentIdToDelete,
            String studentCsvFilePath, String addressCsvFilePath) {
        List<String[]> updatedStudentData = Arrays.stream(studentData)
                .filter(studentRow -> !studentRow[0].equals(studentIdToDelete))
                .collect(Collectors.toList());

        // Write the updated student data back to the student CSV file
        try (BufferedWriter studentWriter = new BufferedWriter(new FileWriter(studentCsvFilePath))) {
            updatedStudentData.stream()
                    .map(row -> String.join(",", row))
                    .forEach(csvRow -> {
                        try {
                            studentWriter.write(csvRow);
                            studentWriter.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            System.out.println("Student with ID " + studentIdToDelete + " has been deleted.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String[]> updatedAddressData = Arrays.stream(addressData)
                .filter(addressRow -> !addressRow[3].equals(studentIdToDelete))
                .collect(Collectors.toList());

        try (BufferedWriter addressWriter = new BufferedWriter(new FileWriter(addressCsvFilePath))) {
            updatedAddressData.stream()
                    .map(csvrow -> String.join(",", csvrow))
                    .forEach(Row -> {
                        try {
                            addressWriter.write(Row);
                            addressWriter.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            System.out.println("Addresses associated with Student ID " + studentIdToDelete
                    + " have been deleted from address CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method-10--If there is no student remaining in that class. Class should also
    // be deleted.

    public static void deleteClassIfNoStudent(String[][] studentData, String[][] addressData, String[][] classData,
            String classCsvFilePath) {
        Set<String> classNamesWithStudents = Arrays.stream(studentData)
                .map(studentInfo -> studentInfo[2])
                .collect(Collectors.toSet());

        List<String[]> remainingClasses = Arrays.stream(classData)
                .filter(classInfo -> classNamesWithStudents.contains(classInfo[0]))
                .collect(Collectors.toList());

        // Display remaining classes
        displayDe(remainingClasses.toArray(new String[0][]));

        // Write the remaining classes back to the class CSV file
        writeClassDataToCSV(remainingClasses, classCsvFilePath);
    }

    private static void displayDe(String[][] classData) {
        System.out.println("Remaining Classes:-\n");
        for (String[] classInfo : classData) {
            System.out.println(Arrays.toString(classInfo));
        }
    }

    private static void writeClassDataToCSV(List<String[]> classData, String classCsvFilePath) {
        try (BufferedWriter classWriter = new BufferedWriter(new FileWriter(classCsvFilePath))) {
            for (String[] row : classData) {
                // Join the array elements with commas to create a CSV row
                String csvRow = String.join(",", row);
                classWriter.write(csvRow);
                classWriter.newLine();
            }
            System.out.println("Updated class data has been written to " + classCsvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to check if a class has at least one student
    // private static boolean hasStudentInClass(String[][] studentData, String
    // className) {
    // return Arrays.stream(studentData)
    // .anyMatch(studentInfo -> studentInfo[1].equals(className));
    // }

    // Method-11-I should be able to read paginated students.
    // like : read female students first 1-9
    // like : read female students first 7-8 order by name
    // like : read female students first 1-5 order by marks
    // like : read female students first 9-50 order by marks

    // 11-1-Read female students first 1-9:

    private static void readFemaleStudentsPage(int pageNumber, int pageSize, String[][] studentData) {
        List<String[]> femaleStudents = Arrays.stream(studentData)
                .filter(studentRow -> "F".equals(studentRow[4])) // Filter female students
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        printStudents(femaleStudents);
    }

    private static void readFemaleStudentsSortedByNamePage(int pageNumber, int pageSize, String[][] studentData) {
        List<String[]> femaleStudents = Arrays.stream(studentData)
                .filter(studentRow -> "F".equals(studentRow[4])) // Filter female students
                .sorted(Comparator.comparing(studentRow -> studentRow[1])) // Sort by name
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        printStudents(femaleStudents);
    }

    private static void printStudents(List<String[]> femaleStudents) {

        for (String[] student : femaleStudents) {
            System.out.println("Student ID: " + student[0] + " | Name: " + student[1]);
        }

    }

    private static void readFemaleStudentsSortedByMarksPage(int pageNumber, int pageSize, String[][] studentData) {

        List<String[]> femaleStudents = Arrays.stream(studentData)
                .filter(studentRow -> "F".equals(studentRow[4])) // Filter female students
                .sorted(Comparator.comparing(studentRow -> Integer.parseInt(studentRow[3]))) // Sort by marks
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        printStudents1(femaleStudents);
    }

    private static void readFemaleStudentsSortedByMarksPageRange(int startPage, int endPage, int pageSize,
            String[][] studentData) {
        List<String[]> femaleStudents = Arrays.stream(studentData)
                .filter(studentRow -> "F".equals(studentRow[4])) // Filter female students
                .sorted(Comparator.comparing(studentRow -> Integer.parseInt(studentRow[3]))) // Sort by marks
                .collect(Collectors.toList());

        int totalStudents = femaleStudents.size();
        int totalPages = (int) Math.ceil((double) totalStudents / pageSize);

        if (startPage < 1 || endPage > totalPages || startPage > endPage) {
            System.out.println("Invalid page range.");
            return;
        }

        for (int page = startPage; page <= endPage; page++) {
            List<String[]> pageStudents = femaleStudents.stream()
                    .skip((page - 1) * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            System.out.println("Page " + page + ":");
            printStudents1(pageStudents);
        }
    }

    private static void printStudents1(List<String[]> femaleStudents) {

        for (String[] student : femaleStudents) {
            System.out.println("Student ID: " + student[0] + " | Name: " + student[1] + " | Marks: " + student[3]);
        }
    }

    // Read CSV File Method-1
    // private static String[][] readCSV(String fileName) throws IOException {
    // BufferedReader reader = new BufferedReader(new FileReader(fileName));
    // String line;
    // int numRows = 0;
    // int numCols = 0;

    // while ((line = reader.readLine()) != null) {
    // numRows++;
    // String[] row = line.split(",");
    // numCols = Math.max(numCols, row.length);
    // }

    // String[][] data = new String[numRows][numCols];
    // reader.close();
    // reader = new BufferedReader(new FileReader(fileName));

    // // Read the CSV data into the array
    // int rowIdx = 0;
    // while ((line = reader.readLine()) != null) {
    // String[] row = line.split(",");
    // for (int colIdx = 0; colIdx < row.length; colIdx++) {
    // data[rowIdx][colIdx] = row[colIdx];
    // }
    // rowIdx++;
    // }

    // reader.close();
    // return data;
    // }
    // Read CSV Method-2 Using Stream API Answer-7

    public static String[][] readCSV(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<String[]> lines = reader.lines()
                    .map(line -> line.split(","))
                    .collect(Collectors.toList());

            int numCols = lines.stream()
                    .mapToInt(row -> row.length)
                    .max()
                    .orElse(0);

            String[][] data = lines.stream()
                    .map(row -> Arrays.copyOf(row, numCols)) // Pad rows with empty strings if needed
                    .toArray(String[][]::new);

            return data;
        }
    }
            }
