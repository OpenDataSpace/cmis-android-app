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
package org.alfresco.mobile.android.application.fragments.browser;

import java.util.List;

import org.opendataspace.android.app.R;
import org.alfresco.mobile.android.application.accounts.Account;
import org.alfresco.mobile.android.application.accounts.AccountManager;
import org.alfresco.mobile.android.application.utils.SessionUtils;
import org.alfresco.mobile.android.application.utils.UIUtils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PathAdapter extends ArrayAdapter<String>
{
    private String item;

    private Account account;

    public PathAdapter(Activity context, int textViewResourceId, List<String> objects)
    {
        super(context, textViewResourceId, objects);
        this.account = SessionUtils.getAccount(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.app_path_shortcut, null);
        }
        item = getItem(position);
        if (item != null)
        {
            ((TextView) v.findViewById(R.id.bottomtext)).setText(item + "  ");
            ((TextView) v.findViewById(R.id.toptext)).setVisibility(View.GONE);
            ((ImageView) v.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
        }
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.app_header_row, null);
        }
        item = getItem(position);
        if (item != null && v != null)
        {
            if (AccountManager.getInstance(getContext()).hasMultipleAccount())
            {
                ((TextView) v.findViewById(R.id.toptext)).setText(UIUtils.getAccountLabel(account));
                v.findViewById(R.id.toptext).setVisibility(View.VISIBLE);
            }
            else
            {
                v.findViewById(R.id.toptext).setVisibility(View.GONE);
            }

            ((TextView) v.findViewById(R.id.bottomtext)).setText(getItem(position));
            ((ImageView) v.findViewById(R.id.icon)).setVisibility(View.GONE);
        }
        return v;
    }
}
