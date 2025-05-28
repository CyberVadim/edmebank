package ru.edmebank.clients.fw.spellers;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;

public class NameFormatterImpl implements NameFormatter {
    @Override
    public String fullName(String lastName, String firstName, String middleName) {
        return formatName(lastName, firstName, middleName, false);
    }

    @Override
    public String shortName(String lastName, String firstName, String middleName) {
        return formatName(lastName, firstName, middleName, true);
    }

    @Override
    public String declineFullName(String lastName, String firstName, String middleName, Gender gender, Case nameCase) {
        return formatDeclineFullName(lastName, firstName, middleName, gender, nameCase);
    }
}
