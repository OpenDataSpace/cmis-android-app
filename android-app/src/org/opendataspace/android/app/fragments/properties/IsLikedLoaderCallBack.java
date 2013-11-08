/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.opendataspace.android.app.fragments.properties;


import org.opendataspace.android.app.R;
import org.opendataspace.android.asynchronous.IsLikedLoader;
import org.opendataspace.android.asynchronous.LikeLoader;
import org.opendataspace.android.asynchronous.LoaderResult;
import org.opendataspace.android.cmisapi.model.Node;
import org.opendataspace.android.cmisapi.session.AlfrescoSession;
import org.opendataspace.android.commonui.fragments.BaseLoaderCallback;
import org.opendataspace.android.commonui.manager.MessengerManager;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class IsLikedLoaderCallBack extends BaseLoaderCallback implements LoaderCallbacks<LoaderResult<Boolean>>
{
    private static final String TAG = "IsLikedLoaderCallBack";

    private Node node;

    private ImageView likeButton;

    private View progressView;

    public IsLikedLoaderCallBack(AlfrescoSession session, Activity context, Node node)
    {
        super();
        this.session = session;
        this.context = context;
        this.node = node;
    }

    @Override
    public Loader<LoaderResult<Boolean>> onCreateLoader(int id, Bundle args)
    {
        boolean isCreate = false;
        if (args != null)
        {
            isCreate = args.getBoolean(IS_CREATE);
        }

        if (!isCreate)
        {
            return new IsLikedLoader(context, session, node);
        }
        else
        {
            return new LikeLoader(context, session, node);
        }
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<Boolean>> arg0, LoaderResult<Boolean> isLiked)
    {
        if (progressView != null)
        {
            progressView.setVisibility(View.GONE);
        }
        if (isLiked.getData() == null)
        {
            Log.e(TAG, Log.getStackTraceString(isLiked.getException()));
            MessengerManager.showToast(context, R.string.error_retrieve_likes);
        }
        else if (isLiked.getData())
        {
            likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
        }
        else
        {
            likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unlike));
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<Boolean>> arg0)
    {

    }

    public void setImageButton(ImageView mi)
    {
        this.likeButton = mi;
    }

    public void setProgressView(View v)
    {
        this.progressView = v;
    }

    private static final String IS_CREATE = "isCreate";

    public void execute(boolean isCreate)
    {
        int id = (isCreate) ? LikeLoader.ID : IsLikedLoader.ID;

        if (progressView != null)
        {
            progressView.setVisibility(View.VISIBLE);
        }

        Bundle b = new Bundle();
        b.putBoolean(IS_CREATE, isCreate);

        if (getLoaderManager().getLoader(id) == null)
        {
            getLoaderManager().initLoader(id, b, this);
        }
        getLoaderManager().restartLoader(id, b, this);
        getLoaderManager().getLoader(id).forceLoad();

    }
}