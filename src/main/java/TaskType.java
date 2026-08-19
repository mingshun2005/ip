/**
 * Represents the fixed types of tasks supported by Duck.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with the given display icon.
     *
     * @param icon short icon used when displaying the task type
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the display tag for this task type.
     *
     * @return task type tag, such as [T]
     */
    public String getTag() {
        return "[" + this.icon + "]";
    }
}
