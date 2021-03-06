package au.com.blitzit.ui.settings

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class FAQFragment : Fragment()
{
    private lateinit var backButton: Button

    companion object
    {
        fun newInstance() = FAQFragment
    }

    private lateinit var header1: TextView
    private lateinit var header2: TextView
    private lateinit var header3: TextView
    private lateinit var header4: TextView
    private lateinit var header5: TextView
    private lateinit var header6: TextView
    private lateinit var header7: TextView
    private lateinit var header8: TextView

    private lateinit var faqBody1: TextView
    private lateinit var faqBody2_1: TextView
    private lateinit var faqBody2_2: TextView
    private lateinit var faqBody2_3: TextView
    private lateinit var faqBody3_1: TextView
    private lateinit var faqBody3_2: TextView
    private lateinit var faqBody3_3: TextView
    private lateinit var faqBody3_4: TextView
    private lateinit var faqBody3_5: TextView
    private lateinit var faqBody3_6: TextView
    private lateinit var faqBody3_7: TextView
    private lateinit var faqBody4_1: TextView
    private lateinit var faqBody5_1: TextView
    private lateinit var faqBody5_2: TextView
    private lateinit var faqBody5_3: TextView
    private lateinit var faqBody6_1: TextView
    private lateinit var faqBody6_2: TextView
    private lateinit var faqBody6_3: TextView
    private lateinit var faqBody6_4: TextView
    private lateinit var faqBody6_5: TextView
    private lateinit var faqBody6_6: TextView
    private lateinit var faqBody6_7: TextView
    private lateinit var faqBody7_1: TextView
    private lateinit var faqBody7_2: TextView
    private lateinit var faqBody7_3: TextView
    private lateinit var faqBody8: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_faq, container, false)

        backButton = view.findViewById(R.id.faq_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(FAQFragmentDirections.actionFAQFragmentToSettingsFragment())
        }

        //Set up all text views
        setAllTextViews(view)

        //Header OnClick Listeners
        header1.setOnClickListener { toggleDisplay(header1, arrayOf(faqBody1)) }
        header2.setOnClickListener { toggleDisplay(header2, arrayOf(faqBody2_1, faqBody2_2, faqBody2_3)) }
        header3.setOnClickListener { toggleDisplay(header3, arrayOf(faqBody3_1, faqBody3_2, faqBody3_3, faqBody3_4, faqBody3_5, faqBody3_6, faqBody3_7)) }
        header4.setOnClickListener { toggleDisplay(header4, arrayOf(faqBody4_1)) }
        header5.setOnClickListener { toggleDisplay(header5, arrayOf(faqBody5_1, faqBody5_2, faqBody5_3)) }
        header6.setOnClickListener { toggleDisplay(header6, arrayOf(faqBody6_1, faqBody6_2, faqBody6_3, faqBody6_4, faqBody6_5, faqBody6_6, faqBody6_7)) }
        header7.setOnClickListener { toggleDisplay(header7, arrayOf(faqBody7_1, faqBody7_2, faqBody7_3)) }
        header8.setOnClickListener { toggleDisplay(header8, arrayOf(faqBody8)) }

        return view
    }

    private fun toggleDisplay(header: TextView, textViews: Array<TextView>)
    {
        for(textView in textViews)
        {
            if (textView.visibility == View.GONE)
                textView.visibility = View.VISIBLE
            else
                textView.visibility = View.GONE
        }

        if(textViews[0].visibility == View.VISIBLE)
            header.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0)
        else
            header.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.forward_arrow, 0)
    }

    private fun setAllTextViews(view: View)
    {
        //Headers
        header1 = view.findViewById(R.id.faq_header_1)
        header2 = view.findViewById(R.id.faq_header_2)
        header3 = view.findViewById(R.id.faq_header_3)
        header4 = view.findViewById(R.id.faq_header_4)
        header5 = view.findViewById(R.id.faq_header_5)
        header6 = view.findViewById(R.id.faq_header_6)
        header7 = view.findViewById(R.id.faq_header_7)
        header8 = view.findViewById(R.id.faq_header_8)

        //Bodies
        faqBody1 = view.findViewById(R.id.faq_body_1)

        faqBody2_1 = view.findViewById(R.id.faq_body_2_1)
        faqBody2_2 = view.findViewById(R.id.faq_body_2_2)
        faqBody2_3 = view.findViewById(R.id.faq_body_2_3)

        faqBody3_1 = view.findViewById(R.id.faq_body_3_1)
        faqBody3_2 = view.findViewById(R.id.faq_body_3_2)
        faqBody3_3 = view.findViewById(R.id.faq_body_3_3)
        faqBody3_4 = view.findViewById(R.id.faq_body_3_4)
        faqBody3_5 = view.findViewById(R.id.faq_body_3_5)
        faqBody3_6 = view.findViewById(R.id.faq_body_3_6)
        faqBody3_7 = view.findViewById(R.id.faq_body_3_7)

        faqBody4_1 = view.findViewById(R.id.faq_body_4_1)

        faqBody5_1 = view.findViewById(R.id.faq_body_5_1)
        faqBody5_2 = view.findViewById(R.id.faq_body_5_2)
        faqBody5_3 = view.findViewById(R.id.faq_body_5_3)

        faqBody6_1 = view.findViewById(R.id.faq_body_6_1)
        faqBody6_2 = view.findViewById(R.id.faq_body_6_2)
        faqBody6_3 = view.findViewById(R.id.faq_body_6_3)
        faqBody6_4 = view.findViewById(R.id.faq_body_6_4)
        faqBody6_5 = view.findViewById(R.id.faq_body_6_5)
        faqBody6_6 = view.findViewById(R.id.faq_body_6_6)
        faqBody6_7 = view.findViewById(R.id.faq_body_6_7)

        faqBody7_1 = view.findViewById(R.id.faq_body_7_1)
        faqBody7_2 = view.findViewById(R.id.faq_body_7_2)
        faqBody7_3 = view.findViewById(R.id.faq_body_7_3)

        faqBody8 = view.findViewById(R.id.faq_body_8_1)

        //Set links
        faqBody1.movementMethod = LinkMovementMethod.getInstance()
        faqBody3_3.movementMethod = LinkMovementMethod.getInstance()
        faqBody6_3.movementMethod = LinkMovementMethod.getInstance()
        faqBody6_5.movementMethod = LinkMovementMethod.getInstance()
        faqBody7_1.movementMethod = LinkMovementMethod.getInstance()
        faqBody7_3.movementMethod = LinkMovementMethod.getInstance()
    }
}