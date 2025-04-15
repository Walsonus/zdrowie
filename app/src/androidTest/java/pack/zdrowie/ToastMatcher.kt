
//Class for 'TEST FOR DISPLAY INCORRECT DATA'
//package pack.zdrowie
//
//import android.view.WindowManager
//import androidx.test.espresso.Root
//import org.hamcrest.Description
//import org.hamcrest.TypeSafeMatcher
//
//class ToastMatcher : TypeSafeMatcher<Root>() {
//    override fun describeTo(description: Description) {
//        description.appendText("is toast")
//    }
//
//    public override fun matchesSafely(root: Root): Boolean {
//        val type = root.windowLayoutParams?.get()?.type
//        if (type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY ||
//            type == WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG
//        ) {
//            return true
//        }
//
//        val windowToken = root.decorView.windowToken
//        val appToken = root.decorView.applicationWindowToken
//        return windowToken !== appToken
//    }
//}

