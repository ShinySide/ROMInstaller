/*

    Copyright © 2016, Giuseppe Montuoro.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package com.peppe130.rominstaller.activities;

import android.support.annotation.Nullable;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import java.io.File;

import com.peppe130.rominstaller.R;
import com.peppe130.rominstaller.BuildConfig;
import com.peppe130.rominstaller.core.Utils;
import com.peppe130.rominstaller.ControlCenter;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.entypo_typeface_library.Entypo;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;


@SuppressWarnings("ResultOfMethodCallIgnored, ConstantConditions")
public class SettingsActivity extends AppCompatActivity {

    SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_layout);

        Utils.ACTIVITY = this;

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        getFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new SettingsPreferencesFragment())
                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Utils.ACTIVITY = this;

        File mROMFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.rom_folder));
        File mSampleZIP = new File(mROMFolder.getPath() + "/" + "Sample.zip");

        if(!mROMFolder.exists()) {
            mROMFolder.mkdirs();
        }

        if(ControlCenter.TRIAL_MODE && !mSampleZIP.exists()) {
            Utils.CopyAssetFolder(getAssets(), "sample", mROMFolder.toString());
        }

        ControlCenter.ROMUtils();

    }

    public static class SettingsPreferencesFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_fragment);

            Preference DOWNLOAD_CENTER = findPreference("download_center");
            Preference PROJECT_BUILD_NUMBER = findPreference("project_build_number");
            Preference APP_BUILD_NUMBER = findPreference("app_build_number");
            Preference PROJECT_DEVELOPER = findPreference("project_developer");
            Preference PROJECT_THEMER = findPreference("project_themer");
            Preference PROJECT_THREAD = findPreference("project_thread");
            Preference PROJECT_GITHUB = findPreference("project_github");
            Preference ROM_BUILD_NUMBER = findPreference("rom_build_number");
            Preference ROM_DEVELOPER = findPreference("rom_developer");
            Preference ROM_THEMER = findPreference("rom_themer");
            Preference ROM_THREAD = findPreference("rom_thread");
            Preference REVIEW_APP = findPreference("review_app");
            Preference ALL_MY_APPS = findPreference("all_my_apps");

            Integer mIconColor = ContextCompat.getColor(getActivity(), IconColorChooser());

            IconicsDrawable mDownloadCenterIcon = new IconicsDrawable(getActivity())
                    .icon(Entypo.Icon.ent_download)
                    .color(mIconColor)
                    .sizeDp(40);

            IconicsDrawable mThreadIcon = new IconicsDrawable(getActivity())
                    .icon(CommunityMaterial.Icon.cmd_book_open_page_variant)
                    .color(mIconColor)
                    .sizeDp(40);

            IconicsDrawable mInfoIcon = new IconicsDrawable(getActivity())
                    .icon(GoogleMaterial.Icon.gmd_info_outline)
                    .color(mIconColor)
                    .sizeDp(40);

            IconicsDrawable mDeveloperIcon = new IconicsDrawable(getActivity())
                    .icon(GoogleMaterial.Icon.gmd_developer_board)
                    .color(mIconColor)
                    .sizeDp(40);

            IconicsDrawable mThemerIcon = new IconicsDrawable(getActivity())
                    .icon(GoogleMaterial.Icon.gmd_color_lens)
                    .color(mIconColor)
                    .sizeDp(40);

            IconicsDrawable mGitHubIcon = new IconicsDrawable(getActivity())
                    .icon(CommunityMaterial.Icon.cmd_github_circle)
                    .color(mIconColor)
                    .sizeDp(40);

            IconicsDrawable mPlayStoreIcon = new IconicsDrawable(getActivity())
                    .icon(CommunityMaterial.Icon.cmd_google_play)
                    .color(mIconColor)
                    .sizeDp(40);

            DOWNLOAD_CENTER.setIcon(mDownloadCenterIcon);
            DOWNLOAD_CENTER.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), DownloadActivity.class));
                    return false;
                }
            });

            PROJECT_BUILD_NUMBER.setIcon(mInfoIcon);
            PROJECT_BUILD_NUMBER.setSummary(Double.toString(BuildConfig.projectVersion));

            PROJECT_DEVELOPER.setIcon(mDeveloperIcon);
            PROJECT_DEVELOPER.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String[] mSocial = new String[] {"Google+", "Twitter"};
                    String[] mLinks = new String[] {"http://google.com/+PeppeMontuoro", "https://twitter.com/PeppeMontuoro"};
                    Utils.FollowMeDialog(mSocial, mLinks);
                    return false;
                }
            });

            PROJECT_THEMER.setIcon(mThemerIcon);
            PROJECT_THEMER.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String[] mSocial = new String[] {"Google+", "Twitter"};
                    String[] mLinks = new String[] {"http://google.com/+MRLOUDT_ONE", "https://twitter.com/MR_LOUD_T_ONE"};
                    Utils.FollowMeDialog(mSocial, mLinks);
                    return false;
                }
            });

            PROJECT_THREAD.setIcon(mThreadIcon);
            PROJECT_THREAD.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri mUri = Uri.parse("http://forum.xda-developers.com/android/apps-games/app-rom-installer-to-flash-custom-rom-t3430099");
                    startActivity(new Intent(Intent.ACTION_VIEW, mUri));
                    return false;
                }
            });

            PROJECT_GITHUB.setIcon(mGitHubIcon);
            PROJECT_GITHUB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri mUri = Uri.parse("https://github.com/peppe130/ROMInstaller");
                    startActivity(new Intent(Intent.ACTION_VIEW, mUri));
                    return false;
                }
            });

            APP_BUILD_NUMBER.setIcon(mInfoIcon);
            APP_BUILD_NUMBER.setSummary(BuildConfig.VERSION_NAME);

            ROM_BUILD_NUMBER.setIcon(mInfoIcon);

            ROM_DEVELOPER.setIcon(mDeveloperIcon);
            ROM_DEVELOPER.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ControlCenter.ROMDeveloperInfoAction();
                    return false;
                }
            });

            ROM_THEMER.setIcon(mThemerIcon);
            ROM_THEMER.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ControlCenter.ROMThemerInfoAction();
                    return false;
                }
            });

            ROM_THREAD.setIcon(mThreadIcon);
            ROM_THREAD.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ControlCenter.ROMThreadInfoAction();
                    return false;
                }
            });

            REVIEW_APP.setIcon(mPlayStoreIcon);
            REVIEW_APP.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    }
                    return false;
                }
            });

            ALL_MY_APPS.setIcon(mPlayStoreIcon);
            ALL_MY_APPS.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Giuseppe Montuoro")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:Giuseppe Montuoro")));
                    }
                    return false;
                }
            });

        }

        @Nullable
        public static Integer IconColorChooser() {

            Integer mTheme = null;

            try {
                mTheme = Utils.ACTIVITY.getPackageManager().getPackageInfo(Utils.ACTIVITY.getPackageName(), 0).applicationInfo.theme;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            switch (mTheme) {
                case R.style.AppTheme_Light:
                    return R.color.colorPrimary_Theme_Light;
                case R.style.AppTheme_Dark:
                    return android.R.color.white;
            }

            return null;

        }

    }

}
