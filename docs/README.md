# Duck User Guide

Duck is a cheerful, organized task manager that helps you keep your ducks in a
row. Its responses use light pond and wing references while keeping commands
and task information concise.

Type `help` at any time to display every supported command.

## Understanding Duck's visual cues

- Blue bubbles show commands that you entered.
- Neutral bubbles with a yellow edge show Duck's responses.
- A `✓` identifies a successful change to your task list.
- `[T]`, `[D]`, and `[E]` identify todos, deadlines, and events respectively.
- A pale-red bubble and warning symbol identify an error.

Duck uses an original yellow-and-blue mascot designed to remain recognizable at
the small avatar size used by the chat window.

## Command summary

| Action | Command |
| --- | --- |
| Show command guidance | `help` |
| Add a todo | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE` |
| Add an event | `event DESCRIPTION /from START /to END` |
| Show all tasks | `list` |
| Find matching tasks | `find KEYWORD` |
| Mark a task complete | `mark NUMBER` |
| Mark a task incomplete | `unmark NUMBER` |
| Delete a task | `delete NUMBER` |
| Exit Duck | `bye` |

## Adding todos

Use a todo for a task without a specific date or time.

```text
todo DESCRIPTION
```

For example:

```text
todo read a book
```

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

## Adding events

Use an event for a task with a start and end time.

```text
event DESCRIPTION /from START /to END
```

For example:

```text
event project meeting /from Mon 2pm /to 4pm
```
