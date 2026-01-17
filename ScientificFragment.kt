package com.smartcalculator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smartcalculator.databinding.FragmentScientificBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * واجهة الآلة الحاسبة العلمية
 * تدعم الدوال المثلثية واللوغاريتمات
 */
@AndroidEntryPoint
class ScientificFragment : Fragment() {

    private var _binding: FragmentScientificBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScientificBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: تنفيذ الواجهة العلمية
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ScientificFragment()
    }
}
