package com.practice.phuc.ums_husc.Helper;

import android.content.Context;
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
    public static Snackbar createTwoButtonSnackbar(final Context context, final View rootLayout, String message
                            , View.OnClickListener negativeClickListener, View.OnClickListener positiveClickListener) {
        // Create the Snackbar
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final Snackbar snackbar = Snackbar.make(rootLayout, message, Snackbar.LENGTH_INDEFINITE);

        // Get the Snackbar layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Set snackbar layout params
        int navbarHeight = getNavBarHeight(context);
        FrameLayout.LayoutParams parentParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
        parentParams.setMargins(0, 0, 0, 0 - navbarHeight + 120);
        layout.setLayoutParams(parentParams);
        layout.setPadding(0, 0, 0, 0);
        layout.setLayoutParams(parentParams);

        // Inflate our custom view
        View snackView = LayoutInflater.from(context).inflate(R.layout.snackbar_custom, null);

        // Configure our custom view
        TextView messageTextView = (TextView) snackView.findViewById(R.id.tv_message);
        messageTextView.setText(message);

        Button btnNegative = snackView.findViewById(R.id.btn_negative);
        if (negativeClickListener == null) btnNegative.setVisibility(View.GONE);
        else {
            btnNegative.setText("Đóng");
            btnNegative.setOnClickListener(negativeClickListener);
            btnNegative.setVisibility(View.VISIBLE);
        }

        Button btnPositive = snackView.findViewById(R.id.btn_positive);
        if (positiveClickListener == null) btnPositive.setVisibility(View.GONE);
        else {
            btnPositive.setText("Thử lại");
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
