package au.com.blitzit.ui.budget

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.MainActivity
import au.com.blitzit.R

enum class BudgetCategories{
    ImprovedDailyLivingSkills,
    SupportCoordination,
    Core
}

class CategoryBudgetFragment : Fragment()
{
    private lateinit var backButton : Button

    private lateinit var titleText : TextView

    private val args: CategoryBudgetFragmentArgs by navArgs()

    companion object
    {
        fun newInstance() = CategoryBudgetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_category_budget, container, false)

        backButton = view.findViewById(R.id.cat_budget_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(CategoryBudgetFragmentDirections.actionCategoryBudgetFragmentToDashboardFragment())
        }

        setupViewForSelected(view)

        return view
    }

    private fun setupViewForSelected(view: View)
    {
        titleText = view.findViewById(R.id.cat_budget_title)
        if(args.selectedbudget == BudgetCategories.ImprovedDailyLivingSkills)
            titleText.text = "Improved Living ya'll"
    }
}