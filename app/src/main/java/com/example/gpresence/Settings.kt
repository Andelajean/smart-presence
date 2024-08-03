package com.example.gpresence
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gpresence.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changeLanguage.setOnClickListener {
            showLanguageSelector()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLanguageSelector() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.application_language, null)

        val layoutEnglish = dialogView.findViewById<LinearLayout>(R.id.layout_english)
        val layoutFrench = dialogView.findViewById<LinearLayout>(R.id.layout_french)
        val tvEnglish = dialogView.findViewById<TextView>(R.id.tv_english)
        val tvFrench = dialogView.findViewById<TextView>(R.id.tv_french)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val selectedLanguage = if (layoutEnglish.isSelected) "en" else "fr"
                setLocale(selectedLanguage)
            }
            .setNegativeButton("Cancel", null)

        val dialog = builder.create()

        // Initial selection
        val currentLanguage = getLocale()
        if (currentLanguage == "fr") {
            highlightSelection(layoutFrench, tvFrench)
        } else {
            highlightSelection(layoutEnglish, tvEnglish)
        }

        // Set click listeners to update selection
        layoutEnglish.setOnClickListener {
            highlightSelection(layoutEnglish, tvEnglish)
            unhighlightSelection(layoutFrench, tvFrench)
        }

        layoutFrench.setOnClickListener {
            highlightSelection(layoutFrench, tvFrench)
            unhighlightSelection(layoutEnglish, tvEnglish)
        }

        dialog.show()
    }

    private fun highlightSelection(layout: View, textView: TextView) {
        layout.isSelected = true
        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selected))
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun unhighlightSelection(layout: View, textView: TextView) {
        layout.isSelected = false
        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.unselected))
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

        val editor = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", language)
        editor.apply()

        Toast.makeText(requireContext(), "Language changed to $language", Toast.LENGTH_SHORT).show()
        requireActivity().recreate()
    }

    private fun getLocale(): String {
        val prefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return prefs.getString("My_Lang", "en") ?: "en"
    }
}
