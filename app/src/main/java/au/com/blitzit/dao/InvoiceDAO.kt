package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Invoice

@Dao
interface InvoiceDAO
{
    @Query("SELECT * FROM Invoice WHERE plan_id = :planID AND invoice_id = :invoiceId AND ndis_number = :ndis LIMIT 1")
    suspend fun getInvoiceById(invoiceId: String, planID: String, ndis: Int): Invoice

    @Query("SELECT * FROM Invoice WHERE plan_id = :planID AND ndis_number = :ndis")
    suspend fun getInvoiceListByPlan(planID: String, ndis: Int): List<Invoice>

    @Query("SELECT * FROM Invoice WHERE plan_id = :planID AND provider = :providerName  AND ndis_number = :ndis")
    suspend fun getInvoiceListByProviderName(planID: String, providerName: String, ndis: Int): List<Invoice>

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