package com.example.gpresence
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
class ParametreTest {
    private lateinit var scenario: FragmentScenario<SettingsFragment>
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        scenario = FragmentScenario.launchInContainer(SettingsFragment::class.java)
        mockAuth = mock(FirebaseAuth::class.java)
        mockSharedPreferences = mock(SharedPreferences::class.java)
        mockContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testChangeThemeToDark() {
        scenario.onFragment { fragment ->
            val editor = mock(SharedPreferences.Editor::class.java)
            `when`(mockSharedPreferences.edit()).thenReturn(editor)

            fragment.changeTheme(SettingsFragment.THEME_DARK)

            verify(editor).putString(SettingsFragment.KEY_THEME, SettingsFragment.THEME_DARK)
            verify(editor).apply()
            verifyNoMoreInteractions(editor)

            // Verify that the theme has been set to dark mode
            assert(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    @Test
    fun testChangeThemeToLight() {
        scenario.onFragment { fragment ->
            val editor = mock(SharedPreferences.Editor::class.java)
            `when`(mockSharedPreferences.edit()).thenReturn(editor)

            fragment.changeTheme(SettingsFragment.THEME_LIGHT)

            verify(editor).putString(SettingsFragment.KEY_THEME, SettingsFragment.THEME_LIGHT)
            verify(editor).apply()

            // Verify that the theme has been set to light mode
            assert(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    @Test
    fun testSetLocaleToFrench() {
        scenario.onFragment { fragment ->
            fragment.setLocale("fr")
            val locale = fragment.requireContext().resources.configuration.locales[0]
            assert(locale.language == "fr")
        }
    }

    @Test
    fun testSetLocaleToEnglish() {
        scenario.onFragment { fragment ->
            fragment.setLocale("en")
            val locale = fragment.requireContext().resources.configuration.locales[0]
            assert(locale.language == "en")
        }
    }

    @Test
    fun testResetPasswordSuccess() {
        scenario.onFragment { fragment ->
            val mockEditText = mock(EditText::class.java)
            mockEditText.setText("Some text") // Correct way to set tex
            `when`(mockAuth.sendPasswordResetEmail("test@example.com")).thenReturn(mock())

            fragment.auth = mockAuth

            fragment.showResetPasswordDialog()

            verify(mockAuth).sendPasswordResetEmail("test@example.com")
            // Add further assertions for successful reset
        }
    }

    @Test
    fun testLogout() {
        scenario.onFragment { fragment ->
            val editor = mock(SharedPreferences.Editor::class.java)
            `when`(mockSharedPreferences.edit()).thenReturn(editor)

            fragment.auth = mockAuth

            fragment.showLogoutDialog()

            // Simulate a click on "yes" to log out
            verify(mockAuth).signOut()
            verify(editor).clear()
            verify(editor).apply()

            // Verify intent to navigate to LoginActivity
            val intent = Intent(mockContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            verify(mockContext).startActivity(intent)
        }
    }
}