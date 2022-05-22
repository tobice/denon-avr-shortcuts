package cz.tobice.denonavrshortcuts.testutils

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Test runner with support for Hilt.
 *
 * Documentation: https://developer.android.com/training/dependency-injection/hilt-testing#ui-test
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
