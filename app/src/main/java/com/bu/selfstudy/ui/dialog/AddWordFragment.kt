package com.bu.selfstudy.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.bu.selfstudy.ui.main.ActivityViewModel
import com.bu.selfstudy.ui.main.MainActivity
import com.bu.selfstudy.R
import com.bu.selfstudy.data.model.Word
import com.bu.selfstudy.databinding.FragmentAddWordBinding
import com.bu.selfstudy.tool.*


class AddWordFragment: Fragment() {
    private val args: AddWordFragmentArgs by navArgs()
    private val binding : FragmentAddWordBinding by viewBinding()
    private val activityViewModel: ActivityViewModel by activityViewModels()

    private var wordName = ""
    private var pronunciation = ""
    private var translation = ""
    private var variation = ""
    private var example = ""
    private var note = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        (activity as MainActivity).let {
            it.setSupportActionBar(binding.toolbar)

            NavigationUI.setupActionBarWithNavController(
                    it, findNavController(), it.appBarConfiguration)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //監聽從DialogChooseBook返回的bookId
        setFragmentResultListener("DialogChooseBook"){_, bundle ->
            val word = Word(
                bookId = bundle.getLong("bookId"),
                wordName = wordName,
                pronunciation = pronunciation,
                translation = translation,
                variation = variation,
                example = example,
                note = note
            )
            activityViewModel.insertWord(word)
            closeKeyboard()
            findNavController().popBackStack()
        }


        binding.soundField.setEndIconOnClickListener {

        }

        binding.soundField.setStartIconOnClickListener {
        }

        /**
         * wordname輸入完按下確認後, 可跳到輸入translation處
         */
        binding.wordField.editText!!.setOnEditorActionListener { view, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    binding.translationField.requestFocus()
                    binding.wordField.isEndIconVisible = false
                    false
                }
                else -> false
            }
        }

        binding.wordField.editText?.doOnTextChanged{ inputText, _, _, _ ->
            inputText?.let {
                if (it.isNotBlank())
                    binding.wordField.error = null
                binding.pronunciationField.editText!!.setText(it)
            }
        }

    }

    /**
     * 避免焦點還在輸入框, 按退回鍵就離開了
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val focusView = binding.root.findFocus()
                if(focusView != null)
                    focusView.clearFocus()
                else {
                    //save state
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
                this, // LifecycleOwner
                callback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.addword_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_send -> {
                wordName = binding.wordField.editText!!.text.toString()
                if(wordName.isBlank())
                    binding.wordField.error = "請輸入正確的英文單字"
                else{
                    val action = AddWordFragmentDirections.actionGlobalDialogChooseBook(
                            "選擇加入的題庫", args.bookId)
                    findNavController().navigate(action)
                }
            }
            android.R.id.home->{
                //新增單字是否需要保存狀態?
                findNavController().popBackStack()
                closeKeyboard()
            }

        }
        return true
    }



}