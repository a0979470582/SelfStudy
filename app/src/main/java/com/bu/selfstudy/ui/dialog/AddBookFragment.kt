package com.bu.selfstudy.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bu.selfstudy.ui.main.ActivityViewModel
import com.bu.selfstudy.ui.main.MainActivity
import com.bu.selfstudy.R
import com.bu.selfstudy.data.model.Book
import com.bu.selfstudy.databinding.FragmentAddBookBinding
import com.bu.selfstudy.tool.closeKeyboard
import com.bu.selfstudy.tool.openKeyboard
import com.bu.selfstudy.tool.setNewToolbar
import com.bu.selfstudy.tool.viewBinding

class AddBookFragment: Fragment()  {

    private val activityViewModel: ActivityViewModel by activityViewModels()
    private val binding : FragmentAddBookBinding by viewBinding()

    private var bookName = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * 1. bookname輸入完按下確認後, 可跳到輸入explanation處
         */
        with(binding.bookField.editText!!){
            setOnEditorActionListener { view, actionId, event ->
                return@setOnEditorActionListener when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        binding.explanationField.requestFocus()
                        binding.bookField.isEndIconVisible = false
                        false
                    }
                    else -> false
                }
            }

            doOnTextChanged() { inputText, _, _, _ ->
                if (!inputText.isNullOrBlank())
                    binding.bookField.error = null
            }
        }


        binding.bookField.requestFocus()
        openKeyboard()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setNewToolbar(binding.toolbar)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.only_done_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_done -> {
                bookName = binding.bookField.editText!!.text.toString()

                if(bookName.isBlank())
                    binding.bookField.error = "請輸入正確的題庫名稱"
                else{
                    //為了告訴SearchFragment是從AddBookFragment返回的, 須重新開啟選擇題庫對話框
                    setFragmentResult("AddBookFragment", Bundle())

                    activityViewModel.insertBook(
                            Book(
                                    bookName = bookName,
                                    explanation =  binding.explanationField.editText!!.text.toString()
                            )
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
     * 避免焦點還在輸入框, 按退回鍵沒有取消焦點就關閉頁面
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
}