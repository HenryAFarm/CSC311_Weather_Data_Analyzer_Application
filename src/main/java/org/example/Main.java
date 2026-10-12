package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Henry Arevalo
 *
 *
 * Develop a Weather Data Analyzer Application using modern Java features such as the enhanced switch statement, text blocks, lambdas, markdown support in Javadoc, and new features introduced from Java 15 to Java 23.
 * The application should be designed without explicit classes (e.g., only records, interfaces, and functional programming) and should demonstrate efficient code organization.
 * The project will be uploaded to GitHub with a comprehensive README.md file (missing readme.md = -30%).
 *
 * Features to Implement:
 * Parse weather data (e.g., temperature, humidity, precipitation) from a CSV file.
 * Provide the following functionalities:
 * Average temperature for a specific month.
 * Days with temperatures above a given threshold.
 * Count of rainy days.
 * Use an enhanced switch statement to determine weather categories (e.g., "Hot", "Warm", "Cold").
 * Include lambdas and streams to process weather data efficiently.
 *
 * Use Modern Java Features:
 * Use Java Records to represent weather data.
 * Use the enhanced switch (introduced in Java 14 and finalized in later versions).
 * Use Text Blocks for generating output (introduced in Java 15).
 * Use Pattern Matching for switch or any other feature from Java 15–23 (e.g., String.stripIndent, virtual threads if relevant).
 * Lambda and Streams must be used.
 *
 *
 * Technical Constraints:
 * Do not use explicit classes. The design should leverage records, interfaces, and functional constructs.
 *
 * Documentation:
 *Use markdown Javadoc for all methods and features. Include code examples in the Javadoc using the markdown support introduced in JDK 18.
 *
 *
 *
 */
public class Main {
    /**
     * A single day's weather measurements.
     *
     * @param date the date of the measurements
     * @param temperature the temperature in degrees
     * @param humidity the relative humidity percentage
     * @param precipitation the precipitation amount
     */
    //the data
    record WeatherData(LocalDate date, double temperature, int humidity, double precipitation) {
    }

    /**
     * Loads the weather records from a CSV file with the header
     * {@code Date,Temperature,Humidity,Precipitation}.
     *
     * @param csvPath path to the CSV file
     * @return the parsed weather records
     * @throws IOException if the file cannot be read or its header is invalid
     * @throws IllegalArgumentException if a data row is malformed
     */
    static List<WeatherData> readWeatherData(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        if (lines.isEmpty()
                || !lines.get(0).strip().equals("Date,Temperature,Humidity,Precipitation")) {
            throw new IOException("The CSV file must start with "
                    + "Date, Temperature, Humidity, Precipitation.");
        }

        return IntStream.range(1, lines.size())
                .filter(index -> !lines.get(index).isBlank())
                .mapToObj(index -> parseWeatherData(lines.get(index), index + 1))
                .toList();
    }

    /**
     * Parses one CSV row and includes its line number in the file if it wasn't in the right way.
     *
     * @param line CSV row contents
     * @param lineNumber one-based line number in the file
     * @return the parsed weather record
     * @throws IllegalArgumentException if the row has the wrong number of columns or invalid values
     */
    //That's if the file wasn't written the way its suppose to!
    private static WeatherData parseWeatherData(String line, int lineNumber) {
        String[] columns = line.strip().split(",", -1);
        if (columns.length != 4) {
            throw new IllegalArgumentException("Expected 4 columns at CSV line " + lineNumber + ".");
        }

        try {
            return new WeatherData(
                    LocalDate.parse(columns[0].strip()),
                    Double.parseDouble(columns[1].strip()),
                    Integer.parseInt(columns[2].strip()),
                    Double.parseDouble(columns[3].strip()));
        } catch (DateTimeParseException | NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid weather data at CSV line " + lineNumber + ".", exception);
        }
    }

    /**
     * Reads {@code weatherdata.csv} from the project directory prints the records.
     *
     * @param args optional path to the weather CSV file
     * @throws IOException if the CSV file cannot be read
     */
    public static void main(String[] args) throws IOException {
        //the path to the file
        Path csvPath = args.length > 0 ? Path.of(args[0]) : Path.of("weatherdata.csv");
        List<WeatherData> weatherData = readWeatherData(csvPath);

        System.out.println("There's " + weatherData.size() + " weather records during the year 2023:");
        weatherData.forEach(System.out::println);
    }
}
