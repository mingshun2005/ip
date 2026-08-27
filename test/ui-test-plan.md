# UI Test Plan

Compile command: `javac -d /tmp/duck-ui-test -sourcepath src/main/java src/main/java/duck/Duck.java`
Run command: `java -cp /tmp/duck-ui-test duck.Duck`

## Test Case 1: Exit command
Aim: Verify that the chatbot greets the user and exits when the user enters `bye`.

### Input
```text
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 2: Add and list task types
Aim: Verify that todo, deadline, and event commands add the correct task types and list them.

### Input
```text
todo borrow book
deadline return book /by 2026-08-30
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] return book (by: Aug 30 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Aug 30 2026)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
T | 0 | borrow book
D | 0 | return book | 2026-08-30
E | 0 | project meeting | Mon 2pm | 4pm
```

## Test Case 3: Exception messages
Aim: Verify that invalid user commands are handled with DuckException messages instead of crashing.

### Input
```text
todo
blah
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 4: Invalid todo and mark commands do not change task state
Aim: Verify that empty todo commands and invalid mark commands do not add tasks or change valid task statuses.

### Input
```text
todo first
todo
todo   
todo second
mark 9
mark x
mark 2
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] first
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] second
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! That task number does not exist.
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a valid task number.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] second
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first
2.[T][X] second
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
T | 0 | first
T | 1 | second
```

## Test Case 5: Invalid deadline and event commands do not add tasks
Aim: Verify that malformed deadline and event commands do not alter the task list before later valid commands.

### Input
```text
deadline
deadline /by 2026-08-30
deadline return book /by 2026-08-30
event
event /from Mon /to Tue
event meeting /from Mon 2pm
event meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] return book (by: Aug 30 2026)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of an event cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of an event cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The event command needs a /from and /to time.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] meeting (from: Mon 2pm to: 4pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Aug 30 2026)
2.[E][ ] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
D | 0 | return book | 2026-08-30
E | 0 | meeting | Mon 2pm | 4pm
```

## Test Case 6: Delete removes the selected task only
Aim: Verify that delete uses one-based task numbers, invalid deletes do not alter the list, and remaining tasks keep their order.

### Input
```text
todo first
todo second
todo third
delete 2
delete 9
delete x
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] first
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] second
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] third
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] second
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! That task number does not exist.
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a valid task number.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first
2.[T][ ] third
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
T | 0 | first
T | 0 | third
```

## Test Case 7: Unmark updates the saved status
Aim: Verify that unmark changes a completed task back to incomplete in the save file.

### Input
```text
todo first
mark 1
unmark 1
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] first
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] first
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] first
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
T | 0 | first
```

## Test Case 8: Load tasks from disk on startup
Aim: Verify that todo, deadline, and event tasks are loaded with their saved statuses and remain editable.

### Initial File data/duck.txt
```text
T | 1 | read book
D | 0 | return book | 2026-08-30
E | 0 | project meeting | Mon 2pm | 4pm
```

### Input
```text
list
mark 2
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Aug 30 2026)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Aug 30 2026)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
T | 1 | read book
D | 1 | return book | 2026-08-30
E | 0 | project meeting | Mon 2pm | 4pm
```

## Test Case 9: Invalid saved data is rejected atomically
Aim: Verify that an invalid status reports its line number and prevents a partially loaded task list.

### Initial File data/duck.txt
```text
T | 1 | valid before bad line
D | 2 | invalid status | 2026-08-30
T | 0 | valid after bad line
```

### Input
```text
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
OOPS!!! Unable to load tasks from line 2: the status must be 0 or 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
T | 1 | valid before bad line
D | 2 | invalid status | 2026-08-30
T | 0 | valid after bad line
```

## Test Case 10: Special characters and blank lines round-trip safely
Aim: Verify that blank lines are ignored and escaped pipes and backslashes survive loading and saving.

### Initial File data/duck.txt
```text
T | 1 | review A \| B

D | 0 | path C:\\tmp \| Friday | 2026-08-28
E | 0 | sync \| plan | Room C:\\1 | Room C:\\2
```

### Input
```text
list
todo keep A | B \ C
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] review A | B
2.[D][ ] path C:\tmp | Friday (by: Aug 28 2026)
3.[E][ ] sync | plan (from: Room C:\1 to: Room C:\2)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] keep A | B \ C
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
T | 1 | review A \| B
D | 0 | path C:\\tmp \| Friday | 2026-08-28
E | 0 | sync \| plan | Room C:\\1 | Room C:\\2
T | 0 | keep A \| B \\ C
```

## Test Case 11: Failed saves roll back task changes
Aim: Verify that an unavailable data directory reports an error and does not leave an unsaved task in memory.

### Initial File data
```text
not a directory
```

### Input
```text
todo should roll back
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! Unable to save tasks to data/duck.txt.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data
```text
not a directory
```

## Test Case 12: Missing deadline dates and event times are rejected
Aim: Verify that incomplete deadline and event commands report errors instead of creating invalid tasks or crashing.

### Input
```text
deadline return book
deadline return book /by
event meeting /from Mon
event meeting /from  /to 4pm
event meeting /from Mon /to
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline command needs a non-empty /by date.
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline command needs a non-empty /by date.
____________________________________________________________
____________________________________________________________
OOPS!!! The event command needs a /from and /to time.
____________________________________________________________
____________________________________________________________
OOPS!!! The event command needs a non-empty /from and /to time.
____________________________________________________________
____________________________________________________________
OOPS!!! The event command needs a /from and /to time.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 13: Deadline dates are strictly parsed and formatted
Aim: Verify that malformed dates, invalid calendar dates, unsupported times, and year zero are rejected, while valid dates and leap days use a readable display format.

### Input
```text
deadline non leap day /by 2023-02-29
deadline impossible /by 2026-02-30
deadline invalid month /by 2026-13-01
deadline wrong format /by 30-08-2026
deadline abbreviated /by 2026-8-3
deadline with time /by 2026-10-15 1800
deadline year zero /by 0000-01-01
deadline leap day /by 2024-02-29
deadline submit report /by 2026-10-15
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a valid deadline date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a valid deadline date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a valid deadline date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a valid deadline date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a valid deadline date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a valid deadline date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a valid deadline date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] leap day (by: Feb 29 2024)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] submit report (by: Oct 15 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] leap day (by: Feb 29 2024)
2.[D][ ] submit report (by: Oct 15 2026)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
D | 0 | leap day | 2024-02-29
D | 0 | submit report | 2026-10-15
```

## Test Case 14: Invalid saved deadline dates are rejected
Aim: Verify that an impossible date in the data file reports its line number without loading partial data.

### Initial File data/duck.txt
```text
D | 0 | impossible | 2026-02-30
T | 0 | should not load
```

### Input
```text
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
OOPS!!! Unable to load tasks from line 1: the deadline date must be a valid yyyy-MM-dd date.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
D | 0 | impossible | 2026-02-30
T | 0 | should not load
```

## Test Case 15: Deadline year boundaries are preserved
Aim: Verify that the earliest and latest four-digit years are accepted, displayed with four digits, and saved without information loss.

### Input
```text
deadline earliest supported /by 0001-01-01
deadline latest supported /by 9999-12-31
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] earliest supported (by: Jan 01 0001)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] latest supported (by: Dec 31 9999)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] earliest supported (by: Jan 01 0001)
2.[D][ ] latest supported (by: Dec 31 9999)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
D | 0 | earliest supported | 0001-01-01
D | 0 | latest supported | 9999-12-31
```

## Test Case 16: Noncanonical saved deadline dates are rejected
Aim: Verify that saved dates must also use exactly yyyy-MM-dd, with line-specific errors and no partial load.

### Initial File data/duck.txt
```text
D | 0 | abbreviated | 2026-8-03
T | 0 | should not load
```

### Input
```text
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
OOPS!!! Unable to load tasks from line 1: the deadline date must be a valid yyyy-MM-dd date.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
D | 0 | abbreviated | 2026-8-03
T | 0 | should not load
```

## Test Case 17: Command words require valid boundaries
Aim: Verify that commands without arguments reject trailing text and longer words are not mistaken for known command prefixes.

### Input
```text
bye later
list now
todoish task
marking 1
todo valid
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] valid
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] valid
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
T | 0 | valid
```

## Test Case 18: Find tasks by description keyword
Aim: Verify that find returns matching tasks in order, ignores case, handles no matches, and rejects invalid syntax.

### Initial File data/duck.txt
```text
T | 0 | Read BOOK
D | 0 | return book | 2026-08-30
E | 0 | project meeting | Mon 2pm | 4pm
T | 0 | submit assignment
```

### Input
```text
find book
find PROJECT
find missing
find
finder book
bye
```

### Expected Output
```text
____________________________________________________________
 ____             _
|  _ \ _   _  ___| | __
| | | | | | |/ __| |/ /
| |_| | |_| | (__|   <
|____/ \__,_|\___|_|\_\

Hello! I'm Duck. Quack~
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] Read BOOK
2.[D][ ] return book (by: Aug 30 2026)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
OOPS!!! The keyword for a find command cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected File data/duck.txt
```text
T | 0 | Read BOOK
D | 0 | return book | 2026-08-30
E | 0 | project meeting | Mon 2pm | 4pm
T | 0 | submit assignment
```
