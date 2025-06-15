package ru.edmebank.clients.domain.entity;

import lombok.Getter;
import lombok.Setter;
import ru.edmebank.clients.domain.annotation.SensitiveField;
import ru.edmebank.clients.domain.annotation.SensitiveObject;
import ru.edmebank.contracts.enums.MaskingPattern;

import java.time.LocalDate;

@Getter
@Setter
@SensitiveObject
public class LogClientTest {
    
    @SensitiveField(patterns = MaskingPattern.FULL_NAME)
    private String firstName;
    
    @SensitiveField(patterns = MaskingPattern.FULL_NAME)
    private String lastName;
    
    @SensitiveField(patterns = MaskingPattern.FULL_NAME)
    private String middleName;
    
    @SensitiveField(patterns = MaskingPattern.DATE_YYYY_MM_DD)
    private LocalDate birthDate;
    
    @SensitiveField(patterns = MaskingPattern.PASSPORT_SERIES)
    private String passportSeries;
    
    @SensitiveField(patterns = MaskingPattern.PASSPORT_NUMBER)
    private String passportNumber;
    
    @SensitiveField(patterns = MaskingPattern.PASSPORT_ISSUED_BY)
    private String passportIssuedBy;
    
    @SensitiveField(patterns = MaskingPattern.DATE_YYYY_MM_DD)
    private LocalDate passportIssuedDate;
    
    @SensitiveField(patterns = MaskingPattern.DATE_YYYY_MM_DD)
    private LocalDate passportExpiryDate;
    
    @SensitiveField(patterns = MaskingPattern.SUBDIVISION_CODE)
    private String passportSubdivisionCode;
    
    @SensitiveField(patterns = MaskingPattern.ADDRESS)
    private String address;
    
    @SensitiveField(patterns = MaskingPattern.PHONE)
    private String phone;
    
    @SensitiveField(patterns = MaskingPattern.EMAIL)
    private String email;
    
    @SensitiveField(patterns = {MaskingPattern.INN_10_DIGITS, MaskingPattern.INN_12_DIGITS})
    private String inn;
    
    @SensitiveField(patterns = MaskingPattern.SNILS)
    private String snils;
    
    @SensitiveField(patterns = {MaskingPattern.DATE_DD_MM_YYYY, MaskingPattern.DATE_YYYY_MM_DD})
    private String stringDate;
}
