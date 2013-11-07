/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * 
 * This file is part of Alfresco Mobile for Android.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.opendataspace.android.app.exception;

import java.net.UnknownHostException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.opendataspace.android.app.R;
import org.opendataspace.android.app.preferences.GeneralPreferences;
import org.opendataspace.android.app.utils.ConnectivityUtils;

import android.content.Context;
import android.util.Log;

/**
 * Helper class to find the right user message to display when an exception has
 * occured.
 * 
 * @author Jean Marie Pascal
 */
public final class SessionExceptionHelper
{

    private SessionExceptionHelper()
    {
    }

    /**
     * Return user friendly message Id for a specific exception.
     * 
     * @param context :
     * @param e : exception occured
     * @return message Id
     */
    public static int getMessageId(final Context context, final Exception e)
    {
        int messageId = R.string.error_session_creation;

        // Case where the user has no right (server configuration or wrong
        // username/password)
        if (e.getCause() instanceof CmisUnauthorizedException)
            messageId = R.string.error_session_unauthorized;
        else if (e.getCause() instanceof CmisObjectNotFoundException)
            messageId = R.string.error_session_service_url;
        else if (e.getCause() instanceof CmisRuntimeException && e.getCause().getMessage().contains("Service Temporarily Unavailable"))
            messageId = R.string.error_session_service_unavailable;
        else if (e.getCause() instanceof CmisRuntimeException && e.getCause().getMessage().contains("Found"))
            messageId = R.string.error_session_port;
        else if (e.getCause() instanceof CmisConnectionException
                && e.getCause().getCause() instanceof UnknownHostException)
        {
            if (ConnectivityUtils.hasInternetAvailable(context))
                messageId = R.string.error_session_hostname;
            else
                messageId = R.string.error_session_nodata;
        }
        // Case where missing certificate / untrusted certificate
        else if (e.getCause() instanceof CmisConnectionException
                && e.getCause().getCause() instanceof SSLHandshakeException
                && (e.getCause().getCause().getCause() instanceof CertPathValidatorException || e.getCause().getCause()
                        .getCause() instanceof CertificateException)
                        && e.getCause().getCause().getCause().getMessage()
                        .contains("Trust anchor for certification path not found."))
            messageId = R.string.error_session_certificate;
        else if (e.getCause() instanceof CmisConnectionException
                && e.getCause().getCause() instanceof SSLHandshakeException
                && e.getCause().getCause().getCause() instanceof CertificateException
                && e.getCause().getCause().getCause().getMessage()
                .contains("Could not validate certificate: current time:"))
            messageId = R.string.error_session_certificate_expired;
        else if (e.getCause() instanceof CmisConnectionException
                && e.getCause().getCause() instanceof SSLHandshakeException
                && (e.getCause().getCause().getCause() instanceof CertificateExpiredException || e.getCause()
                        .getCause().getCause() instanceof CertificateNotYetValidException))
            messageId = R.string.error_session_certificate_expired;
        else if (e.getCause() instanceof CmisConnectionException
                && e.getCause().getCause() instanceof SSLHandshakeException
                && e.getCause().getCause().getCause() instanceof CertificateException)
            messageId = R.string.error_session_certificate;
        else if (e.getCause() instanceof CmisConnectionException
                && e.getCause().getCause() instanceof SSLHandshakeException)
            messageId = R.string.error_session_ssl;
        else if (e.getCause() instanceof CmisConnectionException && e.getCause().getMessage().contains("Cannot access"))
        {
            Log.e("", "Message  "+ e.getCause().getMessage());
            if(GeneralPreferences.getSertificatePref(context) != 1){
                //Log.e("", "Set ERROR");
                messageId = R.string.error_sertif_valid;
                GeneralPreferences.setSertificatePref(-1,context);
            } else
                messageId = R.string.error_session_notfound;
        } else
            messageId = R.string.error_session_creation;

        return messageId;
    }

}
