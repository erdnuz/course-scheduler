# Course Scheduler

A Java application to generate, display, and rank course schedules based on customizable criteria such as days off, minimal gaps, and preferred class times.


## Features

* Load course data from JSON files.
* Generate all valid non-overlapping schedule options for multiple courses.
* Rank schedules based on customizable criteria:

  * Days off (more days off = higher score)
  * Minimal gaps between classes
  * Preferred start and end times
* Update ranking weights and scoring criteria via a JSON configuration file.
* Display schedules in a readable timetable format.

---

## Installation

1. **Clone the repository:**

```bash
git clone https://github.com/erdnuz/course-scheduler.git
cd course-scheduler
```

2. **Build the project using Maven:**

```bash
mvn clean compile
```

> This command downloads dependencies (including Jackson) and compiles the source code.

---

## Running the Application

Run the main class via Maven:

```bash
mvn exec:java -Dexec.mainClass="Main"
```

* When prompted, enter the path to a course JSON file (e.g., `test1.json`).
* If the file is not found, the program will automatically search in `src/test/resources`.
* Type `exit` to quit the program.
* Optional: Enable verbose mode to see detailed scoring and scheduling information.

---

## Project Structure

```text
course-scheduler/
├── pom.xml                     # Maven project file
├── README.md                   # Project documentation
├── src/
│   ├── main/
│   │   ├── java/               # Application source code (model, logic, input, output)
│   │   └── resources/          # Application resources (JSON files)
│   └── test/
│       └── resources/          # Sample JSON files for testing
└── .gitignore
```

---

## JSON Format

### Example JSON for a course:

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

### Field Mapping

* **startTime**: integer from 0 to 8, representing 1.5-hour intervals:

  * 0 → 08:30
  * 1 → 10:00
  * 2 → 11:30
  * 3 → 13:00
  * 4 → 14:30
  * 5 → 16:00
  * 6 → 17:30
  * 7 → 19:00
  * 8 → 20:30

* **day**: integer from 0 to 4, representing weekdays:

  * 0 → Monday
  * 1 → Tuesday
  * 2 → Wednesday
  * 3 → Thursday
  * 4 → Friday

> If a course has no laboratory or tutorial timeslots, the system assumes that component is non-mandatory.

---

## Configuration

Update ranking weights and scoring rules in:

```
src/main/java/logic/config/config.json
```

Configurable options include:

* Points for days off
* Penalties for early/late classes
* Gap penalties
* Whether to do lecture-only scoring
* Lecture-only scoring weighting (If the above option is turned on)

---

## Notes

* Place course JSON files under `src/test/resources`.
* The project uses Maven for dependency management.
* All scheduling options are displayed in the console in a timetable format.
* Enable verbose mode when prompted to display detailed debugging information and commentary on scheduling and scoring.
