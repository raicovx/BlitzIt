package au.com.blitzit.ui.invoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Invoice
import au.com.blitzit.roomdata.LineItem
import au.com.blitzit.ui.budget.CategoryBudgetViewModel
import kotlinx.coroutines.launch

class InvoiceDetailFragment: Fragment()
{
    companion object{
        fun newInstance() = InvoiceDetailFragment
    }

    init {
        lifecycleScope.launch {
            whenStarted {
                viewModel.downloadLineItems(args.invoiceId)
                viewModel.getSelectedInvoice()
                viewModel.getLineItems()
            }
        }
    }

    private lateinit var viewModel: InvoiceDetailViewModel

    private val args: InvoiceDetailFragmentArgs by navArgs()

    private lateinit var mainView: View
    private lateinit var lineItemHolder: LinearLayout
    private lateinit var invoiceDetailProgressBar: ProgressBar
    private lateinit var invoiceLineItemProgressBar: ProgressBar
    private lateinit var invoiceDetailInfo: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        //view Model
        viewModel = ViewModelProvider(this).get(InvoiceDetailViewModel::class.java)
        viewModel.selectedInvoiceId = args.invoiceId

        //View
        mainView = inflater.inflate(R.layout.fragment_invoice_detail, container, false)

        invoiceDetailProgressBar = mainView.findViewById(R.id.invoice_detail_progress)
        invoiceLineItemProgressBar = mainView.findViewById(R.id.invoice_line_item_progress)
        invoiceDetailInfo = mainView.findViewById(R.id.invoice_detail_info)

        //BackButton
        mainView.findViewById<Button>(R.id.invoice_detail_back_button).setOnClickListener {
            this.findNavController().navigate(InvoiceDetailFragmentDirections.actionInvoiceDetailFragmentToInvoicesFragment2())
        }

        //Holder
        lineItemHolder = mainView.findViewById(R.id.invoice_detail_line_item_holder)

        //live Data
        val selectedInvoiceObserver = Observer<Invoice>{
            populateHeader(it)
            invoiceDetailProgressBar.visibility = View.GONE
            invoiceDetailInfo.visibility = View.VISIBLE
        }
        viewModel.selectedInvoice.observe(viewLifecycleOwner, selectedInvoiceObserver)
        val selectedInvoiceLineItemsObserver = Observer<List<LineItem>> {
            populateLineItems(it)
            invoiceLineItemProgressBar.visibility = View.GONE
        }
        viewModel.selectedInvoiceLineItems.observe(viewLifecycleOwner, selectedInvoiceLineItemsObserver)

        return mainView
    }

    private fun populateHeader(invoice: Invoice)
    {
        mainView.findViewById<TextView>(R.id.invoice_detail_paid_to).text = invoice.provider
        mainView.findViewById<TextView>(R.id.invoice_detail_number).text = invoice.invoice_number
        mainView.findViewById<TextView>(R.id.invoice_detail_claimed_total).text = CranstekHelper.convertToCurrency(invoice.amount)
    }

    private fun populateLineItems(lineItems: List<LineItem>)
    {
        for(lineItem in lineItems)
        {
            if(lineItem != lineItems[0]) {
                val dividerView = layoutInflater.inflate(R.layout.part_invoice_divider, lineItemHolder, false)
                lineItemHolder.addView(dividerView)
            }

            val view = layoutInflater.inflate(R.layout.part_invoice_line_item, lineItemHolder, false)

            view.findViewById<TextView>(R.id.invoice_detail_line_item_name).text = lineItem.supportCode

            val lineItemSelector: ConstraintLayout = view.findViewById(R.id.invoice_detail_line_item_selector)
            lineItemSelector.setOnClickListener {
                //TODO
            }

            lineItemHolder.addView(view)
        }
    }
}