/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * 
 *  This file is part of Alfresco Mobile for Android.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.application.operations.sync.node;

import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.application.operations.OperationRequest;
import org.alfresco.mobile.android.application.operations.sync.SynchroProvider;
import org.alfresco.mobile.android.application.operations.sync.SynchroSchema;
import org.alfresco.mobile.android.application.operations.sync.impl.AbstractSyncOperationThread;
import org.opendataspace.android.ui.logging.OdsLog;

import android.content.Context;
import android.database.Cursor;

public abstract class SyncNodeOperationThread<T> extends AbstractSyncOperationThread<T>
{
    private static final String TAG = SyncNodeOperationThread.class.getName();

    protected String nodeIdentifier;

    protected String parentFolderIdentifier;

    protected Node node;

    protected Folder parentFolder;

    protected Cursor cursor;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public SyncNodeOperationThread(Context context, OperationRequest request)
    {
        super(context, request);
        if (request instanceof SyncNodeOperationRequest)
        {
            this.nodeIdentifier = ((SyncNodeOperationRequest) request).getNodeIdentifier();
            this.parentFolderIdentifier = ((SyncNodeOperationRequest) request).getParentFolderIdentifier();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // ///////////////////////////////////////////////////////////////////////////
    protected LoaderResult<T> doInBackground()
    {
        try
        {
            super.doInBackground();

            cursor = context.getContentResolver().query(SynchroProvider.CONTENT_URI, SynchroSchema.COLUMN_ALL,
                    SynchroProvider.getAccountFilter(acc) + " AND " + SynchroSchema.COLUMN_NODE_ID + " LIKE '" + nodeIdentifier + "%'", null, null);

            try
            {
                node = session.getServiceRegistry().getDocumentFolderService().getNodeByIdentifier(nodeIdentifier);
                parentFolder = retrieveParentFolder();
            }
            catch (AlfrescoServiceException e)
            {
                // Do Nothing
            }


            if (listener != null)
            {
                listener.onPreExecute(this);
            }
        }
        catch (Exception e)
        {
            OdsLog.exw(TAG, e);
        }
        return new LoaderResult<T>();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    protected Folder retrieveParentFolder()
    {
        if (parentFolder == null && parentFolderIdentifier != null && !parentFolderIdentifier.isEmpty())
        {
            parentFolder = (Folder) session.getServiceRegistry().getDocumentFolderService()
                    .getNodeByIdentifier(parentFolderIdentifier);
        }

        if (parentFolder == null && node != null)
        {
            parentFolder = (Folder) session.getServiceRegistry().getDocumentFolderService().getParentFolder(node);
        }

        return parentFolder;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS
    // ///////////////////////////////////////////////////////////////////////////
    public Node getNode()
    {
        return node;
    }

    public Folder getParentFolder()
    {
        return parentFolder;
    }
}