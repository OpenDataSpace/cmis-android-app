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
package org.alfresco.mobile.android.application.fragments.workflow.process;

import org.alfresco.mobile.android.api.asynchronous.AbstractPagingLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.Task;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.opendataspace.android.ui.logging.OdsLog;

import android.content.Context;

/**
 * @author jpascal
 */
public class ProcessTasksLoader extends AbstractPagingLoader<LoaderResult<PagingResult<Task>>>
{
    /** Unique SitesLoader identifier. */
    public static final int ID = ProcessTasksLoader.class.hashCode();

    private Process process;

    private String processId;

    public ProcessTasksLoader(Context context, AlfrescoSession session, String processId)
    {
        super(context);
        this.session = session;
        this.processId = processId;
    }

    @Override
    public LoaderResult<PagingResult<Task>> loadInBackground()
    {
        LoaderResult<PagingResult<Task>> result = new LoaderResult<PagingResult<Task>>();
        PagingResult<Task> pagingResult = null;

        try
        {
            process = session.getServiceRegistry().getWorkflowService().getProcess(processId);
            if (process != null)
            {
                pagingResult = session.getServiceRegistry().getWorkflowService().getTasks(process, listingContext);
            }
        }
        catch (Exception e)
        {
            OdsLog.exw("ProcessTasksLoader", e);
            result.setException(e);
        }

        result.setData(pagingResult);

        return result;
    }
}