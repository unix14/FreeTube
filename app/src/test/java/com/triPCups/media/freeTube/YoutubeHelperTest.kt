package com.triPCups.media.freeTube

import com.triPCups.media.freeTube.utils.YoutubeHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class YoutubeHelperTest {

    @Test
    fun parsesTimestampWithUnits() {
        assertEquals(3723, YoutubeHelper.extractTimestampFromUrl("https://youtu.be/abcdefghijk?t=1h2m3s"))
    }

    @Test
    fun parsesPlainSecondsTimestamp() {
        assertEquals(90, YoutubeHelper.extractTimestampFromUrl("https://www.youtube.com/watch?v=abcdefghijk&t=90"))
    }

    @Test
    fun oversizedUnitValueReturnsNullInsteadOfThrowing() {
        assertNull(YoutubeHelper.extractTimestampFromUrl("https://youtu.be/abcdefghijk?t=99999999999s"))
    }

    @Test
    fun overflowingTotalReturnsNull() {
        assertNull(YoutubeHelper.extractTimestampFromUrl("https://youtu.be/abcdefghijk?t=999999h"))
    }

    @Test
    fun oversizedPlainValueReturnsNull() {
        assertNull(YoutubeHelper.extractTimestampFromUrl("https://youtu.be/abcdefghijk?t=99999999999"))
    }
}
