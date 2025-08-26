import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

class BiometricAuth(private val context: Context) {

    fun authenticate() {
        // Créer un Exécuteur pour le BiometricPrompt
        val executor: Executor = ContextCompat.getMainExecutor(context)

        // Créer un BiometricPrompt
        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Afficher un message de succès
                    Toast.makeText(context, "Authentification réussie !", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Afficher un message d'échec
                    Toast.makeText(context, "Authentification échouée !", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Créer un prompt d'authentification
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authentification biométrique")
            .setSubtitle("Utilisez votre empreinte digitale ou votre visage")
            .setNegativeButtonText("Annuler")
            .build()

        // Lancer le prompt d'authentification
        biometricPrompt.authenticate(promptInfo)
    }
}