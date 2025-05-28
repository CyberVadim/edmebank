package ru.edmebank.clients.fw.spellers;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;

import static ru.edmebank.clients.utils.spellers.NameFormatterUtils.formDeclineName;
import static ru.edmebank.clients.utils.spellers.NameFormatterUtils.formName;

public interface NameFormatter {
    String fullName(String lastName, String firstName, String middleName);

    String shortName(String lastName, String firstName, String middleName);

    String declineFullName(String lastName, String firstName, String middleName, Gender gender, Case nameCase);

    default String formatName(String lastName, String firstName, String middleName, boolean isShort) {
        return formName(lastName, firstName, middleName, isShort);
    }

    default String formatDeclineFullName(String lastName, String firstName, String middleName,
                                         Gender gender, Case nameCase) {
        return formDeclineName(lastName, firstName, middleName, gender, nameCase);
    }
}
