package com.example.api.controller.resource.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletResponse;

public class SingleResourceRetrievedEvent extends ApplicationEvent {
    @Getter
    private final HttpServletResponse response;

    public SingleResourceRetrievedEvent(Object source, HttpServletResponse response) {
        super(source);
        this.response = response;
    }


}
