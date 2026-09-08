package duck.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests canonical deadline-date parsing shared by commands and storage.
 */
public class DeadlineDateParserTest {

    @Test
    public void parse_validBoundaryDates_returnsParsedDates() {
        assertEquals(LocalDate.of(1, 1, 1), DeadlineDateParser.parse("0001-01-01"));
        assertEquals(LocalDate.of(2024, 2, 29), DeadlineDateParser.parse("2024-02-29"));
        assertEquals(LocalDate.of(9999, 12, 31), DeadlineDateParser.parse("9999-12-31"));
    }

    @Test
    public void parse_invalidDates_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DeadlineDateParser.parse("2026-02-30"));
        assertThrows(DateTimeParseException.class, () -> DeadlineDateParser.parse("2026-8-3"));
        assertThrows(DateTimeParseException.class, () -> DeadlineDateParser.parse("0000-01-01"));
    }
}
