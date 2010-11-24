/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;
import org.mule.DefaultMuleEvent;
import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.api.routing.RoutingException;
import org.mule.routing.outbound.AbstractOutboundRouter;


/**
 * RoundRobin divides the messages it receives among its target routes in round-robin fashion. This includes messages
 * received on all threads, so there is no guarantee that messages received from a splitter are sent to consecutively
 * numbered targets. 
 */
public class RoundRobin extends AbstractOutboundRouter implements MessageProcessor
{
    /** Index of target route to use */
    AtomicInteger index = new AtomicInteger(0);

    /**
     *  Process the event using the next target route in sequence
     */
    public MuleEvent route(MuleEvent event) throws MessagingException
    {
        int index = getAndIncrementModuloN(routes.size());
        if (index < 0)
        {
            throw new CouldNotRouteOutboundMessageException(event, this);
        }
        MessageProcessor mp = routes.get(index);
        MuleEvent toProcess = event;
        if (mp instanceof OutboundEndpoint)
        {
            toProcess = new DefaultMuleEvent(event.getMessage(), (OutboundEndpoint)mp, event.getSession());
        }
        try
        {
            return mp.process(toProcess);
        }
        catch (MuleException ex)
        {
            throw new RoutingException(event, this, ex);
        }
    }

    /**
     * Get the index of the processor to use
     */
    private int getAndIncrementModuloN(int modulus)
    {
        if (modulus == 0)
        {
            return -1;
        }
        while (true)
        {
            int lastIndex = index.get();
            int nextIndex = (lastIndex + 1) % modulus;
            if (index.compareAndSet(lastIndex, nextIndex))
            {
                return nextIndex;
            }
        }
    }

    public boolean isMatch(MuleMessage message) throws MuleException
    {
        return true;
    }
}
