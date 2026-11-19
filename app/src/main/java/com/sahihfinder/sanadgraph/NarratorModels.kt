package com.sahihfinder.sanadgraph

import androidx.compose.ui.graphics.Color

enum class NarratorGrade(val label: String, val color: Color) {
    PROPHET("Source of Revelation", Color(0xFFD4AF37)),
    SAHABI("Sahabi ('Adl)", Color(0xFF0F5132)),
    THIQAH("Thiqah (Trustworthy)", Color(0xFF16A34A)),
    SADUQ("Saduq (Truthful)", Color(0xFF0284C7)),
    DAIF("Da'if (Weak / Broken)", Color(0xFFDC2626))
}

data class NarratorNode(
    val id: String,
    val name: String,
    val deathAH: Int,
    val rank: String,
    val grade: NarratorGrade,
    val bio: String,
    val parentId: String? = null,
    val brokenLink: Boolean = false
)

data class HadithChain(
    val id: String,
    val title: String,
    val text: String,
    val isSahih: Boolean,
    val nodes: List<NarratorNode>
)

object HadithRepository {
    val chains = listOf(
        HadithChain(
            id = "intentions",
            title = "Hadith #1: Actions & Intentions",
            text = "\"Verily, actions are judged by intentions...\" — Sahih al-Bukhari 1",
            isSahih = true,
            nodes = listOf(
                NarratorNode("prophet", "Prophet Muhammad ﷺ", 11, "Source", NarratorGrade.PROPHET, "Delivered on the pulpit in Madinah."),
                NarratorNode("umar", "Umar ibn al-Khattab", 23, "Sahabi", NarratorGrade.SAHABI, "Second Caliph; heard directly from the Prophet ﷺ.", "prophet"),
                NarratorNode("alqamah", "Alqamah ibn Waqqas", 73, "Senior Tabi'i", NarratorGrade.THIQAH, "Trusted Madinan Tabi'i who heard from Umar.", "umar"),
                NarratorNode("muhammad", "Muhammad b. Ibrahim", 120, "Tabi'i", NarratorGrade.THIQAH, "Reputable Madinan jurist.", "alqamah"),
                NarratorNode("yahya", "Yahya ibn Sa'id", 143, "Junior Tabi'i", NarratorGrade.THIQAH, "Over 200 scholars narrated this Hadith from him.", "muhammad"),
                NarratorNode("sufyan", "Sufyan ibn Uyaynah", 198, "Atba' al-Tabi'in", NarratorGrade.THIQAH, "Grand Imam of Makkah.", "yahya"),
                NarratorNode("malik", "Imam Malik", 179, "Imam", NarratorGrade.THIQAH, "Recorded in Al-Muwatta.", "yahya"),
                NarratorNode("shubah", "Shu'bah ibn al-Hajjaj", 160, "Imam", NarratorGrade.THIQAH, "Master of Hadith criticism in Basra.", "yahya"),
                NarratorNode("bukhari", "Imam al-Bukhari", 256, "Compiler", NarratorGrade.THIQAH, "Opening Hadith of Sahih al-Bukhari.", "sufyan"),
                NarratorNode("abudawud", "Imam Abu Dawud", 275, "Compiler", NarratorGrade.THIQAH, "Recorded in Sunan Abi Dawud.", "malik"),
                NarratorNode("muslim", "Imam Muslim", 261, "Compiler", NarratorGrade.THIQAH, "Recorded in Sahih Muslim.", "shubah")
            )
        ),
        HadithChain(
            id = "gold_chain",
            title = "Silsilat al-Dhahab (Golden Chain)",
            text = "\"Do not undercut the sale of your brother...\" — Al-Muwatta & Bukhari",
            isSahih = true,
            nodes = listOf(
                NarratorNode("prophet", "Prophet Muhammad ﷺ", 11, "Source", NarratorGrade.PROPHET, "Source of the Sunnah."),
                NarratorNode("ibnumar", "Abdullah ibn Umar", 73, "Sahabi", NarratorGrade.SAHABI, "Meticulous Companion in Madinah.", "prophet"),
                NarratorNode("nafi", "Nafi' Mawla Ibn Umar", 117, "Tabi'i", NarratorGrade.THIQAH, "Studied under Ibn Umar for 30 years.", "ibnumar"),
                NarratorNode("salim", "Salim ibn Abdullah", 106, "Tabi'i", NarratorGrade.THIQAH, "Son of Ibn Umar; one of the Seven Fuqaha.", "ibnumar"),
                NarratorNode("malik", "Imam Malik", 179, "Imam", NarratorGrade.THIQAH, "The Golden Chain: Malik → Nafi' → Ibn Umar.", "nafi"),
                NarratorNode("zuhri", "Ibn Shihab al-Zuhri", 124, "Tabi'i", NarratorGrade.THIQAH, "First official compiler of Hadith.", "salim"),
                NarratorNode("bukhari", "Imam al-Bukhari", 256, "Compiler", NarratorGrade.THIQAH, "Recorded in Sahih al-Bukhari.", "malik"),
                NarratorNode("shafii", "Imam al-Shafi'i", 204, "Mujtahid", NarratorGrade.THIQAH, "Narrated directly from Imam Malik.", "malik")
            )
        ),
        HadithChain(
            id = "broken_example",
            title = "Case Study: Broken Chain (Munqati')",
            text = "Illustrates how Hadith critics spot a missing link when a student never met a teacher.",
            isSahih = false,
            nodes = listOf(
                NarratorNode("prophet", "Prophet Muhammad ﷺ", 11, "Source", NarratorGrade.PROPHET, "Source of narration."),
                NarratorNode("umar", "Umar ibn al-Khattab", 23, "Sahabi", NarratorGrade.SAHABI, "Martyred in 23 AH.", "prophet"),
                NarratorNode("hasan", "Al-Hasan al-Basri", 110, "Tabi'i", NarratorGrade.THIQAH, "Born in 21 AH (age 2 when Umar died). Never heard directly from Umar.", "umar", brokenLink = true),
                NarratorNode("weak", "Unverified Transmitter", 165, "Transmitter", NarratorGrade.DAIF, "Critiqued for frequent errors in attribution.", "hasan")
            )
        )
    )
}
