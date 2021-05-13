package au.com.blitzit

import android.content.Context
import androidx.room.*
import au.com.blitzit.dao.ParticipantDAO
import au.com.blitzit.dao.UserDAO
import au.com.blitzit.roomdata.*

@Database(entities = [
    Category::class,
    Invoice::class,
    LineItem::class,
    Participant::class,
    Plan::class,
    PrimaryContact::class,
    Provider::class,
    Purpose::class,
    SignUpRequest::class,
    SupportCoordinator::class,
    User::class],
    version = 1002, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase()
{
    //DAOs
    abstract fun userDAO(): UserDAO
    abstract fun participantDAO(): ParticipantDAO

    //Singleton Pattern
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}