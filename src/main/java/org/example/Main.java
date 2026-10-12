package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

/**
 * @author Henry Arevalo
 */

public record Main() {
    /**
     * A day's weather measurements.
     *
     * @param date the date of the measurements
     * @param temperature the temperature in degrees
     * @param humidity the relative humidity percentage
     * @param precipitation the precipitation amount
     */
    //the data using Java records
    record WeatherData(LocalDate date, double temperature, int humidity, double precipitation) {
    }

    /**
     * Loads the weather records from a CSV file
     * {@code Date,Temperature,Humidity,Precipitation}.
     *
     * @param csvPath path to the CSV file
     * @return the parsed weather records
     * @throws IOException if the file cannot be read!
     * @throws IllegalArgumentException if a data row is not correctly written!
     */
    static List<WeatherData> readWeatherData(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        if (lines.isEmpty()
                || !lines.get(0).strip().equals("Date,Temperature,Humidity,Precipitation")) {
            throw new IOException("The CSV file must start with: Date, Temperature, Humidity, Precipitation.");
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
     * Calculates the average temperature for a particular month.
     *
     * @param weatherData all available weather records
     * @param month the year and month to analyze
     * @return the average temperature, or an empty result if the month has no records
     */
    //Find the average of temperature.
    static OptionalDouble averageTemperatureForMonth(List<WeatherData> weatherData, YearMonth month) {
        return weatherData.stream()
                .filter(record -> YearMonth.from(record.date()).equals(month))
                .mapToDouble(WeatherData::temperature)
                .average();
    }

    /**
     * Finds records whose temperature is strictly greater than the supplied threshold.
     *
     * @param weatherData all available weather records
     * @param threshold temperature threshold in degrees
     * @return matching records, in their original order
     */
    //Filtering the data, if that day is going over the threshold.
    static List<WeatherData> daysAboveThreshold(List<WeatherData> weatherData, double threshold) {
        return weatherData.stream()
                .filter(record -> record.temperature() > threshold)
                .toList();
    }

    /**
     * Counts days with measurable precipitation.
     *
     * @param weatherData all available weather records
     * @return the number of records with precipitation greater than zero
     */
    //To filter the data, by showing if there is rainy days
    static long countRainyDays(List<WeatherData> weatherData) {
        return weatherData.stream()
                .filter(record -> record.precipitation() > 0)
                .count();
    }

    /**
     * Assigns a category by matching the weather record's components in a switch
     * expression: below 10 is cold, below 25 is warm, and 25 or above is hot.
     *
     * @param weather the weather record to categorize
     * @return the matching weather category
     */
    //using pattern matching
    static String weatherCategory(WeatherData weather) {
        return switch (weather) {
            case WeatherData(var date, var temperature, var humidity, var precipitation)
                    when temperature < 10 -> "Cold";
            case WeatherData(var date, var temperature, var humidity, var precipitation)
                    when temperature < 25 -> "Warm";
            case WeatherData(var date, var temperature, var humidity, var precipitation) -> "Hot";
        };
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
        //Month of August
        YearMonth month = YearMonth.of(2023, 8);
        //the temperature threshold, being hot
        double threshold = 33.0;
        OptionalDouble average = averageTemperatureForMonth(weatherData, month);
        List<WeatherData> aboveThreshold = daysAboveThreshold(weatherData, threshold);

        //Using the local variable in main!
        String averageText = average.isPresent()
                ? "%.1f degrees".formatted(average.getAsDouble())
                : "No data for this month";
        String aboveThresholdText = aboveThreshold.isEmpty()
                ? "  (none)"
                : aboveThreshold.stream()
                        .map(record -> "  %s: %.1f degrees".formatted(record.date(), record.temperature()))
                        .collect(Collectors.joining(System.lineSeparator()));
        String categoriesText = weatherData.stream()
                .map(record -> "  %s: %s".formatted(record.date(), weatherCategory(record)))
                .collect(Collectors.joining(System.lineSeparator()));

        String report = """
                Weather Data Summary
                --------------------
                Records shown in file: %d
                
                Average temperature for %s: %s
                Days above %.1f(hot) degrees:
                %s
                
                Rainy days in the month(count): %d
                
                Weather categories(by C) (cold < 10, warm < 25, hot >= 25 degrees):
                %s
                """.formatted(
                weatherData.size(),
                month,
                averageText,
                threshold,
                aboveThresholdText,
                countRainyDays(weatherData),
                categoriesText);

        System.out.println(report);
    }
}
