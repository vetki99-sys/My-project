package com.smartcalculator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * واجهة الآلة الحاسبة الهندسية
 * تدعم حسابات الأشكال الهندسية وتحويلات الوحدات
 */
@AndroidEntryPoint
class EngineeringFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return View(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: تنفيذ الواجهة الهندسية
    }

    companion object {
        fun newInstance() = EngineeringFragment()
    }
}
