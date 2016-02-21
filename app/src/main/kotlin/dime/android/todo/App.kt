package dime.android.todo

import android.app.Application
import android.content.Intent
import com.crashlytics.android.Crashlytics
import dime.android.todo.widget.ToDoWidgetService
import io.fabric.sdk.android.Fabric

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())

        updateWidget()
    }

    fun updateWidget() {
        startService(Intent(this.applicationContext, ToDoWidgetService::class.java))
    }
}
