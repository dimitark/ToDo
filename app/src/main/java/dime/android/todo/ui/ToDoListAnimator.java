package dime.android.todo.ui;

import android.support.v7.widget.DefaultItemAnimator;

/**
 * Created by dime on 05/11/14.
 */
public class ToDoListAnimator extends DefaultItemAnimator {
    @Override
    public long getRemoveDuration() {
        return 10000000;
    }
}
