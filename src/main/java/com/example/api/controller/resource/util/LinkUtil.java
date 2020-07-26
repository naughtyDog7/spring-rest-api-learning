package com.example.api.controller.resource.util;

public class LinkUtil {
    public static String createLinkHeader(String uri, String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}
