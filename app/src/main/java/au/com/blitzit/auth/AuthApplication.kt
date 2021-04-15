package au.com.blitzit.auth

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import kotlinx.coroutines.runBlocking

class AuthApplication : Application()
{
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())

            Amplify.configure(applicationContext)
            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }

        Amplify.Hub.subscribe(HubChannel.AUTH) { event ->
            when (event.name) {
                InitializationStatus.SUCCEEDED.toString() ->
                    Log.i("AuthQuickstart", "Auth successfully initialized")
                InitializationStatus.FAILED.toString() ->
                    Log.i("AuthQuickstart", "Auth failed to succeed")
                else -> when (AuthChannelEventName.valueOf(event.name)) {
                    AuthChannelEventName.SIGNED_IN ->
                        Log.i("AuthQuickstart", "Auth just became signed in")
                    AuthChannelEventName.SIGNED_OUT ->
                        Log.i("AuthQuickstart", "Auth just became signed out")
                    AuthChannelEventName.SESSION_EXPIRED -> {
                        Log.i("AuthQuickstart", "Auth session just expired")
                        runBlocking { AuthServices.attemptSignOut() }
                    }
                    else ->
                        Log.w("AuthQuickstart", "Unhandled Auth Event: ${event.name}")
                }
            }
        }
    }
}