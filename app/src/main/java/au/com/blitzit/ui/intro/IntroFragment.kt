package au.com.blitzit.ui.intro

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import au.com.blitzit.MainActivity
import au.com.blitzit.R

class IntroFragment : Fragment() {

    companion object {
        fun newInstance() = IntroFragment()
    }

    private lateinit var viewModel: IntroViewModel

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_intro, container, false)
        loginButton = view.findViewById(R.id.login_button)
        loginButton.setOnClickListener { _ ->
            val action = IntroFragmentDirections.introToLogin()
            this.findNavController().navigate(action)
        }
        registerButton = view.findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            this.findNavController().navigate(IntroFragmentDirections.actionIntroToSignUpTypeFragment())
        }

        val mActivity: MainActivity = activity as MainActivity
        mActivity.hideFAB()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(IntroViewModel::class.java)


    }

}