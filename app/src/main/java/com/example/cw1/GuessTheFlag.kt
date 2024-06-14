package com.example.cw1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cw1.ui.theme.CW1Theme
import java.util.Locale


class GuessTheFlag : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CW1Theme {
                // Assume CW1Theme is your Compose Theme

                // Parse JSON and get the list of countries
                val countries = parseCountriesJson(LocalContext.current)

                // Get three random countries from the list
                var randomCountries by rememberSaveable {
                    mutableStateOf(
                        getRandomThreeCountries(countries)
                    )
                }

                var correctCountry by remember {
                    mutableStateOf(randomCountries.random())
                }
                var (correctCountryCode, correctCountryName) = correctCountry

                var showDialog by remember { mutableStateOf(false) }
                var answerCorrect by remember { mutableStateOf(false) }

                var answer by rememberSaveable {
                    mutableStateOf(false)
                }

                var nextButtonState by rememberSaveable {
                    mutableStateOf(false)
                }

                if (showDialog) {
                    MinimalDialog(
                        text1 = if (answerCorrect) "CORRECT!" else "WRONG!",
                        text2 = "",
                        textColor = if (answerCorrect) Color.Green else Color.Red,
                        onDismissRequest = {
                            showDialog = false
                        })
                }
                //---------------------------------------------------------------------------------
                //timer
                var istimerOn = intent.getBooleanExtra("timer", false)
                Log.d("timerState", istimerOn.toString())

                var timer = (rememberCountdownTimerState(initialMillis = 10000).value) / 1000
                //---------------------------------------------------------------------------------

                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text("Guess The Flag")
                            }
                        )
                    },

                    ) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 40.dp)
                    ) {
                        //-------------------------------------------------------------


                        Row() {
                            //Timer
                            if (istimerOn == true) {
                                Text(
                                    text = "Timer: $timer",
                                )

                            }
                        }

                        Row() {
                            Text(
                                correctCountryName,
                                fontSize = 25.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp),
                                textAlign = TextAlign.Center,
                            )
                        }



                        randomCountries.forEach { country ->
                            val flagDrawableId = resources.getIdentifier(
                                country.first.lowercase(Locale.getDefault()),
                                "drawable",
                                packageName
                            )

                            //Text(text = country.toString())
                            //to check random country
                            Log.d("Random Country", country.second.toString())

                            Image(
                                painter = painterResource(id = flagDrawableId),
                                contentDescription = "Flag of ${country.second}",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .aspectRatio(16f / 9f)
                                    .size(100.dp)
                                    .padding(10.dp)
                                    .clickable {
                                        answerCorrect = country == correctCountry
                                        showDialog = true

                                    }
                            )
                        }


                        Button(
                            onClick = {
                                nextButtonState = nextButtonState

                                //to reset values
                                if (!nextButtonState) {
                                    randomCountries = getRandomThreeCountries(countries)
                                    correctCountry = randomCountries.random()
                                    showDialog = false
                                    answerCorrect = false
                                }

                            },
                            modifier = Modifier
                                .padding(top = 16.dp)
                        ) {
                            Text("Next")
                        }
                    }
                }
            }
        }
    }
}

