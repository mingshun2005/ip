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
