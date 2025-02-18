package com.example.fontapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.FontRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fontapp.ui.theme.FontAppTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FontAppTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(text = Destination.FontList.title)
                            },
                            navigationIcon = {
                                if (navController.previousBackStackEntry != null) {
                                    IconButton(onClick = {
                                        navController.popBackStack()
//                                        title = "Font App"
                                    }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                        )
                                    }
                                }
                            },
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Destination.FontList,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable<Destination.FontList> {
                            FontListScreen(
                                onFontSelected = { fontName, fontResId ->
                                    navController.navigate(
                                        Destination.FontDetail(
                                            fontName,
                                            fontResId,
                                        ),
                                    )
                                },
                            )
                        }
                        composable<Destination.FontDetail> { backStackEntry ->
                            val fontDetail = backStackEntry.toRoute<Destination.FontDetail>()
                            FontDetailScreen(
                                fontResId = fontDetail.fontResId,
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        val FONTS =
            mapOf(
                "Default" to null,
                "MPLUS1" to R.font.mplus1_variable_font_wght,
                "MPLUS1 CODE" to R.font.mplus1_code_variable_font_wght,
                "MPLUS2 CODE" to R.font.mplus2_variable_font_wght,
                "牟礼町 Regular" to R.font.murecho_variable_font_wght,
                "NotoSansJp" to R.font.noto_sans_jp_variable_font_wght,
                "NotoSerifJp" to R.font.noto_serif_jp_variable_font_wght,
            ).entries.toList()
    }
}

sealed interface Destination {
    val title: String

    @Serializable
    data object FontList : Destination {
        override val title: String
            get() = "Font App"
    }

    @Serializable
    data class FontDetail(
        val fontName: String,
        val fontResId: Int?,
    ) : Destination {
        override val title: String
            get() = fontName
    }
}

@Suppress("FunctionName")
@Composable
fun FontListScreen(
    onFontSelected: (fontName: String, fontResId: Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
    ) {
        itemsIndexed(MainActivity.FONTS) { index, (fontName, fontResId) ->
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }

            val fontFamily = fontResId?.let { FontFamily(Font(fontResId)) }
            Text(
                text = fontName,
                fontSize = 24.sp,
                fontFamily = fontFamily,
                modifier =
                    Modifier
                        .clickable { onFontSelected(fontName, fontResId) }
                        .fillMaxWidth()
                        .padding(16.dp),
            )
        }
    }
}

@Suppress("FunctionName")
@OptIn(ExperimentalTextApi::class)
@Composable
fun FontDetailScreen(
    @FontRes fontResId: Int?,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        (1..9).forEach { num ->
            val weight = num * 100
            val fontFamily =
                fontResId?.let {
                    FontFamily(
                        Font(
                            resId = fontResId,
                            variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
                        ),
                    )
                }

            Text(
                text = "$weight 美しいデザインの日本語フォント",
                fontFamily = fontFamily,
                fontWeight = FontWeight(weight = weight),
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
