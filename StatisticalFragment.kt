package com.smartcalculator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * واجهة الآلة الحاسبة الإحصائية
 * تدعم المتوسط، الوسيط، الانحراف المعياري، إلخ
 */
@AndroidEntryPoint
class StatisticalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return View(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: تنفيذ الواجهة الإحصائية
    }

    companion object {
        fun newInstance() = StatisticalFragment()
    }
}
