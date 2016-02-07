package dime.android.todo

import android.app.Application
import android.content.Intent
import dime.android.todo.widget.ToDoWidgetService

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        updateWidget()
    }

    fun updateWidget() {
        startService(Intent(this.applicationContext, ToDoWidgetService::class.java))
    }
}
