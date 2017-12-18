import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Harvests emali adresses from text files.
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
    private static List<String> parseLines(List<String> lines) {
        // Email addresses follow complex formats that are hard and slow to find and validate correctly (see RFC 5322).
        // For now we use a wildly simplified regular expression for finding addresses.
        Pattern pattern = Pattern.compile("[a-zA-Z.-_]+@[a-zA-Z.-_]+");
        List<String> emailAddresses = new ArrayList<String>();
        for (String line: lines) {
            for (String emailAddress: parse(line)) {
                emailAddresses.add(emailAddress);

            }
        }

        Collections.sort(emailAddresses, new EmailAddressComparator());

        return emailAddresses;
    }

    /**
     * Parses a line and returns the email addresses.
     *
     * @param line The source line.
     *
     * @return The email addresses.
     */
    private static List<String> parse(String line) {
        List<String> emailAddresses = new ArrayList<String>();
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            emailAddresses.add(matcher.group());

        }

        return emailAddresses;
    }

    public static void main(String[] args) {
        if (1 != args.length) {
            System.out.println("Exactly one argument must be given.");
            System.exit(1);
        }
        String sourceFilePath = args[0];
        try {
            List<String> lines = Files.readAllLines(Paths.get(sourceFilePath).toAbsolutePath());
            System.out.println(String.format("Parsing %s...", sourceFilePath));
            for (String emailAddress: parseLines(lines)) {
                System.out.println(emailAddress);
            }
        }
        catch (IOException e) {
            System.out.println(String.format("Could not read source file '%s': %s", sourceFilePath, e.getMessage()));
            System.exit(2);
        }
    }

}
