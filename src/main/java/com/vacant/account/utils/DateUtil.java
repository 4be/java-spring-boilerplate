package com.vacant.account.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtil {

    public final String CORE_DATE_FORMAT = "yyyyMMdd";
    public final String CORE_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm";
    public final String ISO_DATE_TIME_WITHOUT_NANO_SECOND = "yyyy-MM-dd'T'hh:mm:ssXXX";

    public final ZoneOffset ZONE_OFFSET_WIB = ZoneOffset.ofHours(7);


    public LocalDateTime parseToLocalDateTime(String dateTime, DateTimeFormatter formatter) {
        return LocalDateTime.parse(dateTime, formatter);
    }

    public LocalDate parseToLocalDate(String dateTime) {
        return parseToLocalDateTime(dateTime, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
    }

    public String parseToCoreDateFormat(String dateTime) {
        return parseToLocalDateTime(dateTime, DateTimeFormatter.ISO_DATE_TIME).toLocalDate().format(DateTimeFormatter.ofPattern(CORE_DATE_FORMAT));
    }

    public String coreDateTimeToIso8601(String dateTime) {
        if (ObjectUtils.isEmpty(dateTime)) {
            return "";
        }

        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(CORE_DATE_TIME_FORMAT))
                .atOffset(ZONE_OFFSET_WIB).format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
