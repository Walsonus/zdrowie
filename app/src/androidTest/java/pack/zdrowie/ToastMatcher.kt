package pack.zdrowie

import android.os.Build
import android.view.WindowManager
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ToastMatcher : TypeSafeMatcher<Root>() {

    override fun describeTo(description: Description) {
        description.appendText("is toast")
    }

    override fun matchesSafely(root: Root): Boolean {
        val windowLayoutParamsType = root.windowLayoutParams.get()?.type
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Użycie TYPE_APPLICATION_OVERLAY dla API 30+
            windowLayoutParamsType == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            // Użycie TYPE_TOAST dla starszych API
            windowLayoutParamsType == WindowManager.LayoutParams.TYPE_TOAST
        }
    }
}
