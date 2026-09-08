# Duck User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

Use a deadline when a task must be completed by a specific date.

```text
deadline DESCRIPTION /by DATE
```

`DATE` can be a date in `yyyy-MM-dd` format or one of these English weekday
abbreviations:

```text
Mon Tue Wed Thu Fri Sat Sun
```

Weekday abbreviations are case-insensitive and mean the next occurrence of that
weekday. If today is Monday, `Mon` means the Monday seven days later.

For example, if today is Tuesday, September 8, 2026:

```text
deadline submit report /by Mon
```

Duck resolves the weekday to its exact date and displays:

```
[D][ ] submit report (by: Sep 14 2026)
```

The saved deadline uses the resolved ISO date, `2026-09-14`. Full weekday names,
informal abbreviations such as `Tues`, relative phrases such as `next Mon`, and
dates containing times are not supported.

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
