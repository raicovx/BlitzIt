package au.com.blitzit

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity()
{

    lateinit var fab: FloatingActionButton
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Switch off dark mode hopefully
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //Set status bar text to white (TEMP)
        window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

        //Set up the nav host
        navHostFragment =supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        fab = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            navHostFragment.navController.navigate(R.id.menuFragment)
        }

        //Temp fix for display sizes set above normal, to be patched out in next version
        val config = resources.configuration
        config.fontScale = 1f
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.scaledDensity = config.fontScale * displayMetrics.density
        config.densityDpi = resources.displayMetrics.xdpi.toInt()
        resources.updateConfiguration(config, displayMetrics)
    }

    //Handles changing of the font size to anything but default
    //Effectively forces the app to use the default font size of the phone ensuring that our applications text sizes are kept try to design
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        Log.i("GAz_STUFF", "override applied")
        applyOverrideConfiguration(newOverride)
    }

    fun hideFAB()
    {
        fab.hide()
    }
    fun showFAB()
    {
        fab.show()
    }
}