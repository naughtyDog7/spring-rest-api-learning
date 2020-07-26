package com.example.api.repository.util;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value(staticConstructor = "from")
public class SearchCriteria {
    String key;
    String operation;
    Object value;

    public static final String pattern = "(\\w+)([:<>])(\\w*)";

    public static List<SearchCriteria> fromString(String s) {
        Pattern p = Pattern.compile(pattern + ",");
        Matcher m = p.matcher(s + ",");
        List<SearchCriteria> result = new ArrayList<>();
        while (m.find()) {
            result.add(SearchCriteria.from(m.group(1), m.group(2), m.group(3)));
        }
        return result;
    }
}
