package life.qbic

import org.junit.Test
import kotlin.test.assertEquals

class KotlinRegexTest {

    fun testRegex(pattern: String, apply: String) : Boolean{
        val escapedPattern = """$pattern""".toRegex()
        return (escapedPattern.containsMatchIn(apply))
    }

    @Test
    fun simpleRegex() {
        val simpleRegex = "jobscript.FastQC."
        assertEquals(true, testRegex(simpleRegex, "jobscript.FastQC.blabliblub"))
        assertEquals(false, testRegex(simpleRegex, "jobshit.FaSTTT"))
    }

    @Test
    fun complexRegex() {
        val complexRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#${'$'}%!\\-_?&])(?=\\S+${'$'}).{8,}"
        assertEquals(true, testRegex(complexRegex, "Align@123"))
    }

    @Test
    fun moderatelyComplexRegex() {
        val moderatelyComplexRegex = "^[a-z0-9_-]{3,16}$"
        assertEquals(true, testRegex(moderatelyComplexRegex, "my-us3r_n4m3"))
        assertEquals(false, testRegex(moderatelyComplexRegex, "th1s1s-wayt00_l0ngt0beausername"))
    }

}