package com.github.catomon.moewpaper.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import moe_wallpaper.composeapp.generated.resources.Abel_Regular
import moe_wallpaper.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

//val provider = GoogleFont.Provider(
//    providerAuthority = "com.google.android.gms.fonts",
//    providerPackage = "com.google.android.gms",
//    certificates = R.array.com_google_android_gms_fonts_certs
//)
//
//val bodyFontFamily = FontFamily(
//    Font(
//        googleFont = GoogleFont("Abel"),
//        fontProvider = provider,
//    )
//)
//
//val displayFontFamily = FontFamily(
//    Font(
//        googleFont = GoogleFont("Abel"),
//        fontProvider = provider,
//    )
//)

// Default Material 3 typography values
val baseline = Typography()

//val AppTypography = Typography(
//    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
//    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
//    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
//    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
//    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
//    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
//    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
//    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
//    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
//    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
//    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
//    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
//    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
//    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
//    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
//)

@Composable
fun AppTypography(): Typography {
    val bodyFontFamily = FontFamily(
        Font(
            Res.font.Abel_Regular
        )
    )

    val displayFontFamily = FontFamily(
        Font(
            Res.font.Abel_Regular
        )
    )

    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
    )
}