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
package org.opendataspace.android.app.intent;

public interface PublicIntent
{

    String ACTION_VIEW = "org.opendataspace.android.app.intent.ACTION_VIEW";

    String EXTRA_NODE = "org.opendataspace.android.app.intent.EXTRA_NODE";

    String EXTRA_CONTENT = "org.opendataspace.android.app.intent.EXTRA_CONTENT";

    String EXTRA_FOLDER = "org.opendataspace.android.app.intent.EXTRA_FOLDER";
    
    String EXTRA_FOLDER_ID = "org.opendataspace.android.app.intent.EXTRA_FOLDER_ID";

    String NODE_TYPE = "org.opendataspace.android.app/object.node";
    
    String EXTRA_DOCUMENT = "org.opendataspace.android.app.intent.EXTRA_DOCUMENT";
    
    String EXTRA_DOCUMENT_ID = "org.opendataspace.android.app.intent.EXTRA_DOCUMENT_ID";
    
    String EXTRA_DATA = "org.opendataspace.android.app.intent.EXTRA_DATA";
    
    String EXTRA_FILE = "org.opendataspace.android.app.intent.EXTRA_FILE";
    
    String EXTRA_FILE_PATH = "org.opendataspace.android.app.intent.EXTRA_FILE_PATH";


    // REQUEST CODE
    int REQUESTCODE_FILEPICKER = 128;

    int REQUESTCODE_SAVE_BACK = 129;

    int REQUESTCODE_CREATE = 130;

    int REQUESTCODE_DECRYPTED = 131;
}
