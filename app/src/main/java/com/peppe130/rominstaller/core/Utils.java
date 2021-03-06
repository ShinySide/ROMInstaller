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

package com.peppe130.rominstaller.core;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

import com.peppe130.rominstaller.R;
import com.afollestad.materialdialogs.MaterialDialog;


@SuppressLint("CommitPrefEdits")
@SuppressWarnings("ResultOfMethodCallIgnored, unused, ConstantConditions")
public class Utils {

    public static Toast TOAST;
    public static AppCompatActivity ACTIVITY;
    public static File ZIP_FILE;

    public static String MODEL;
    public static String FILE_NAME;

    public static Boolean SHOULD_LOCK_UI = true;

    public static ArrayList<DownloadManager.Request> DOWNLOAD_REQUEST_LIST = new ArrayList<>();
    public static ArrayList<Uri> DOWNLOAD_LINK_LIST = new ArrayList<>();
    public static ArrayList<File> DOWNLOAD_DIRECTORY_LIST = new ArrayList<>();
    public static ArrayList<String> DOWNLOADED_FILE_NAME_LIST = new ArrayList<>();
    public static ArrayList<String> DOWNLOADED_FILE_MD5_LIST = new ArrayList<>();


    public static void Sleep(long mTime) {

        try {
            Thread.sleep(mTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void ToastShort(Context mContext, String mString) {

        if (TOAST != null) {
            TOAST.cancel();
            TOAST = null;
        }

        TOAST = Toast.makeText(mContext, mString, Toast.LENGTH_SHORT);
        TOAST.show();

    }

    public static void ToastLong(Context mContext, String mString) {

        if (TOAST != null) {
            TOAST.cancel();
            TOAST = null;
        }

        TOAST = Toast.makeText(mContext, mString, Toast.LENGTH_LONG);
        TOAST.show();

    }

    public static void ExportPreferences() {

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(Utils.ACTIVITY);

        try {
            FileWriter mFileWriter = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/" + Utils.ACTIVITY.getString(R.string.rom_name) + "/" + "preferences.prop");
            BufferedWriter mBufferedWriter = new BufferedWriter(mFileWriter);
            Map<String,?> mPreferences = SP.getAll();
            mPreferences.remove("file_path");
            mPreferences.remove("first_time");
            mPreferences.remove("default_values");
            for (Map.Entry<String,?> entry : mPreferences.entrySet()) {
                mBufferedWriter.write(
                        entry.getKey() + "=" + entry.getValue().toString() + "\n"
                );
            }
            mBufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void SetDefaultValues() {

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(Utils.ACTIVITY);
        SharedPreferences.Editor mEditor = SP.edit();

        try {
            Map<String,?> mPreferences = SP.getAll();
            mPreferences.remove("file_path");
            mPreferences.remove("first_time");
            mPreferences.remove("default_values");
            for (Map.Entry<String,?> entry : mPreferences.entrySet()) {
                mEditor.remove(entry.getKey()).apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void StartSingleDownload(String downloadLink, String downloadDirectory, String downloadedFileFinalName, String downloadedFileMD5) {

        Uri mDownloadLink = Uri.parse(downloadLink);
        File mDownloadDirectory = new File(downloadDirectory);
        DownloadManager.Request mRequest = new DownloadManager.Request(mDownloadLink);
        mRequest.setDestinationInExternalPublicDir(mDownloadDirectory.getPath(), downloadedFileFinalName);

        new Download(
                mRequest,
                mDownloadDirectory,
                downloadedFileFinalName,
                downloadedFileMD5).execute();
    }

    public static void StartMultipleDownloads() {

        new Download(
                Utils.DOWNLOAD_REQUEST_LIST.get(0),
                Utils.DOWNLOAD_DIRECTORY_LIST.get(0),
                Utils.DOWNLOADED_FILE_NAME_LIST.get(0),
                Utils.DOWNLOADED_FILE_MD5_LIST.get(0), 1).execute();

    }

    public static void StartDownloadROM(String downloadLink, String downloadDirectory) {

        Uri mDownloadLink = Uri.parse(downloadLink);
        File mDownloadDirectory = new File(downloadDirectory);
        DownloadManager.Request mRequest = new DownloadManager.Request(mDownloadLink);
        mRequest.setDestinationInExternalPublicDir(mDownloadDirectory.getPath(), Utils.FILE_NAME);

        new Download(
                mRequest,
                mDownloadDirectory,
                Utils.FILE_NAME, true).execute();

    }

    public static void StartFlashRecovery(String downloadLink, String downloadDirectory, String downloadedFileFinalName, String recoveryPartition) {

        Uri mDownloadLink = Uri.parse(downloadLink);
        File mDownloadDirectory = new File(downloadDirectory);
        DownloadManager.Request mRequest = new DownloadManager.Request(mDownloadLink);
        mRequest.setDestinationInExternalPublicDir(mDownloadDirectory.getPath(), downloadedFileFinalName);

        new FlashRecovery(
                mRequest,
                mDownloadDirectory,
                downloadedFileFinalName,
                recoveryPartition, false).execute();

    }

    public static void StartFlashRecoveryWithAddons(String downloadLink, String downloadDirectory, String downloadedFileFinalName, String recoveryPartition) {

        Uri mDownloadLink = Uri.parse(downloadLink);
        File mDownloadDirectory = new File(downloadDirectory);
        DownloadManager.Request mRequest = new DownloadManager.Request(mDownloadLink);
        mRequest.setDestinationInExternalPublicDir(mDownloadDirectory.getPath(), downloadedFileFinalName);

        new FlashRecovery(
                mRequest,
                mDownloadDirectory,
                downloadedFileFinalName,
                recoveryPartition, true).execute();

    }

    public static void EnqueueDownload(String downloadLink, String downloadDirectory, String downloadedFileFinalName, String downloadedFileMD5) {

        Utils.DOWNLOAD_LINK_LIST.add(Uri.parse(downloadLink));
        Utils.DOWNLOAD_DIRECTORY_LIST.add(new File(downloadDirectory));
        Utils.DOWNLOADED_FILE_NAME_LIST.add(downloadedFileFinalName);
        Utils.DOWNLOADED_FILE_MD5_LIST.add(downloadedFileMD5);
        DownloadManager.Request request = new DownloadManager.Request(Utils.DOWNLOAD_LINK_LIST.get(Utils.DOWNLOAD_LINK_LIST.size() - 1));
        request.setDestinationInExternalPublicDir(Utils.DOWNLOAD_DIRECTORY_LIST.get(Utils.DOWNLOAD_DIRECTORY_LIST.size() - 1).getPath(), Utils.DOWNLOADED_FILE_NAME_LIST.get(Utils.DOWNLOADED_FILE_NAME_LIST.size() - 1));
        Utils.DOWNLOAD_REQUEST_LIST.add(request);

    }

    public static void FileChooser() {

        CustomFileChooser mCustomFileChooserDialog = new CustomFileChooser.Builder(ACTIVITY)
                .initialPath(Environment.getExternalStorageDirectory().getPath())
                .build();
        mCustomFileChooserDialog.setCancelable(false);
        mCustomFileChooserDialog.show(ACTIVITY);

    }

    public static void FollowMeDialog(String[] items, final String[] links) {

        new MaterialDialog.Builder(ACTIVITY)
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        ACTIVITY.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(links[which])));
                    }
                })
                .show();

    }

    public static void DeleteFolderRecursively(String path) {

        File mFile = new File(path);

        if (mFile.exists()) {
            String mCommand = "rm -r " + path;
            Runtime mRuntime = Runtime.getRuntime();
            try {
                mRuntime.exec(mCommand);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static int FetchPrimaryColor() {

        TypedValue mTypedValue = new TypedValue();
        TypedArray mTypedArray = ACTIVITY.obtainStyledAttributes(mTypedValue.data, new int[] {R.attr.colorPrimary});
        int mColor = mTypedArray.getColor(0, 0);
        mTypedArray.recycle();
        return mColor;

    }

    public static int FetchAccentColor() {

        TypedValue mTypedValue = new TypedValue();
        TypedArray mTypedArray = ACTIVITY.obtainStyledAttributes(mTypedValue.data, new int[] {R.attr.colorAccent});
        int mColor = mTypedArray.getColor(0, 0);
        mTypedArray.recycle();
        return mColor;

    }

    @Nullable
    public static Integer BackgroundColorChooser() {

        Integer mTheme = null;

        try {
            mTheme = Utils.ACTIVITY.getPackageManager().getPackageInfo(Utils.ACTIVITY.getPackageName(), 0).applicationInfo.theme;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        switch (mTheme) {
            case R.style.AppTheme_Light:
                return R.color.colorBackground_Theme_Light;
            case R.style.AppTheme_Dark:
                return R.color.colorBackground_Theme_Dark;
        }

        return null;

    }

    public static boolean CopyAssetFolder(AssetManager manager, String fromPath, String toPath) {

        try {
            String[] files = manager.list(fromPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
                if (file.contains(".")) {
                    res &= CopyAsset(manager, fromPath + "/" + file, toPath + "/" + file);
                } else {
                    res &= CopyAssetFolder(manager, fromPath + "/" + file, toPath + "/" + file);
                }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean CopyAsset(AssetManager manager, String fromPath, String toPath) {

        InputStream in;
        OutputStream out;

        try {
            in = manager.open(fromPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            CopyFile(in, out);
            in.close();
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void CopyFile(InputStream in, OutputStream out) throws IOException {

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

    }

}
