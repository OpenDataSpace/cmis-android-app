/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * 
 * This file is part of the Alfresco Mobile SDK.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.opendataspace.android.cmisapi.model.impl;

import java.io.Serializable;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.opendataspace.android.cmisapi.constants.CloudConstant;
import org.opendataspace.android.cmisapi.constants.OnPremiseConstant;

/**
 * Representation of a request to join moderated sites.
 * 
 * @since 1.1.0
 * @author Jean Marie Pascal
 */
public class JoinSiteRequestImpl implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String identifier;

    private String siteShortName;

    private String message;

    /**
     * Parse Json Response from Alfresco REST API to create a JoinSiteRequest.
     * 
     * @param json : json response that contains data from the repository
     * @return JoinSiteRequest object that contains essential information about
     *         it.
     */
    public static JoinSiteRequestImpl parseJson(Map<String, Object> json)
    {
        JoinSiteRequestImpl request = new JoinSiteRequestImpl();
        request.identifier = JSONConverter.getString(json, OnPremiseConstant.INVITEID_VALUE);
        request.siteShortName = JSONConverter.getString(json, OnPremiseConstant.RESOURCENAME_VALUE);
        request.message = JSONConverter.getString(json, OnPremiseConstant.INVITEECOMMENTS_VALUE);

        return request;
    }

    /**
     * Parse Json Response from Alfresco Public API to create a JoinSiteRequest.
     * 
     * @param json : json response that contains data from the repository
     * @return JoinSiteRequest object that contains essential information about
     *         it.
     */
    @SuppressWarnings("unchecked")
    public static JoinSiteRequestImpl parsePublicAPIJson(Map<String, Object> json)
    {
        JoinSiteRequestImpl request = new JoinSiteRequestImpl();

        Map<String, Object> jo = (Map<String, Object>) json.get(CloudConstant.SITE_VALUE);

        request.identifier = JSONConverter.getString(jo, CloudConstant.GUID_VALUE);
        request.siteShortName = JSONConverter.getString(jo, CloudConstant.ID_VALUE);
        request.message = JSONConverter.getString(json, CloudConstant.MESSAGE_VALUE);

        return request;
    }

    /**
     * Returns the unique identifier for the join request.
     * 
     * @return the identifier
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * Returns the short name of the site the join request was for.
     * 
     * @return the site short name
     */
    public String getSiteShortName()
    {
        return siteShortName;
    }

    /**
     * Returns the message the user provided with their join request.
     * 
     * @return the message.
     */
    public String getMessage()
    {
        return message;
    }

}