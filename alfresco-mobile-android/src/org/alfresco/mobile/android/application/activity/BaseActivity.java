/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * <p/>
 * This file is part of Alfresco Mobile for Android.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.application.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.application.ApplicationManager;
import org.alfresco.mobile.android.application.accounts.Account;
import org.alfresco.mobile.android.application.accounts.AccountManager;
import org.alfresco.mobile.android.application.commons.fragments.SimpleAlertDialogFragment;
import org.alfresco.mobile.android.application.commons.utils.AndroidVersion;
import org.alfresco.mobile.android.application.exception.AlfrescoAppException;
import org.alfresco.mobile.android.application.exception.CloudExceptionUtils;
import org.alfresco.mobile.android.application.fragments.DisplayUtils;
import org.alfresco.mobile.android.application.fragments.FragmentDisplayer;
import org.alfresco.mobile.android.application.fragments.WaitingDialogFragment;
import org.alfresco.mobile.android.application.fragments.browser.ChildrenBrowserFragment;
import org.alfresco.mobile.android.application.intent.IntentIntegrator;
import org.alfresco.mobile.android.application.manager.RenditionManager;
import org.alfresco.mobile.android.application.utils.SessionUtils;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;
import org.alfresco.mobile.android.ui.manager.MessengerManager;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.opendataspace.android.app.R;
import org.opendataspace.android.app.config.OdsConfigManager;
import org.opendataspace.android.app.session.OdsRepositorySession;
import org.opendataspace.android.ui.logging.OdsLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all activities.
 *
 * @author Jean Marie Pascal
 */
public abstract class BaseActivity extends Activity
{
    protected AccountManager accountManager;

    protected LocalBroadcastManager broadcastManager;

    protected ApplicationManager applicationManager;

    protected BroadcastReceiver receiver;

    protected BroadcastReceiver utilsReceiver;

    protected List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>(2);

    protected List<BroadcastReceiver> publicReceivers = new ArrayList<BroadcastReceiver>(2);

    protected Account currentAccount;

    protected RenditionManager renditionManager;

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_LEFT_ICON);

        broadcastManager = LocalBroadcastManager.getInstance(this);
        applicationManager = ApplicationManager.getInstance(this);
        accountManager = applicationManager.getAccountManager();

        IntentFilter filters = new IntentFilter();
        filters.addAction(IntentIntegrator.ACTION_DISPLAY_DIALOG);
        filters.addAction(IntentIntegrator.ACTION_DISPLAY_ERROR);
        filters.addAction(IntentIntegrator.ACTION_CONFIGURATION_BRAND);
        utilsReceiver = new UtilsReceiver();
        receivers.add(utilsReceiver);
        broadcastManager.registerReceiver(utilsReceiver, filters);
    }

    @Override
    protected void onStart()
    {
        if (accountManager == null)
        {
            accountManager = applicationManager.getAccountManager();
        }
        if (applicationManager == null)
        {
            applicationManager = ApplicationManager.getInstance(this);
            applicationManager.setAccountManager(accountManager);
        }
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        for (BroadcastReceiver bReceiver : receivers)
        {
            broadcastManager.unregisterReceiver(bReceiver);
        }

        for (BroadcastReceiver bReceiver : publicReceivers)
        {
            unregisterReceiver(bReceiver);
        }

        receivers.clear();
        publicReceivers.clear();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        rebrand();
    }

    @SuppressLint("NewApi")
    protected void rebrand()
    {
        if (AndroidVersion.isICSOrAbove())
        {
            OdsConfigManager cfg = ApplicationManager.getInstance(this).getOdsConfig();
            Account acc = SessionUtils.getAccount(this);
            Drawable dr = cfg.getBrandingDrawable(this, OdsConfigManager.BRAND_ICON, acc);
            getActionBar().setLogo(dr != null ? dr : getResources().getDrawable(R.drawable.ic_alfresco_logo));
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    public Fragment getFragment(String tag)
    {
        return getFragmentManager().findFragmentByTag(tag);
    }

    protected int getFragmentPlace()
    {
        int id = R.id.left_pane_body;
        if (DisplayUtils.hasCentralPane(this))
        {
            id = R.id.central_pane_body;
        }
        return id;
    }

    protected int getFragmentPlace(boolean forceRight)
    {
        int id = R.id.left_pane_body;
        if (forceRight && DisplayUtils.hasCentralPane(this))
        {
            id = R.id.central_pane_body;
        }
        return id;
    }

    protected boolean isVisible(String tag)
    {
        return getFragmentManager().findFragmentByTag(tag) != null &&
                getFragmentManager().findFragmentByTag(tag).isAdded();
    }

    public void displayWaitingDialog()
    {
        if (getFragmentManager().findFragmentByTag(WaitingDialogFragment.TAG) == null)
        {
            new WaitingDialogFragment().show(getFragmentManager(), WaitingDialogFragment.TAG);
        }
    }

    public void removeWaitingDialog()
    {
        if (getFragmentManager().findFragmentByTag(WaitingDialogFragment.TAG) != null)
        {
            ((WaitingDialogFragment) getFragmentManager().findFragmentByTag(WaitingDialogFragment.TAG)).dismiss();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ACCOUNTS / SESSION MANAGEMENT
    // ///////////////////////////////////////////////////////////////////////////
    public void setCurrentAccount(Account account)
    {
        this.currentAccount = account;
    }

    public void setCurrentAccount(long accountId)
    {
        this.currentAccount = AccountManager.retrieveAccount(this, accountId);
    }

    public Account getCurrentAccount()
    {
        return currentAccount;
    }

    public AlfrescoSession getCurrentSession()
    {
        if (currentAccount == null)
        {
            currentAccount = applicationManager.getCurrentAccount();
        }

        AlfrescoSession ses = currentAccount != null ? applicationManager.getSession(currentAccount.getId()) : null;

        if (ses instanceof OdsRepositorySession)
        {
            OdsRepositorySession ods = ((OdsRepositorySession) ses).getCurrent();

            if (ods != null)
            {
                ses = ods;
            }
        }

        return ses;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MANAGERS
    // ///////////////////////////////////////////////////////////////////////////
    public RenditionManager getRenditionManager()
    {
        return renditionManager;
    }

    public void setRenditionManager(RenditionManager renditionManager)
    {
        this.renditionManager = renditionManager;
    }

//    public AccountManager getAccountManager()
//    {
//        return accountManager;
//    }

    // ///////////////////////////////////////////////////////////////////////////
    // FRAGMENT UTILITY
    // ///////////////////////////////////////////////////////////////////////////
    public void addBrowserFragment(String path)
    {
        if (path == null)
        {
            return;
        }

        ChildrenBrowserFragment mFragment = (ChildrenBrowserFragment) getFragment(ChildrenBrowserFragment.TAG);
        if (mFragment != null && path.equals(mFragment.getParent().getPropertyValue(PropertyIds.PATH)))
        {
            return;
        }

        BaseFragment frag = ChildrenBrowserFragment.newInstance(path);
        frag.setSession(SessionUtils.getSession(this));
        FragmentDisplayer
                .replaceFragment(this, frag, DisplayUtils.getLeftFragmentId(this), ChildrenBrowserFragment.TAG, true);
    }

    public void addNavigationFragment(Folder f)
    {
        if (f == null)
        {
            return;
        }

        ChildrenBrowserFragment mFragment = (ChildrenBrowserFragment) getFragment(ChildrenBrowserFragment.TAG);
        if (mFragment != null && f.getIdentifier().equals(mFragment.getParent().getIdentifier()))
        {
            return;
        }

        BaseFragment frag = ChildrenBrowserFragment.newInstance(f);
        frag.setSession(SessionUtils.getSession(this));
        FragmentDisplayer
                .replaceFragment(this, frag, DisplayUtils.getLeftFragmentId(this), ChildrenBrowserFragment.TAG, true);
    }

    public void addNavigationFragment(Folder f, boolean isShortcut)
    {
        if (f == null)
        {
            return;
        }

        ChildrenBrowserFragment mFragment = (ChildrenBrowserFragment) getFragment(ChildrenBrowserFragment.TAG);
        if (mFragment != null && f.getIdentifier().equals(mFragment.getParent().getIdentifier()))
        {
            return;
        }

        BaseFragment frag = ChildrenBrowserFragment.newInstance(f, isShortcut);
        frag.setSession(SessionUtils.getSession(this));
        FragmentDisplayer
                .replaceFragment(this, frag, DisplayUtils.getLeftFragmentId(this), ChildrenBrowserFragment.TAG, true);
    }

    public void addNavigationFragment(String folderIdentifier)
    {
        BaseFragment frag = ChildrenBrowserFragment.newInstanceById(folderIdentifier);
        frag.setSession(SessionUtils.getSession(this));
        FragmentDisplayer
                .replaceFragment(this, frag, DisplayUtils.getLeftFragmentId(this), ChildrenBrowserFragment.TAG, true);
    }

    public void addNavigationFragment(Site site, Folder f)
    {
        if (f == null)
        {
            return;
        }
        if (site == null)
        {
            addNavigationFragment(f);
            return;
        }

        ChildrenBrowserFragment mFragment = (ChildrenBrowserFragment) getFragment(ChildrenBrowserFragment.TAG);
        if (mFragment != null && mFragment.getParent() != null &&
                f.getIdentifier().equals(mFragment.getParent().getIdentifier()))
        {
            return;
        }

        BaseFragment frag = ChildrenBrowserFragment.newInstance(site, f);
        frag.setSession(SessionUtils.getSession(this));
        FragmentDisplayer
                .replaceFragment(this, frag, DisplayUtils.getLeftFragmentId(this), ChildrenBrowserFragment.TAG, true);
    }

    public void addNavigationFragment(Site site, Folder f, boolean isShortcut)
    {
        if (!isShortcut)
        {
            addNavigationFragment(site, f);
        }
        else
        {
            if (f == null)
            {
                return;
            }
            if (site == null)
            {
                addNavigationFragment(f);
                return;
            }

            ChildrenBrowserFragment mFragment = (ChildrenBrowserFragment) getFragment(ChildrenBrowserFragment.TAG);
            if (mFragment != null && mFragment.getParent() != null &&
                    f.getIdentifier().equals(mFragment.getParent().getIdentifier()))
            {
                return;
            }

            BaseFragment frag = ChildrenBrowserFragment.newInstance(site, f, true);
            frag.setSession(SessionUtils.getSession(this));
            FragmentDisplayer
                    .replaceFragment(this, frag, DisplayUtils.getLeftFragmentId(this), ChildrenBrowserFragment.TAG,
                            true);
        }
    }

    public void addNavigationFragment(Site s)
    {
        BaseFragment frag = ChildrenBrowserFragment.newInstance(s);
        frag.setSession(SessionUtils.getSession(this));
        FragmentDisplayer
                .replaceFragment(this, frag, DisplayUtils.getLeftFragmentId(this), ChildrenBrowserFragment.TAG, true);
    }

    // ////////////////////////////////////////////////////////
    // BROADCAST RECEIVER
    // ///////////////////////////////////////////////////////

    /**
     * Register a broadcast receiver to this specific activity. If used this
     * methods is responsible to unregister the receiver during on stop().
     */
    public void registerPrivateReceiver(BroadcastReceiver receiver, IntentFilter filter)
    {
        if (receiver != null && filter != null)
        {
            broadcastManager.registerReceiver(receiver, filter);
            receivers.add(receiver);
        }
    }

    public void registerPublicReceiver(BroadcastReceiver receiver, IntentFilter filter)
    {
        if (receiver != null && filter != null)
        {
            registerReceiver(receiver, filter);
            publicReceivers.add(receiver);
        }
    }

    /**
     * Utility BroadcastReceiver for displaying dialog after an error or to
     * display custom message. Use ACTION_DISPLAY_DIALOG or ACTION_DISPLAY_ERROR
     * Action inside an Intent and send it with localBroadcastManager instance.
     *
     * @author Jean Marie Pascal
     */
    private class UtilsReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Activity activity = BaseActivity.this;

            if (activity.isFinishing() || activity.isChangingConfigurations())
            {
                return;
            }

            //
            if (IntentIntegrator.ACTION_DISPLAY_DIALOG.equals(intent.getAction()))
            {
                removeWaitingDialog();

                try
                {
                    SimpleAlertDialogFragment.newInstance(intent.getExtras())
                            .show(activity.getFragmentManager(), SimpleAlertDialogFragment.TAG);
                }
                catch (Exception ex)
                {
                    OdsLog.ex("UtilsReceiver", ex);
                }
                return;
            }

            // Intent for Display Errors
            if (IntentIntegrator.ACTION_DISPLAY_ERROR.equals(intent.getAction()))
            {
                removeWaitingDialog();
                Exception e = (Exception) intent.getExtras().getSerializable(IntentIntegrator.EXTRA_ERROR_DATA);

                String errorMessage = getString(R.string.error_general);
                if (e instanceof AlfrescoAppException && ((AlfrescoAppException) e).isDisplayMessage())
                {
                    errorMessage = e.getMessage();
                }

                MessengerManager.showLongToast(activity, errorMessage);

                CloudExceptionUtils.handleCloudException(activity, e, false);

                return;
            }

            if (IntentIntegrator.ACTION_CONFIGURATION_BRAND.equals(intent.getAction()))
            {
                rebrand();
            }
        }
    }
}
