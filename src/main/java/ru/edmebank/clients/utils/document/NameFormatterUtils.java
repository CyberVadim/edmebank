package ru.edmebank.clients.utils.document;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import com.github.petrovich4j.NameType;
import com.github.petrovich4j.Petrovich;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.github.petrovich4j.NameType.FirstName;
import static com.github.petrovich4j.NameType.LastName;
import static com.github.petrovich4j.NameType.PatronymicName;
import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@UtilityClass
public class NameFormatterUtils {
    private static final Petrovich petrovich = new Petrovich();

    public static String formDeclineName(String lastName, String firstName, String middleName,
                                               Gender gender, Case nameCase) {
        lastName = getFormattedName(lastName, LastName, gender, nameCase);
        firstName = getFormattedName(firstName, FirstName, gender, nameCase);
        middleName = getFormattedName(middleName, PatronymicName, gender, nameCase);

        return (lastName.isEmpty() || firstName.isEmpty())
                ? ""
                : format("%s %s %s", lastName, firstName, middleName).trim();
    }

    public static String formName(String lastName, String firstName, String middleName, boolean isShort) {
        lastName = cleanString(lastName);
        firstName = cleanString(firstName);
        middleName = cleanString(middleName);

        return (lastName.isEmpty() || firstName.isEmpty())
                ? ""
                : isNotEmpty(middleName)
                ? (isShort
                ? format("%s %s.%s.", lastName, firstName.charAt(0), middleName.charAt(0)).trim()
                : format("%s %s %s", lastName, firstName, middleName).trim())
                : (isShort
                ? format("%s %s.", lastName, firstName.charAt(0)).trim()
                : format("%s %s", lastName, firstName).trim());
    }

    private static String getFormattedName(String name, NameType nameType, Gender gender, Case nameCase) {
        return Optional
                .ofNullable(name)
                .filter(StringUtils::isNotEmpty)
                .map(n -> petrovich.say(n, nameType, gender, nameCase))
                .orElse("");
    }

    private static String cleanString(String input) {
        return Optional.ofNullable(input)
                .filter(StringUtils::isNotEmpty)
                .orElse("");
    }
}
