package ru.edmebank.clients.utils.document;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import com.github.petrovich4j.NameType;
import com.github.petrovich4j.Petrovich;
import lombok.experimental.UtilityClass;

import static java.lang.String.format;

@UtilityClass
public class NameFormatterUtils {
    private static final Petrovich petrovich = new Petrovich();

    public static String fullName(String lastName, String firstName, String middleName) {
        return format("%s %s %s", lastName, firstName, middleName);
    }

    public static String shortName(String lastName, String firstName, String middleName) {
        return middleName != null && !middleName.isEmpty()
                ? format("%s %s. %s.", lastName, firstName.charAt(0), middleName.charAt(0))
                : format("%s %s.", lastName, firstName.charAt(0));
    }

    public static String declineFullName(String lastName, String firstName, String middleName,
                                         Gender gender, Case nameCase) {
        return format("%s %s %s",
                petrovich.say(lastName, NameType.LastName, gender, nameCase),
                petrovich.say(firstName, NameType.FirstName, gender, nameCase),
                petrovich.say(middleName, NameType.PatronymicName, gender, nameCase));
    }
}
