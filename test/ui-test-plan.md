# UI Test Plan

Compile command: `javac -d /tmp/duck-ui-test src/main/java/*.java`
Run command: `java -cp /tmp/duck-ui-test Duck`

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
deadline return book /by Sunday
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
[D][ ] return book (by: Sunday)
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
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
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

## Test Case 5: Invalid deadline and event commands do not add tasks
Aim: Verify that malformed deadline and event commands do not alter the task list before later valid commands.

### Input
```text
deadline
deadline /by Sunday
deadline return book /by Sunday
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
[D][ ] return book (by: Sunday)
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
1.[D][ ] return book (by: Sunday)
2.[E][ ] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
