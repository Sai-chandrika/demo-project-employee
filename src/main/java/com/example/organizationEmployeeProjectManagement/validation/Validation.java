package com.example.organizationEmployeeProjectManagement.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 06-04-2023
 */
public class Validation {
    public static Boolean isObjectValid(Object object) {
        if (object != null) return Boolean.TRUE;
        return Boolean.FALSE;
    }
    public static Boolean isValid(Long id) {
        if (id != null && id > 0) return Boolean.TRUE;
        return Boolean.FALSE;
    }
    public static Boolean isValid(Double id) {
        if (id != null && id > 0) return Boolean.TRUE;
        return Boolean.FALSE;
    }
    public static Boolean isValids(Double id) {
        if (id != null && id >= 0) return Boolean.TRUE;
        return Boolean.FALSE;
    }
    public static Boolean isValid(Integer id) {
        if (id != null && id > 0) return Boolean.TRUE;
        return Boolean.FALSE;
    }
    public static Boolean isValid(LocalDate localDate){
        if(localDate !=null)
            return Boolean.TRUE;
        return  Boolean.FALSE;
    }
    public static Boolean isValid(String value) {
        if ((value != null) && !value.trim().isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isValid(List<?> objList) {
        if ((objList != null) && !objList.isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public static Boolean isValid(Set<?> objSet) {
        if ((objSet != null) && !objSet.isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    public static Boolean isValid(Map<?, ?> objMap) {
        if ((objMap != null) && !objMap.isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    public static Boolean isValid(Enumeration<?> objMap) {
        if ((objMap != null) && objMap.hasMoreElements()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    public static Boolean isValid(Page<?> objList) {
        if ((objList != null) && !objList.isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    public static Boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value.describeConstable().isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isValidEmailPattern(String email) {
        String ePattern = "^[a-z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    public static Boolean isValidMobileNumber(String mobileNumber){
        String ePattern=("^(0|9)?[6-9]{1}[0-9]{9}+$");
        Pattern p=Pattern.compile(ePattern);
        Matcher m=p.matcher(mobileNumber);
        return m.matches();
    }
    public static Boolean isValidPassword(String password) {
        String ePattern=("^[a-zA-Z0-9'@&#.\\s]{6,15}$");
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(password);
        return m.matches();
    }


    public static Boolean isValidProjectName(String name){
        String pattern=("[a-zA-Z]+");
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(name);
        return m.matches();
    }



    public static String properTextCase(String stringValue){
        String[] words =stringValue.split("\\s");
        StringBuilder newString = new StringBuilder();
        for(String w: words){
            String first = w.substring(0,1); //First Letter
            String rest = w.substring(1); //Rest of the letters
            newString.append(first.toUpperCase()).append(rest).append(" ");
        }
        return newString.toString().trim();
    }

}

