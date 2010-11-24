/*
 * $Id: DefaultOutboundRouterCollection.java 19640 2010-09-13 22:00:05Z tcarlson $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.cxf.example;

import javax.jws.WebService;

@WebService
public interface HelloWorld
{
    String sayHi(String text);
}