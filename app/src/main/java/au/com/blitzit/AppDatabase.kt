package au.com.blitzit

import android.content.Context
import androidx.room.*
import au.com.blitzit.dao.*
import au.com.blitzit.roomdata.*

@Database(entities = [
    Category::class,
    Invoice::class,
    LineItem::class,
    Participant::class,
    Plan::class,
    PrimaryContact::class,
    Provider::class,
    ProviderCategorySpending::class,
    Purpose::class,
    SignUpRequest::class,
    SupportCoordinator::class,
    User::class],
    version = 1030, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase()
{
    //DAOs
    abstract fun userDAO(): UserDAO
    abstract fun participantDAO(): ParticipantDAO
    abstract fun primaryContactDAO(): PrimaryContactDAO
    abstract fun supportCoordinatorDAO(): SupportCoordinatorDAO
    abstract fun planDAO(): PlanDAO
    abstract fun purposeDAO(): PurposeDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun invoiceDAO(): InvoiceDAO
    abstract fun providerDAO(): ProviderDAO
    abstract fun providerCategorySpendingDAO(): ProviderCategorySpendingDAO
    abstract fun signUpRequestDAO(): SignUpRequestDAO
    abstract fun lineItemDAO(): LineItemDAO

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
                    "blitzit_Database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}