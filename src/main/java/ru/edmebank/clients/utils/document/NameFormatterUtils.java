package ru.edmebank.clients.utils.document;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import com.github.petrovich4j.Petrovich;
import lombok.experimental.UtilityClass;

import static com.github.petrovich4j.NameType.FirstName;
import static com.github.petrovich4j.NameType.LastName;
import static com.github.petrovich4j.NameType.PatronymicName;
import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@UtilityClass
public class NameFormatterUtils {
    private static final Petrovich petrovich = new Petrovich();

    public static String fullName(String lastName, String firstName, String middleName) {
        return formatName(lastName, firstName, middleName, false);
    }

    public static String shortName(String lastName, String firstName, String middleName) {
        return formatName(lastName, firstName, middleName, true);
    }

    public static String declineFullName(String lastName, String firstName, String middleName,
                                         Gender gender, Case nameCase) {
        lastName = defaultIfNull(lastName, "");
        firstName = defaultIfNull(firstName, "");
        middleName = defaultIfNull(middleName, "");

        if (lastName.isEmpty() || firstName.isEmpty() || gender == null || nameCase == null) {
            return "";
        }

        String last = petrovich.say(lastName, LastName, gender, nameCase);
        String first = petrovich.say(firstName, FirstName, gender, nameCase);
        String middle = isNotEmpty(middleName)
                ? petrovich.say(middleName, PatronymicName, gender, nameCase)
                : null;

        return format("%s %s%s", last, first, isNotEmpty(middle) ? " " + middle : "").trim();
    }

    private static String formatName(String lastName, String firstName, String middleName, boolean isShort) {
        lastName = defaultIfNull(lastName, "");
        firstName = defaultIfNull(firstName, "");

        if (lastName.isEmpty() || firstName.isEmpty()) {
            return "";
        }

        if (isNotEmpty(middleName)) {
            return isShort
                    ? format("%s %s.%s.", lastName, firstName.charAt(0), middleName.charAt(0)).trim()
                    : format("%s %s %s", lastName, firstName, middleName).trim();
        }
        return isShort
                ? format("%s %s.", lastName, firstName.charAt(0)).trim()
                : format("%s %s", lastName, firstName).trim();
    }
}
