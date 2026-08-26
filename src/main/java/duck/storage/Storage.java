package duck.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import duck.DuckException;
import duck.task.Deadline;
import duck.task.Event;
import duck.task.Task;
import duck.task.TaskType;
import duck.task.Todo;

/**
 * Loads tasks from and saves tasks to a plain-text data file.
 */
public class Storage {
    /** Exact, ASCII-only date shape required in saved deadline records. */
    private static final Pattern DEADLINE_DATE_PATTERN =
            Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

    /** Strict formatter used to decode saved deadline dates. */
    private static final DateTimeFormatter DEADLINE_DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    /** Error included in the line-specific message for invalid saved dates. */
    private static final String INVALID_SAVED_DATE_MESSAGE =
            "the deadline date must be a valid yyyy-MM-dd date.";

    /** Original path text used in user-facing storage errors. */
    private final String filePathText;

    /** Path of the task data file. */
    private final Path dataFilePath;

    /**
     * Creates storage backed by the given file.
     *
     * @param filePath path to the task data file
     */
    public Storage(String filePath) {
        this.filePathText = Objects.requireNonNull(filePath, "Storage file path cannot be null.");
        this.dataFilePath = Path.of(filePath);
    }

    /**
     * Writes the current task list to disk, replacing the previous contents atomically
     * where the file system supports it.
     *
     * @param tasks tasks to save
     * @throws DuckException if the task list cannot be saved
     */
    public void save(List<Task> tasks) throws DuckException {
        ArrayList<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(task.toFileString());
        }

        Path temporaryFile = null;
        try {
            Path parentDirectory = this.dataFilePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Path temporaryDirectory = parentDirectory == null ? Path.of(".") : parentDirectory;
            temporaryFile = Files.createTempFile(temporaryDirectory, "duck-", ".tmp");
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, this.dataFilePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, this.dataFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            throw new DuckException("Unable to save tasks to " + this.filePathText + ".");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // The original save error is more useful than a temporary-file cleanup error.
                }
            }
        }
    }

    /**
     * Reads saved tasks from disk. A missing file represents an empty task list.
     *
     * @return tasks reconstructed from the data file
     * @throws DuckException if the data file cannot be read or contains an invalid task
     */
    public List<Task> load() throws DuckException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        try {
            if (Files.notExists(this.dataFilePath)) {
                return loadedTasks;
            }

            List<String> taskLines = Files.readAllLines(this.dataFilePath, StandardCharsets.UTF_8);
            for (int i = 0; i < taskLines.size(); i++) {
                String taskLine = taskLines.get(i);
                if (taskLine.isBlank()) {
                    continue;
                }
                try {
                    loadedTasks.add(parseTask(taskLine));
                } catch (DuckException e) {
                    throw new DuckException("Unable to load tasks from line " + (i + 1)
                            + ": " + e.getMessage());
                }
            }
            return loadedTasks;
        } catch (IOException | SecurityException e) {
            throw new DuckException("Unable to read tasks from " + this.filePathText + ".");
        }
    }

    /**
     * Reconstructs one task from its plain-text storage representation.
     *
     * @param taskLine one line from the data file
     * @return reconstructed task
     * @throws DuckException if the line does not match the expected storage format
     */
    private Task parseTask(String taskLine) throws DuckException {
        ArrayList<String> parts = splitFileFields(taskLine);
        if (parts.size() < 3) {
            throw new DuckException("the record has too few fields.");
        }

        String taskType = parts.get(0);
        String status = parts.get(1);
        if (!"0".equals(status) && !"1".equals(status)) {
            throw new DuckException("the status must be 0 or 1.");
        }

        Task task;
        if (TaskType.TODO.getFileCode().equals(taskType) && parts.size() == 3) {
            requireNonBlank(parts.get(2), "todo description");
            task = new Todo(parts.get(2));
        } else if (TaskType.DEADLINE.getFileCode().equals(taskType) && parts.size() == 4) {
            requireNonBlank(parts.get(2), "deadline description");
            requireNonBlank(parts.get(3), "deadline date");
            task = new Deadline(parts.get(2), parseDeadlineDate(parts.get(3)));
        } else if (TaskType.EVENT.getFileCode().equals(taskType) && parts.size() == 5) {
            requireNonBlank(parts.get(2), "event description");
            requireNonBlank(parts.get(3), "event start time");
            requireNonBlank(parts.get(4), "event end time");
            task = new Event(parts.get(2), parts.get(3), parts.get(4));
        } else if (!TaskType.TODO.getFileCode().equals(taskType)
                && !TaskType.DEADLINE.getFileCode().equals(taskType)
                && !TaskType.EVENT.getFileCode().equals(taskType)) {
            throw new DuckException("the task type is not recognized.");
        } else {
            throw new DuckException("the task type has the wrong number of fields.");
        }

        if ("1".equals(status)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a stored line while decoding escaped backslashes and pipe characters.
     *
     * @param taskLine line to split
     * @return decoded storage fields
     */
    private ArrayList<String> splitFileFields(String taskLine) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < taskLine.length(); i++) {
            char currentCharacter = taskLine.charAt(i);
            if (currentCharacter == '\\' && i + 1 < taskLine.length()) {
                char escapedCharacter = taskLine.charAt(i + 1);
                if (escapedCharacter == '\\' || escapedCharacter == '|') {
                    currentField.append(escapedCharacter);
                    i++;
                    continue;
                }
            }
            if (taskLine.startsWith(" | ", i)) {
                fields.add(currentField.toString());
                currentField.setLength(0);
                i += 2;
            } else {
                currentField.append(currentCharacter);
            }
        }
        fields.add(currentField.toString());
        return fields;
    }

    /**
     * Rejects required storage fields that contain no visible text.
     *
     * @param value field value
     * @param fieldName user-facing name of the field
     * @throws DuckException if the value is blank
     */
    private void requireNonBlank(String value, String fieldName) throws DuckException {
        if (value.isBlank()) {
            throw new DuckException("the " + fieldName + " cannot be empty.");
        }
    }

    /**
     * Parses a canonical deadline date from a saved record.
     *
     * @param dateText date in yyyy-MM-dd format
     * @return parsed date
     * @throws DuckException if the text is not a valid date from year 0001 to 9999
     */
    private LocalDate parseDeadlineDate(String dateText) throws DuckException {
        if (!DEADLINE_DATE_PATTERN.matcher(dateText).matches()) {
            throw new DuckException(INVALID_SAVED_DATE_MESSAGE);
        }

        try {
            LocalDate date = LocalDate.parse(dateText, DEADLINE_DATE_FORMAT);
            if (date.getYear() == 0) {
                throw new DuckException(INVALID_SAVED_DATE_MESSAGE);
            }
            return date;
        } catch (DateTimeParseException e) {
            throw new DuckException(INVALID_SAVED_DATE_MESSAGE);
        }
    }
}
