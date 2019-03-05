/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.mozilla.focus.R;

/**
 * A CoordinatorLayout implementation that resizes dynamically (by adding padding to the bottom)
 * based on whether a keyboard is visible or not.
 * <p>
 * Implementation based on:
 * https://github.com/mikepenz/MaterialDrawer/blob/master/library/src/main/java/com/mikepenz/materialdrawer/util/KeyboardUtil.java
 * <p>
 * An optional viewToHideWhenActivated can be set: this is a View that will be hidden when the keyboard
 * is showing. That can be useful for things like FABs that you don't need when someone is typing.
 */
public class ResizableKeyboardLayout extends CoordinatorLayout {

    public ResizableKeyboardLayout(Context context) {
        this(context, null);
    }

    public ResizableKeyboardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResizableKeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setOnApplyWindowInsetsListener((v, insets) -> {
            int difference = insets.getSystemWindowInsetBottom();
            if (getLayoutParams() instanceof MarginLayoutParams) {
                ((MarginLayoutParams) getLayoutParams()).bottomMargin = difference;
            }
            return insets;
        });
    }
}
