package dime.android.todo.ui;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by dime on 02/11/14.
 */
public class RecyclerViewSwipeToRemove implements RecyclerView.OnItemTouchListener {
    private static final int LOCK_MIN_DISTANCE = 20;
    private static final int DEFAULT_MIN_DISTANCE = 200;

    private SwipeListener swipeListener;

    private float downX, downY;
    private boolean eventLocked;
    private ScrollOrientation scrollOrientation;
    private View childView;

    /* The min distance need for the swipe. It should be half of the whole width of the recycler view */
    private int minDistance = -1;

    private enum ScrollOrientation {
        HORIZONTAL, VERTICAL;
    }

    public RecyclerViewSwipeToRemove(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;

        /* TODO Calculate the min distance */
        minDistance = DEFAULT_MIN_DISTANCE;
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
        float deltaX = downX - event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                /* Inform the listener */
                swipeListener.swipeInProgress(childView, deltaX);
                break;
            case MotionEvent.ACTION_CANCEL:
                swipeListener.swipeCanceled(childView, deltaX);
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(deltaX) > minDistance) {
                    /* Inform the listener that the swipe has been done */
                    swipeListener.swipeDone(childView, deltaX);
                } else {
                    /* The swipe has been canceled */
                    swipeListener.swipeCanceled(childView, deltaX);
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
        public void swipeCanceled(View v, float deltaX);
        /**
         * Called when the swipe has been done.
         *
         * @param v The view on which the swipe has been happening
         */
        public void swipeDone(View v, float deltaX);
        /**
         * Called for every move motion action on the given view.
         *
         * @param v
         * @param deltaX
         */
        public void swipeInProgress(View v, float deltaX);

    }
}
