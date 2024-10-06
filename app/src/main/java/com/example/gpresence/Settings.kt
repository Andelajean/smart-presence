package com.example.gpresence

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class SettingsFragment : Fragment() {
    companion object {
        const val PREFS_NAME = "theme_prefs"
        const val KEY_THEME = "theme"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
    }

    lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        val changeLanguageButton: View = view.findViewById(R.id.change_language)
        val creditsButton: View = view.findViewById(R.id.credits)
        val aboutAppButton: View = view.findViewById(R.id.about_app)
        val changeThemeButton: View = view.findViewById(R.id.change_theme)
        val logoutButton: View = view.findViewById(R.id.logout)
        val resetPasswordButton: View = view.findViewById(R.id.reset_password)

        // Gérer le changement de langue
        changeLanguageButton.setOnClickListener {
            showChangeLanguageDialog()
        }

        // Gérer le changement de thème
        changeThemeButton.setOnClickListener {
            showChangeThemeDialog()
        }

        creditsButton.setOnClickListener {
            showCreditsDialog()
        }

        aboutAppButton.setOnClickListener {
            showAboutAppDialog()
        }

        resetPasswordButton.setOnClickListener {
            showResetPasswordDialog()
        }

        logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    // Méthode pour afficher le dialogue de changement de langue
    fun showChangeLanguageDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.application_language, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

                .create()

        val englishOption: View = dialogView.findViewById(R.id.english_option)
        val frenchOption: View = dialogView.findViewById(R.id.french_option)
        val changeButton: Button = dialogView.findViewById(R.id.button_change_language)

        // Gérer le choix de la langue
        englishOption.setOnClickListener {
            setLocale("en")
            alertDialog.dismiss()
        }

        frenchOption.setOnClickListener {
            setLocale("fr")
            alertDialog.dismiss()
        }

        // Changer la langue quand l'utilisateur clique sur "Changer"
        changeButton.setOnClickListener {
            // Vous pouvez ajouter une logique supplémentaire ici si nécessaire
           // Toast.makeText(context, getString(R.string.language_changed), Toast.LENGTH_SHORT).show()
        }

        alertDialog.show()
    }

    // Méthode pour changer la langue de l'application
    fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        // Redémarrer l'activité pour appliquer le changement de langue
        requireActivity().recreate()
    }

    // Méthode pour afficher le dialogue de changement de thème
    fun showChangeThemeDialog() {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.change_theme, null)

        // Create and configure the AlertDialog
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .setTitle("Changer le Thème")
            .setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Clair") { _, _ -> changeTheme(THEME_LIGHT) }
            .setPositiveButton("Sombre") { _, _ -> changeTheme(THEME_DARK) }
            .show()
    }
    // Méthode pour changer le thème
    fun changeTheme(theme: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_THEME, theme)
        editor.apply()

        AppCompatDelegate.setDefaultNightMode(
            if (theme == THEME_DARK) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        requireActivity().recreate()
    }

    // Méthode pour afficher le dialogue de réinitialisation de mot de passe
    fun showResetPasswordDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.reset_password, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.email)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.buton).setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(context, getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, getString(R.string.reset_password_email_sent), Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                        } else {
                            Toast.makeText(context, getString(R.string.reset_password_failed), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        alertDialog.show()
    }

    // Méthode pour afficher le dialogue de déconnexion
    fun showLogoutDialog() {
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

            val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    // Méthode pour afficher les crédits
    fun showCreditsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.credit, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.butto).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Méthode pour afficher les informations sur l'application
    fun showAboutAppDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.aboutapp, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.buton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
