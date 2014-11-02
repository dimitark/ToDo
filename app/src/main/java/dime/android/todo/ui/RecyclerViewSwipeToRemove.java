package dime.android.todo.ui;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by dime on 02/11/14.
 */
public class RecyclerViewSwipeToRemove implements RecyclerView.OnItemTouchListener {
    private static final int LOCK_MIN_DISTANCE = 20;
    private static final int MIN_DISTANCE = 400;
    private SwipeListener swipeListener;

    private float downX, downY;
    private boolean eventLocked;
    private ScrollOrientation scrollOrientation;
    private View childView;

    private enum ScrollOrientation {
        HORIZONTAL, VERTICAL;
    }

    public RecyclerViewSwipeToRemove(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
        float deltaX;
        float deltaY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                eventLocked = false;
                childView = recyclerView.findChildViewUnder(event.getX(), event.getY());

                /* Allow other events like Click to be processed */
                return false;
            case MotionEvent.ACTION_MOVE:

                if (eventLocked && scrollOrientation == ScrollOrientation.HORIZONTAL) {
                    return true;
                } else if (eventLocked && scrollOrientation == ScrollOrientation.VERTICAL) {
                    return false;
                }

                /* Get the deltas */
                deltaX = downX - event.getX();
                deltaY = downY - event.getY();

                /* If we have horizontal scroll - lock the event */
                if (Math.abs(deltaX) > LOCK_MIN_DISTANCE) {
                    eventLocked = true;
                    scrollOrientation = ScrollOrientation.HORIZONTAL;
                    return true;
                }

                if (Math.abs(deltaY) > LOCK_MIN_DISTANCE) {
                    eventLocked = true;
                    scrollOrientation = ScrollOrientation.VERTICAL;
                    return false;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (eventLocked && scrollOrientation == ScrollOrientation.HORIZONTAL) {
                    return true;
                } else if (eventLocked && scrollOrientation == ScrollOrientation.VERTICAL) {
                    return false;
                }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) {
        float deltaX;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                /* Get the deltas */
                deltaX = downX - event.getX();
                /* Inform the listener */
                swipeListener.swipeInProgress(childView, deltaX);
                break;
            case MotionEvent.ACTION_CANCEL:
                swipeListener.swipeCanceled(childView);
                break;
            case MotionEvent.ACTION_UP:
                deltaX = downX - event.getX();
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    /* Inform the listener that the swipe has been done */
                    swipeListener.swipeDone(childView);
                } else {
                    /* The swipe has been canceled */
                    swipeListener.swipeCanceled(childView);
                }
                break;
        }
    }

    /**
     * The Swipe Listener
     */
    public interface SwipeListener {
        /**
         * Called when the swipe has been canceled (deltaX < MIN_DISTANCE)
         *
         * @param v The view on which the swipe has been happening
         */
        public void swipeCanceled(View v);
        /**
         * Called when the swipe has been done.
         *
         * @param v The view on which the swipe has been happening
         */
        public void swipeDone(View v);
        /**
         * Called for every move motion action on the given view.
         *
         * @param v
         * @param deltaX
         */
        public void swipeInProgress(View v, float deltaX);

    }
}
