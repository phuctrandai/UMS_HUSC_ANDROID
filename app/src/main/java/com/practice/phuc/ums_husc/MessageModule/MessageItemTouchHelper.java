package com.practice.phuc.ums_husc.MessageModule;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.practice.phuc.ums_husc.Adapter.MessageRecyclerDataAdapter;

public class MessageItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private MessageItemTouchHelperListener listener;
    private int swipeDirs;

    MessageItemTouchHelper(int dragDirs, int swipeDirs, MessageItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
        this.swipeDirs = swipeDirs;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, swipeDirs);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((MessageRecyclerDataAdapter.DataViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((MessageRecyclerDataAdapter.DataViewHolder) viewHolder).viewForeground;
        final View backgroundView = ((MessageRecyclerDataAdapter.DataViewHolder) viewHolder).viewBackground;
        final View backgroundView2 = ((MessageRecyclerDataAdapter.DataViewHolder) viewHolder).viewBackground2;

        if (dX > 0) {
            backgroundView.setAlpha(0.0f);
            backgroundView2.setAlpha(1.0f);

        } else {
            backgroundView2.setAlpha(0.0f);
            backgroundView.setAlpha(1.0f);
        }
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((MessageRecyclerDataAdapter.DataViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((MessageRecyclerDataAdapter.DataViewHolder) viewHolder).viewForeground;
        final View backgroundView = ((MessageRecyclerDataAdapter.DataViewHolder) viewHolder).viewBackground;
        final View backgroundView2 = ((MessageRecyclerDataAdapter.DataViewHolder) viewHolder).viewBackground2;

        if (dX > 0) {
            backgroundView.setAlpha(0.0f);
            backgroundView2.setAlpha(1.0f);
        } else {
            backgroundView2.setAlpha(0.0f);
            backgroundView.setAlpha(1.0f);
        }
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface MessageItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
