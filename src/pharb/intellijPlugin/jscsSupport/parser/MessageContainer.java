package pharb.intellijPlugin.jscsSupport.parser;


public class MessageContainer {

    /**
     * Absolute path or "input".
     */
    public final String fileName;

    public final String message;

    /**
     * Starts at 1.
     */
    public final int line;

    /**
     * Starts at 1.
     */
    public final int column;

    public final String severity;

    public final String source;

    public final String ruleName;

    public MessageContainer(String fileName, String rawMessage, int line, int column,
                            String severity, String source) {
        this.fileName = fileName;
        this.line = line;
        this.column = column;
        this.severity = severity;
        this.source = source;

        String[] splitMessage = rawMessage.split(": ");
        this.ruleName = splitMessage[0];
        this.message = splitMessage[1];
    }

    public String getDisplayMessage() {
        return String.format("%s: %s in line %d in column %d", source, message, line, column);
    }
}
