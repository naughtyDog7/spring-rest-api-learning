package com.example.api.controller.resource.listener;

import com.example.api.controller.resource.event.SingleResourceRetrievedEvent;
import com.example.api.controller.resource.util.LinkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class SingleResourceRetrievedDiscoverabilityListener {

    @EventListener
    public void onSingleResourceRetrieved(SingleResourceRetrievedEvent event) {
        HttpServletResponse response = event.getResponse();
        addLinkOnSingleResourceRetrieval(response);
    }

    private void addLinkOnSingleResourceRetrieval(HttpServletResponse response) {
        String requestUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build().toUri().toASCIIString();
        int positionOfLastSlash = requestUrl.lastIndexOf('/');
        String uriForResourceCreation = requestUrl.substring(0, positionOfLastSlash);
        String linkHeaderValue = LinkUtil
                .createLinkHeader(uriForResourceCreation, "collection");
        response.addHeader(HttpHeaders.LINK, linkHeaderValue);
    }
}
