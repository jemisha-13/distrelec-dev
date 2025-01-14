/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.eventtracking.ws.controllers.rest;

import de.hybris.eventtracking.ws.services.RawEventEnricher;
import de.hybris.platform.util.Config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * @author stevo.slavic
 *
 */
@Controller("defaultEventsController")
@RequestMapping(value = "/events")
public class EventsController
{

    private static final Logger LOG = Logger.getLogger(EventsController.class);

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER_VALUE_CONF_PROPERTY = "eventtrackingwsaddon.events_endpoint.ok_response.access_control_allow_origin_header_value";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER_DEFAULT_VALUE = "*";
    private static final String EVENTS_ENDPOINT_ENABLED_CONF_PROPERTY = "eventtrackingwsaddon.events_endpoint.enabled";

    private final QueueChannel rawTrackingEventsChannel;

    private final RawEventEnricher rawEventEnricher;

    public EventsController(final QueueChannel rawTrackingEventsChannel, final RawEventEnricher rawEventEnricher)
    {
        this.rawTrackingEventsChannel = rawTrackingEventsChannel;
        this.rawEventEnricher = rawEventEnricher;
    }

    @RequestMapping(method = {RequestMethod.POST,RequestMethod.OPTIONS}, consumes = "application/json")
    public ResponseEntity<String> trackEvent(final HttpServletRequest request,final HttpServletResponse response ) throws IOException
    {
        if (Config.getBoolean(EVENTS_ENDPOINT_ENABLED_CONF_PROPERTY, true))
        {
            final String body = extractBody(request);
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Events endpoint handling track event request with body: \n" + body);
            }

            final String payload = rawEventEnricher.enrich(body, request);

            forwardForProcessing(payload);
        }
        else
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Events endpoint is disabled. Ignoring request.");
            }
        }

        return ok(response);
    }

    protected String extractBody(final HttpServletRequest request) throws IOException
    {
        return IOUtils.toString(request.getReader());
    }

    protected void forwardForProcessing(final String payload)
    {
        final Message<String> message = new GenericMessage<String>(payload);
        rawTrackingEventsChannel.send(message);
    }

    protected ResponseEntity<String> ok(HttpServletResponse response)
    {
        final HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

}

