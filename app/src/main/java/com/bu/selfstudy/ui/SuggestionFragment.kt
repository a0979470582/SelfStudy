package com.bu.selfstudy.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bu.selfstudy.ui.main.MainActivity
import com.bu.selfstudy.R
import com.bu.selfstudy.databinding.FragmentSuggestionBinding
import com.bu.selfstudy.tool.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class SuggestionFragment: Fragment()  {

    private val binding : FragmentSuggestionBinding by viewBinding()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = binding.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setNewToolbar(binding.toolbar)

        val userMail = FirebaseAuth.getInstance().currentUser?.email

        if(userMail.isNullOrEmpty()) {
            binding.mailField.requestFocus()
        }else{
            binding.titleField.requestFocus()
            binding.mailField.setText(userMail)
        }
        openKeyboard()

        with(binding.mailField!!){
            setOnEditorActionListener { view, actionId, event ->
                return@setOnEditorActionListener when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        binding.titleField.requestFocus()
                        false
                    }
                    else -> false
                }
            }
        }

        with(binding.titleField!!){
            setOnEditorActionListener { view, actionId, event ->
                return@setOnEditorActionListener when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        binding.suggestionField.requestFocus()
                        false
                    }
                    else -> false
                }
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.only_send_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_send -> {
                val userMail = binding.mailField.text.toString()
                val title = binding.titleField.text.toString()
                val suggestion = binding.suggestionField.text.toString()

                if(userMail.isBlank())
                    MaterialAlertDialogBuilder(requireContext())
                            .setMessage("?????????????????????")
                            .setPositiveButton("??????") { dialog, which -> }
                            .show()

                else if(title.isBlank() && suggestion.isBlank())
                    MaterialAlertDialogBuilder(requireContext())
                            .setMessage("?????????????????????")
                            .setPositiveButton("??????") { dialog, which -> }
                            .show()
                else if(!hasNetwork())
                    "????????????????????????".showToast()
                else{
                    (requireActivity() as MainActivity).uploadUserSuggestion(
                            userMail,
                            title,
                            suggestion
                    )
                    closeKeyboard()
                    findNavController().popBackStack()
                }
            }
            android.R.id.home->{
                findNavController().popBackStack()
                closeKeyboard()
            }

        }
        return true
    }

    /**
     * ???????????????????????????, ?????????????????????????????????????????????
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val focusView = binding.root.findFocus()
                if(focusView != null)
                    focusView.clearFocus()
                else
                    findNavController().popBackStack()
            }
        }

        requireActivity()
                .onBackPressedDispatcher
                .addCallback(this, callback)
    }
}