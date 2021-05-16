package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Invoice

@Dao
interface InvoiceDAO
{
    @Query("SELECT * FROM Invoice WHERE plan_id = :planID AND invoice_id = :invoiceId LIMIT 1")
    suspend fun getInvoiceById(invoiceId: String, planID: String): Invoice

    @Query("SELECT * FROM Invoice WHERE plan_id = :planID")
    suspend fun getInvoiceListByPlan(planID: String): List<Invoice>

    @Query("SELECT * FROM Invoice WHERE plan_id = :planID AND provider = :providerName")
    suspend fun getInvoiceListByProviderName(planID: String, providerName: String): List<Invoice>

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