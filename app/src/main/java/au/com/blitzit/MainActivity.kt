package au.com.blitzit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.amplifyframework.core.Amplify
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

        //Set up the nav host
        navHostFragment =supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        fab = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            navHostFragment.navController.navigate(R.id.menuFragment)
        }
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