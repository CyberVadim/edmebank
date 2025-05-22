package ru.edmebank.clients.utils.currency;

import org.junit.jupiter.api.Test;
import ru.edmebank.contracts.enums.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RussianNumeralUtilsTest {

    private final Currency rub = Currency.RUB;
    private final Currency usd = Currency.USD;

    @Test
    void rubleFor1() {
        assertEquals("рубль",
                RussianNumeralUtils.declension(1, rub.wholeText, rub.wholeForms));
    }

        @Test
    void rubleFor2() {
        assertEquals("рубля",
                RussianNumeralUtils.declension(2, rub.wholeText, rub.wholeForms));
    }

    @Test
    void rubleFor5() {
        assertEquals("рублей",
                RussianNumeralUtils.declension(5, rub.wholeText, rub.wholeForms));
    }

    @Test
    void rubleFor11() {
        assertEquals("рублей",
                RussianNumeralUtils.declension(11, rub.wholeText, rub.wholeForms));
    }

    @Test
    void rubleFor21() {
        assertEquals("рубль",
                RussianNumeralUtils.declension(21, rub.wholeText, rub.wholeForms));
    }

    @Test
    void kopeykaFor1() {
        assertEquals("копейка",
                RussianNumeralUtils.declension(1, rub.fractionalText, rub.fractionalForms));
    }

    @Test
    void kopeykaFor3() {
        assertEquals("копейки",
                RussianNumeralUtils.declension(3, rub.fractionalText, rub.fractionalForms));
    }

    @Test
    void kopeykaFor10() {
        assertEquals("копеек",
                RussianNumeralUtils.declension(10, rub.fractionalText, rub.fractionalForms));
    }

    @Test
    void dollarFor1() {
        assertEquals("доллар",
                RussianNumeralUtils.declension(1, usd.wholeText, usd.wholeForms));
    }

    @Test
    void dollarFor4() {
        assertEquals("доллара",
                RussianNumeralUtils.declension(4, usd.wholeText, usd.wholeForms));
    }

    @Test
    void dollarFor7() {
        assertEquals("долларов",
                RussianNumeralUtils.declension(7, usd.wholeText, usd.wholeForms));
    }

    @Test
    void dollarFor11() {
        assertEquals("долларов",
                RussianNumeralUtils.declension(11, usd.wholeText, usd.wholeForms));
    }

    @Test
    void dollarFor21() {
        assertEquals("доллар",
                RussianNumeralUtils.declension(21, usd.wholeText, usd.wholeForms));
    }

    @Test
    void centFor1() {
        assertEquals("цент",
                RussianNumeralUtils.declension(1, usd.fractionalText, usd.fractionalForms));
    }

    @Test
    void centFor2() {
        assertEquals("цента",
                RussianNumeralUtils.declension(2, usd.fractionalText, usd.fractionalForms));
    }

    @Test
    void centFor10() {
        assertEquals("центов",
                RussianNumeralUtils.declension(10, usd.fractionalText, usd.fractionalForms));
    }
}