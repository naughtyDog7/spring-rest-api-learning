package com.example.api.controller.resource.listener;

import com.example.api.controller.StudentController;
import com.example.api.controller.resource.event.PagedResourceRetrievedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class PagedResourceRetrievedListener {

    @EventListener
    public void onModelReceived(PagedResourceRetrievedEvent<?> event) {
        addPageLinks(event.getModel(), event.getPageable(), event.getTotalPages(), event.getSort(), event.isAsc());
    }

    private void addPageLinks(CollectionModel<?> model, Pageable pageable, int totalPages, String sort, boolean asc) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        if (currentPage > 0) {
            model.add(
                    linkToPageable(currentPage - 1, pageSize, sort, asc)
                            .withRel("previous")
            );
        }
        if (currentPage < totalPages) {
            model.add(
                    linkToPageable(currentPage + 1, pageSize, sort, asc)
                            .withRel("next")
            );
        }
        model.add(
                linkToPageable(0, pageSize, sort, asc)
                .withRel("first")
        );
        model.add(
                linkToPageable(totalPages, pageSize, sort, asc)
                .withRel("last")
        );
    }

    private WebMvcLinkBuilder linkToPageable(int page, int size, String sort, boolean asc) {
        return linkTo(methodOn(StudentController.class)
                .studentsPaged(page, size, sort, asc));
    }
}
