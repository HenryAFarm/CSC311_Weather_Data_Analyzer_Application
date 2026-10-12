# Weather Data Analyzer Application

A Java application that reads daily weather observations from a CSV file and summarizes the data.
It reports the average temperature for a selected month, days above a temperature threshold, the number of rainy days, and a weather category for each observation.

## CSV Format
The CSV file has this header:
```csv
Date,Temperature,Humidity,Precipitation

examples like for the data: 
2023-08-01,32.5,65,0.0
2023-08-02,35.0,60,0.2
```

# Analysis
•Monthly average: The application currently analyzes August 2023.
•Days above a threshold: currently lists days above 33 degrees.
•Rainy days: A day counts as rainy when precipitation is greater than zero.
•Weather categories: Temperatures below 10 are Cold, below 25 are Warm, and 25 or higher are Hot.


# Implementation
The application uses a Java record to represent each day's weather data. So most of its variable is created there! 
It uses streams and lambdas to process the observations, a switch expression with record patterns to assign weather categories, and a text block to format the summary report.

# Running the code
This application need to read data in order to be used. So it important that the CSV file is there,
and there's a path to the main. 

# Requirements
JDK 23 above, but in this case I had it in JDK 25, as shown in the pom.xml
maven project
