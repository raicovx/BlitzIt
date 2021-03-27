package au.com.blitzit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

        //Set up the navhost
        navHostFragment =supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        fab = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            navHostFragment.navController.navigate(R.id.menuFragment)
        }
    }

    override fun onDestroy() {
        Amplify.Auth.signOut(
                { Log.i("AuthQuickstart", "Signed out successfully") },
                { Log.e("AuthQuickstart", "Sign out failed", it) }
        )
        super.onDestroy()
    }

    public fun HideFAB()
    {
        fab.hide()
    }
    public fun ShowFAB()
    {
        fab.show()
    }
}