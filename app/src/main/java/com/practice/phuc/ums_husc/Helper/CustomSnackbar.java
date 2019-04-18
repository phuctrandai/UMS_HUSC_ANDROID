package com.practice.phuc.ums_husc.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;

public class CustomSnackbar {
    @SuppressLint("SetTextI18n")
    public static Snackbar createTwoButtonSnackbar(final Context context, final View rootLayout, String message, final int duration
                            , View.OnClickListener negativeClickListener, View.OnClickListener positiveClickListener) {

        // Create the Snackbar
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final Snackbar snackbar;

        snackbar = Snackbar.make(rootLayout, message, duration);

        // Get the Snackbar layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Set snackbar layout params
        int navbarHeight = getNavBarHeight(context);
        ViewGroup.LayoutParams parentParams = layout.getLayoutParams();

        if (parentParams instanceof FrameLayout.LayoutParams) { //
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) parentParams;
            params.setMargins(14, 0, 14, 0 - navbarHeight + 134);
            layout.setLayoutParams(params);
            layout.setPadding(0, 0, 0, 0);
            layout.setLayoutParams(params);

        } else if (parentParams instanceof CoordinatorLayout.LayoutParams) { // Message fragment
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parentParams;
                params.setMargins(14, 0, 14, 0 - navbarHeight + 280);
            layout.setLayoutParams(params);
            layout.setPadding(0, 0, 0, 0);
            layout.setLayoutParams(params);
        }
        layout.setElevation(0.0f);
        layout.setBackground(context.getDrawable(R.drawable.snackbar_custom_background));

        // Inflate our custom view
        @SuppressLint("InflateParams") View snackView = LayoutInflater.from(context).inflate(R.layout.snackbar_custom, null);

        // Message
        TextView messageTextView = snackView.findViewById(R.id.tv_message);
        messageTextView.setText(message);
        // Negative button
        Button btnNegative = snackView.findViewById(R.id.btn_negative);
        if (negativeClickListener == null) btnNegative.setVisibility(View.GONE);
        else {
            btnNegative.setText("Đóng");
            btnNegative.setOnClickListener(negativeClickListener);
            btnNegative.setVisibility(View.VISIBLE);
        }
        // Positive button
        Button btnPositive = snackView.findViewById(R.id.btn_positive);
        if (positiveClickListener == null) btnPositive.setVisibility(View.GONE);
        else {
            btnPositive.setText(message.equals("Đã xóa 1 mục") ? "Hoàn tác" : "Thử lại");
            btnPositive.setOnClickListener(positiveClickListener);
            btnPositive.setVisibility(View.VISIBLE);
        }
        // Add our custom view to the Snackbar's layout
        layout.addView(snackView, objLayoutParams);
        return snackbar;
    }

    private static int getNavBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
