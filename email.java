import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Compares email addresses by hostname.
 */
class EmailAddressComparator implements Comparator<String> {
    @Override
    public int compare(String a, String b) {
        String aHost = a.substring(a.lastIndexOf("@"));
        String bHost = b.substring(b.lastIndexOf("@"));
        return a.compareTo(b);
    }
}

/**
 * Harvests email adresses from text files.
 */
class Email {

    /**
     * The regular expression to match an email address in a larger text.
     *
     * Email addresses follow complex formats that are hard and slow to find and validate correctly (see RFC 5322). For
     * now we use a wildly simplified regular expression for finding addresses.
     */
    private static Pattern pattern = Pattern.compile("[a-zA-Z.-_]+@[a-zA-Z.-_]+");

    /**
     * Parses multiple source lines and returns the email addresses.
     *
     * @param lines The source lines.
     *
     * @return The email addresses.
     */
    private static Stream<String> parseLines(Stream<String> lines) {
        return lines.map(Email::parse)
                // Flatten Stream<Stream<String>> to Stream<String>.
                .reduce((a, b) -> Stream.concat(a, b))
                // Provide a default value.
                .orElse(Stream.<String>empty())
                .sorted(new EmailAddressComparator());
    }

    /**
     * Parses a line and returns the email addresses.
     *
     * @param line The source line.
     *
     * @return The email addresses.
     */
    private static Stream<String> parse(String line) {
        List<String> emailAddresses = new ArrayList<String>();
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            emailAddresses.add(matcher.group());
        }

        // The regex package did not receive stream support until Java 1.9, and we support 1.8, so convert manually.
        return emailAddresses.stream();
    }

    public static void main(String[] args) {
        if (1 != args.length) {
            System.out.println("Exactly one argument must be given.");
            System.exit(1);
        }
        String sourceFilePath = args[0];
        try {
            System.out.println(String.format("Parsing %s...", sourceFilePath));
            Stream<String> lines = Files.lines(Paths.get(sourceFilePath).toAbsolutePath());
            parseLines(lines).forEach(System.out::println);
        }
        catch (IOException e) {
            System.out.println(String.format("Could not read source file '%s': %s", sourceFilePath, e.getMessage()));
            System.exit(2);
        }
    }

}
