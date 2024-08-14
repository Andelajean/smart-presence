package com.example.gpresence

import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import java.util.Locale
import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME = "theme"
        private const val THEME_LIGHT = "light"
        private const val THEME_DARK = "dark"
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val changeLanguageButton: View = view.findViewById(R.id.change_language)
        val creditsButton: View = view.findViewById(R.id.credits)
        val aboutAppButton: View = view.findViewById(R.id.about_app)
        val change_theme: View = view.findViewById(R.id.change_theme)
        val logout:View = view.findViewById(R.id.logout)
        val resetPasswordButton: View = view.findViewById(R.id.reset_password)  // Ajout de cette ligne

        changeLanguageButton.setOnClickListener {
            showLanguageSelectorDialog()
        }
        change_theme.setOnClickListener {
            showChangeThemeDialog()
        }
        creditsButton.setOnClickListener {
            showCreditsDialog()
        }

        aboutAppButton.setOnClickListener {
            showAboutAppDialog()
        }

        resetPasswordButton.setOnClickListener {  // Ajout de cette ligne
            showResetPasswordDialog()  // Appel de la méthode pour afficher la boîte de dialogue
        }
        auth = FirebaseAuth.getInstance()

        logout.setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    private fun showResetPasswordDialog() {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.reset_password, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.email)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.buton).setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(context, "Veuillez entrer votre adresse e-mail", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Un e-mail de réinitialisation a été envoyé", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                        } else {
                            Toast.makeText(context, "Échec de l'envoi de l'e-mail de réinitialisation", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        alertDialog.show()
    }
    private fun showChangeThemeDialog() {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.change_theme, null)

        // Create and configure the AlertDialog
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .setTitle("Changer Vos Theme ici")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Black") { _, _ -> changeTheme(THEME_DARK) }
            .setNeutralButton("White") { _, _ -> changeTheme(THEME_LIGHT) }
            .show()
    }

    private fun changeTheme(theme: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_THEME, theme)
        editor.apply()

        activity?.recreate()
    }

    override fun onResume() {
        super.onResume()
        applyTheme()
    }

    private fun applyTheme() {
        val theme = sharedPreferences.getString(KEY_THEME, THEME_LIGHT)
        when (theme) {
            THEME_DARK -> requireActivity().setTheme(R.style.AppTheme_Dark)
            else -> requireActivity().setTheme(R.style.AppTheme_Light)
        }
    }
    private fun showLogoutDialog() {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.logout, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.button_no).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.button_yes).setOnClickListener {
            auth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun showLanguageSelectorDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.application_language, null)
        val englishOption = dialogView.findViewById<LinearLayout>(R.id.english_option)
        val frenchOption = dialogView.findViewById<LinearLayout>(R.id.french_option)
        val changeButton = dialogView.findViewById<Button>(R.id.button_change_language)

        var selectedLanguage = ""

        englishOption.setOnClickListener {
            selectedLanguage = "en"
            englishOption.setBackgroundColor(resources.getColor(R.color.selected_background))
            frenchOption.setBackgroundColor(resources.getColor(android.R.color.transparent))
        }

        frenchOption.setOnClickListener {
            selectedLanguage = "fr"
            frenchOption.setBackgroundColor(resources.getColor(R.color.selected_background))
            englishOption.setBackgroundColor(resources.getColor(android.R.color.transparent))
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        changeButton.setOnClickListener {
            if (selectedLanguage.isNotEmpty()) {
                setLocale(selectedLanguage)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showCreditsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.credit, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.butto).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAboutAppDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.aboutapp, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.buton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        // Redémarrer l'activité pour appliquer les changements de langue
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }
}
