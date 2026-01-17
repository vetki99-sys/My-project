package com.smartcalculator.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.smartcalculator.R
import com.smartcalculator.core.CalculatorEngine
import com.smartcalculator.databinding.FragmentBasicBinding
import com.smartcalculator.ui.viewmodels.BasicCalculatorViewModel
import com.smartcalculator.utils.AnimationHelper
import com.smartcalculator.utils.KeyboardHelper
import dagger.hilt.android.AndroidEntryPoint

/**
 * واجهة الآلة الحاسبة الأساسية
 * تدعم العمليات الحسابية الأساسية: الجمع، الطرح، الضرب، القسمة
 */
@AndroidEntryPoint
class BasicFragment : Fragment() {

    private var _binding: FragmentBasicBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BasicCalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNumberButtons()
        setupOperationButtons()
        setupSpecialButtons()
        setupResultButtons()
        observeViewModel()

        // إخفاء لوحة المفاتيح عند النقر خارجها
        setupTouchListener()
    }

    /**
     * إعداد أرقام لوحة المفاتيح
     */
    private fun setupNumberButtons() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9,
            binding.btnDecimal
        )

        numberButtons.forEach { button ->
            button?.setOnClickListener {
                viewModel.appendInput(button.text.toString())
                AnimationHelper.animatePress(requireContext(), it)
            }
        }
    }

    /**
     * إعداد أزرار العمليات
     */
    private fun setupOperationButtons() {
        val operations = mapOf(
            binding.btnAdd to "+",
            binding.btnSubtract to "-",
            binding.btnMultiply to "×",
            binding.btnDivide to "÷",
            binding.btnPercent to "%"
        )

        operations.forEach { (button, operation) ->
            button?.setOnClickListener {
                viewModel.appendOperation(operation)
                AnimationHelper.animatePress(requireContext(), it)
            }
        }

        // أزرار الأقواس
        binding.btnLeftParenthesis?.setOnClickListener {
            viewModel.appendOperation("(")
            AnimationHelper.animatePress(requireContext(), it)
        }

        binding.btnRightParenthesis?.setOnClickListener {
            viewModel.appendOperation(")")
            AnimationHelper.animatePress(requireContext(), it)
        }
    }

    /**
     * إعداد الأزرار الخاصة
     */
    private fun setupSpecialButtons() {
        // زر المسح
        binding.btnClear?.setOnClickListener {
            viewModel.clear()
            AnimationHelper.animatePress(requireContext(), it)
        }

        // زر الحذف
        binding.btnBackspace?.setOnClickListener {
            viewModel.deleteLast()
            AnimationHelper.animatePress(requireContext(), it)
        }
    }

    /**
     * إعداد أزرار النتائج والإجراءات
     */
    private fun setupResultButtons() {
        // زر المساواة
        binding.btnEquals?.setOnClickListener {
            viewModel.calculate()
            AnimationHelper.animatePress(requireContext(), it)
        }

        // زر النسخ
        binding.btnCopy?.setOnClickListener {
            copyToClipboard()
            AnimationHelper.animatePress(requireContext(), it)
        }

        // زر المشاركة
        binding.btnShare?.setOnClickListener {
            shareResult()
            AnimationHelper.animatePress(requireContext(), it)
        }
    }

    /**
     * مراقبة التغييرات في الـ ViewModel
     */
    private fun observeViewModel() {
        viewModel.expression.observe(viewLifecycleOwner) { expression ->
            binding.tvExpression.text = expression.ifEmpty { "0" }
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            binding.tvResult.text = result
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * إعداد مستمع اللمس لإخفاء لوحة المفاتيح
     */
    private fun setupTouchListener() {
        binding.root.setOnTouchListener { _, _ ->
            KeyboardHelper.hideKeyboard(requireActivity())
            false
        }
    }

    /**
     * نسخ النتيجة إلى الحافظة
     */
    private fun copyToClipboard() {
        val result = viewModel.result.value ?: return
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Calculator Result", result)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    /**
     * مشاركة النتيجة
     */
    private fun shareResult() {
        val expression = viewModel.expression.value ?: return
        val result = viewModel.result.value ?: return

        val shareText = "$expression = $result"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_result))
        startActivity(shareIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = BasicFragment()
    }
}
