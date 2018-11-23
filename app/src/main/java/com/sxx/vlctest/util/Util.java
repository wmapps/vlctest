/*****************************************************************************
 * Util.java
 *****************************************************************************
 * Copyright © 2011-2014 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.sxx.vlctest.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.videolan.libvlc.util.AndroidUtil;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

public class Util {
    public final static String TAG = "VLC/Util";
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /** Print an on-screen message to alert the user */
    public static void snacker(View view, int stringId) {
        Snackbar.make(view, stringId, Snackbar.LENGTH_SHORT).show();
    }

    /** Print an on-screen message to alert the user */
    public static void snacker(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static int convertPxToDp(@NonNull Context context, int px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float logicalDensity = metrics.density;
        int dp = Math.round(px / logicalDensity);
        return dp;
    }

    public static int convertDpToPx(@NonNull Context context, int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    public static String readAsset(@NonNull Context context, String assetName, String defaultS) {
        InputStream is = null;
        BufferedReader r = null;
        try {
            is = context.getResources().getAssets().open(assetName);
            r = new BufferedReader(new InputStreamReader(is, "UTF8"));
            StringBuilder sb = new StringBuilder();
            String line = r.readLine();
            if (line != null) {
                sb.append(line);
                line = r.readLine();
                while (line != null) {
                    sb.append('\n');
                    sb.append(line);
                    line = r.readLine();
                }
            }
            return sb.toString();
        } catch (IOException e) {
            return defaultS;
        } finally {
            close(is);
            close(r);
        }
    }

    /**
     * Get a resource id from an attribute id.
     *
     * @param context the context
     * @param context the context
     * @param attrId  the attr id
     *
     * @return the resource id
     */
    public static int getResourceFromAttribute(@NonNull Context context, int attrId) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attrId});
        int resId = a.getResourceId(0, 0);
        a.recycle();
        return resId;
    }

    /**
     * Get a color id from an attribute id.
     *
     * @param context the context
     * @param context the context
     * @param attrId  the attr id
     *
     * @return the color id
     */
    public static int getColorFromAttribute(@NonNull Context context, int attrId) {
        return context.getResources().getColor(getResourceFromAttribute(context, attrId));
    }

    /**
     * Set the alignment mode of the specified TextView with the desired align
     * mode from preferences.
     * <p>
     * See @array/audio_title_alignment_values
     *
     * @param alignMode Align mode as read from preferences
     * @param t         Reference to the textview
     */
    public static void setAlignModeByPref(int alignMode, TextView t) {
        if (alignMode == 1) {
            t.setEllipsize(TruncateAt.END);
        } else if (alignMode == 2) {
            t.setEllipsize(TruncateAt.START);
        } else if (alignMode == 3) {
            t.setEllipsize(TruncateAt.MARQUEE);
            t.setMarqueeRepeatLimit(-1);
            t.setSelected(true);
        }
    }

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static String getPathFromURI(@NonNull Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        } finally {
            close(cursor);
        }
        return null;
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void commitPreferences(SharedPreferences.Editor editor) {
        if (AndroidUtil.isGingerbreadOrLater()) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public static boolean close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
