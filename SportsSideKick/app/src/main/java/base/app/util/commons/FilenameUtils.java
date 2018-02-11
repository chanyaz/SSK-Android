package base.app.util.commons;

public class FilenameUtils {

    private static final int NOT_FOUND = -1;

    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    public static String getName(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    private static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }
}
