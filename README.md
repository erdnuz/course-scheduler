# Course Scheduler

A Java tool to generate, display, and rank course schedules based on customizable criteria such as days off, minimal gaps, and preferred start/end times.

## Installation

1. Clone the repository:

```bash
git clone https://github.com/erdnuz/course-scheduler.git
cd course-scheduler
```

2. Build the project using Maven:

```bash
mvn clean compile
```

This will download all required dependencies (e.g., Jackson) and compile the source code.

## Running the Application

You can run the main class (Main) via Maven:

```bash
mvn exec:java -Dexec.mainClass="Main"
```

When prompted, provide the path to a course JSON file (e.g., test1.json or test2.json).

If the file is not found, the program automatically searches in src/test/resources.

Type exit to quit the program.

## Project Structure

```text
course-scheduler/
├── pom.xml                  # Maven project file
├── README.md                # This file
├── src/
│   ├── main/
│   │   ├── java/            # Application source code (model, logic, input, output)
│   │   └── resources/       # Application resources (JSON files)
│   └── test/
│       └── resources/       # Test resources (sample JSON files)
└── .gitignore
```

## Features

- Load course data from JSON files.
- Generate all valid schedule options for multiple courses.
- Rank schedules based on:
  - Number of days off.
  - Minimal gaps between classes.
  - Preferred start and end times (avoid early/late classes).
- Display schedules in a clear, readable timetable format.
- Provide detailed debugging output showing how points are calculated for each schedule.


## JSON Format

Example JSON for a course:

```json
[
  {
    "field": "CSI",
    "code": 1011,
    "sections": [
      {
        "name": "A",
        "lectures": [
          {"startTime": 0, "day": 0, "extended": true}
        ],
        "laboratoryOptions": [
          {"startTime": 3, "day": 1, "extended": false}
        ],
        "tutorialOptions": [
          {"startTime": 1, "day": 4, "extended": false}
        ]
      }
    ]
  }
]
``` 

### Notes on JSON Fields

#### `startTime` - An integer from 0 to 8 representing 1.5-hour intervals:

| Value | Time  |
|-------|-------|
| 0     | 08:30 |
| 1     | 10:00 |
| 2     | 11:30 |
| 3     | 13:00 |
| 4     | 14:30 |
| 5     | 16:00 |
| 6     | 17:30 |
| 7     | 19:00 |
| 8     | 20:30 |

#### `day` - An integer from 0 to 4 representing weekdays:

| Value | Day       |
|-------|-----------|
| 0     | Monday    |
| 1     | Tuesday   |
| 2     | Wednesday |
| 3     | Thursday  |
| 4     | Friday    |


## Notes
Place course JSON files under src/main/resources or src/test/resources.

The project uses Maven to manage dependencies.

All schedule outputs and scoring details are printed to the console in a readable timetable format.
