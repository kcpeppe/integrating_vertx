package com.kodewerk.safepoint.parser;

public interface Tokens {

    String DECIMAL_POINT = "(?:\\.|,)";
    String INTEGER = "\\d+";
    String REAL_NUMBER = INTEGER + DECIMAL_POINT + INTEGER;
    String TIME_VALUE = "(" + REAL_NUMBER + ")";
    String TIME_UNITS = "(seconds)";
    String TIME_STAMP = TIME_VALUE + " " + TIME_UNITS;

}
