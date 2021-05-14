package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Invoice

@Dao
interface InvoiceDAO
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInvoice(invoice: Invoice)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateInvoice(invoice: Invoice)

    @Transaction
    suspend fun upsertInvoice(invoice: Invoice)
    {
        try
        {
            insertInvoice(invoice)
        }
        catch (e: SQLiteConstraintException)
        {
            updateInvoice(invoice)
        }
    }
}