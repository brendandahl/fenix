/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.customtabs

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavDirections
import io.mockk.every
import io.mockk.mockk
import mozilla.components.support.utils.toSafeIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.TestApplication
import org.mozilla.fenix.components.metrics.Event
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class ExternalAppBrowserActivityTest {

    @Test
    fun getIntentSource() {
        val activity = ExternalAppBrowserActivity()

        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }.toSafeIntent()
        assertEquals(Event.OpenedApp.Source.CUSTOM_TAB, activity.getIntentSource(launcherIntent))

        val viewIntent = Intent(Intent.ACTION_VIEW).toSafeIntent()
        assertEquals(Event.OpenedApp.Source.CUSTOM_TAB, activity.getIntentSource(viewIntent))

        val otherIntent = Intent().toSafeIntent()
        assertEquals(Event.OpenedApp.Source.CUSTOM_TAB, activity.getIntentSource(otherIntent))
    }

    @Test
    fun `getNavDirections finishes activity if session ID is null`() {
        val activity = spy(object : ExternalAppBrowserActivity() {
            public override fun getNavDirections(
                from: BrowserDirection,
                customTabSessionId: String?
            ): NavDirections? {
                return super.getNavDirections(from, customTabSessionId)
            }

            override fun getIntent(): Intent {
                val intent: Intent = mockk()
                val bundle: Bundle = mockk()
                every { bundle.getString(any()) } returns ""
                every { intent.extras } returns bundle
                return intent
            }
        })

        var directions = activity.getNavDirections(BrowserDirection.FromGlobal, "id")
        assertNotNull(directions)
        verify(activity, never()).finish()

        directions = activity.getNavDirections(BrowserDirection.FromGlobal, null)
        assertNull(directions)
        verify(activity).finish()
    }
}
