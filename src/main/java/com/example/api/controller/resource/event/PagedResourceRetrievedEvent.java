package com.example.api.controller.resource.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;


@EqualsAndHashCode(callSuper = false)
@Value
public class PagedResourceRetrievedEvent<T> extends ApplicationEvent {
    CollectionModel<T> model;
    Pageable pageable;
    int totalPages;
    String sort;
    boolean asc;

    public PagedResourceRetrievedEvent(Object source, CollectionModel<T> model, Pageable pageable, int totalPages, String sort, boolean asc) {
        super(source);
        this.model = model;
        this.pageable = pageable;
        this.totalPages = totalPages;
        this.sort = sort;
        this.asc = asc;
    }
}
