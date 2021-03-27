package au.com.blitzit.ui.login

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import java.text.SimpleDateFormat
import java.util.*

class ForgotPasswordFragment : Fragment()
{
    companion object{
        fun newInstance() = ForgotPasswordFragment
    }

    private lateinit var progressWheel: ProgressBar
    private lateinit var backButton: Button
    private lateinit var submitButton: Button
    private lateinit var dobField: TextView
    private lateinit var emailField: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_forgotpassword, container, false)

        backButton = view.findViewById(R.id.sign_up_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLogin())
        }

        progressWheel = view.findViewById(R.id.forgot_password_progress)
        progressWheel.isVisible = false

        dobField = view.findViewById(R.id.forgot_password_dob)
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            handleDatePicker()
        else
            handleDatePickerLower()

        emailField = view.findViewById(R.id.forgot_password_email)

        submitButton = view.findViewById(R.id.forgot_password_submit)
        submitButton.setOnClickListener {
            TODO()
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun handleDatePicker()
    {
        dobField.isFocusable = false

        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(myFormat)
            dobField.text = sdf.format(cal.time)
        }

        dobField.setOnClickListener{
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(
                Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun handleDatePickerLower()
    {
        //Check dobfield text, attempt to format to useable type
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat)

        //try {

        //}
        TODO("This")
    }
}