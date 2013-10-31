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
package org.opendataspace.android.app.security;

import org.opendataspace.android.app.R;
import org.opendataspace.android.app.fragments.DisplayUtils;
import org.opendataspace.android.app.fragments.FragmentDisplayer;
import org.opendataspace.android.app.fragments.upload.UploadFormFragment;
import org.opendataspace.android.app.preferences.PasscodePreferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PassCodeActivity extends Activity
{
    public static final int REQUEST_CODE_PASSCODE = 48976;

    // ///////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_left_panel);

        PassCodeDialogFragment f = PassCodeDialogFragment.requestPasscode();
        FragmentDisplayer.replaceFragment(this, f, DisplayUtils.getLeftFragmentId(this), UploadFormFragment.TAG, false,
                false);
    }

    public static void requestUserPasscode(Activity activity)
    {
        if (PasscodePreferences.hasPasscodeEnable(activity))
        {
            activity.startActivityForResult(new Intent(activity, PassCodeActivity.class), REQUEST_CODE_PASSCODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PassCodeActivity.REQUEST_CODE_PASSCODE && resultCode == RESULT_CANCELED)
        {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);
        finish();
    }

}
