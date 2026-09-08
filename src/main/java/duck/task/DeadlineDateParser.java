package duck.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Parses the canonical date format shared by deadline commands and saved records.
 */
public final class DeadlineDateParser {
    /** Exact, ASCII-only shape accepted for deadline dates. */
    private static final Pattern DEADLINE_DATE_PATTERN =
            Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

    /** Strict formatter used to parse deadline dates. */
    private static final DateTimeFormatter DEADLINE_DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private DeadlineDateParser() {
    }

    /**
     * Parses a canonical deadline date. The shape check rejects abbreviated, signed,
     * extended, and non-ASCII years before strict calendar validation is attempted.
     *
     * @param dateText Date in yyyy-MM-dd format.
     * @return Parsed date.
     * @throws DateTimeParseException If the text is not a valid date from year 0001 to 9999.
     */
    public static LocalDate parse(String dateText) {
        if (!DEADLINE_DATE_PATTERN.matcher(dateText).matches()) {
            throw invalidDate(dateText);
        }

        LocalDate date = LocalDate.parse(dateText, DEADLINE_DATE_FORMAT);
        if (date.getYear() == 0) {
            throw invalidDate(dateText);
        }
        return date;
    }

    /** Creates a consistent exception for deadline-specific validation failures. */
    private static DateTimeParseException invalidDate(String dateText) {
        return new DateTimeParseException("Invalid deadline date.", dateText, 0);
    }
}
