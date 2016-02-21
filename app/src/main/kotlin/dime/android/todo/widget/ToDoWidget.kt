package dime.android.todo.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent

class ToDoWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // To prevent any ANR timeouts, we perform the update in a service
        context.startService(Intent(context, ToDoWidgetService::class.java))
    }
}
