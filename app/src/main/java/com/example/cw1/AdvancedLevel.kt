package com.example.cw1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cw1.ui.theme.CW1Theme
import java.util.Locale


class AdvancedLevel : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CW1Theme {
                //------------------------------------------------------------------------
                var istimerOn = intent.getBooleanExtra("timer", false)
                Log.d("timerState", istimerOn.toString())

                var timer = (rememberCountdownTimerState(initialMillis = 10000).value) / 1000

                //--------------------------------------------------------------------------

                //var isSelected by remember { mutableStateOf(false) }
                var showDialog by remember { mutableStateOf(false) }

                if (showDialog) {
                    MinimalDialog(
                        text1 = "Time up",
                        text2 = "timeup",
                        textColor = Color.Red,
                        onDismissRequest = { showDialog = false })
                }

                //var isSelected by remember { mutableStateOf(false) }
                var correctDialog by remember { mutableStateOf(false) }

                if (correctDialog) {
                    MinimalDialog(
                        text1 = "CORRECT!",
                        text2 = "",
                        textColor = Color.Green,
                        onDismissRequest = { correctDialog = false })
                }

                var wrongDialog by remember { mutableStateOf(false) }

                if (wrongDialog) {
                    MinimalDialog(
                        text1 = "WRONG!",
                        text2 = "",
                        textColor = Color.Red,
                        onDismissRequest = { wrongDialog = false })
                }
                //---------------

                // Parse JSON and get the list of countries
                val countries = parseCountriesJson(LocalContext.current)

                val (randomCountries, setRandomCountries) = remember {
                    mutableStateOf(
                        getRandomThreeCountries(countries)
                    )
                }
                val guesses = remember { mutableStateListOf("", "", "") }

                val (attempts, setAttempts) = remember { mutableStateOf(0) }
                val (score, setScore) = remember { mutableStateOf(0) }
                val correctGuesses = remember { mutableStateListOf(false, false, false) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text("Advanced Level")
                            }
                        )
                    },

                    ) { innerPadding ->


                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 60.dp)
                    )
                    {
                        // Display Score
                        Text(
                            text = "Score: $score",
                            textAlign = TextAlign.Center
                        )
                        Text(text = "Attempts: $attempts")

                        if (istimerOn == true) {
                            Text(text = "Timer: $timer")

                        }


                        randomCountries.forEachIndexed { index, country ->
                            val flagDrawable = resources.getIdentifier(
                                country.first.lowercase(Locale.getDefault()),
                                "drawable",
                                packageName
                            )

                            //to check random country
                            Log.d("Random Country", country.second.toString())

                            Image(
                                painter = painterResource(id = flagDrawable),
                                contentDescription = "Flag of ${country.second}"
                            )
                            if (attempts == 3) {
                                Text(
                                    text = country.second,
                                    color = Color.Blue
                                )
                            }
                            TextField(
                                value = guesses[index],
                                onValueChange = { if (!correctGuesses[index]) guesses[index] = it },
                                modifier = Modifier
                                    .padding(top = 10.dp, bottom = 20.dp),
                                //.background(color = if (correctGuesses[index]) Color.Green else Color.Transparent),
                                enabled = !correctGuesses[index],
                                isError = !correctGuesses[index] && attempts > 0,
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = Color.Green,
                                    errorTextColor = Color.Red
                                )
                            )
                        }

                        Log.d("score", score.toString())
                        Log.d("setScore", setScore.toString())



                        Log.d("done", timer.toString())

                        Button(onClick = {
                            if (attempts < 3) {
                                var correctCount = 0
                                guesses.forEachIndexed { index, guess ->
                                    // Check guess is correct and hasn't previously marked as correct
                                    if (guess.equals(
                                            randomCountries[index].second,
                                            ignoreCase = true
                                        ) && !correctGuesses[index]
                                    ) {
                                        correctGuesses[index] = true
                                        correctCount++
//                                        setScore(score + 1)
                                    }
                                }
                                setScore(score + correctCount)

                                Log.d("correctCount", correctCount.toString())

                                // All correct, reset for next round
                                if (correctGuesses.all { it }) {
                                    setRandomCountries(getRandomThreeCountries(countries))
                                    guesses.replaceAll { "" }
                                    correctGuesses.replaceAll { false }
                                    setAttempts(0)
                                    correctDialog = true
                                } else {
                                    setAttempts(attempts + 1)
                                }
                            }


                            //to handle max attempts or all correct
                            if (attempts >= 3 || correctGuesses.all { it }) {
                                // Show correct answers or reset for next if all correct
                                setRandomCountries(getRandomThreeCountries(countries))
                                guesses.replaceAll { "" }
                                correctGuesses.replaceAll { false }
                                setAttempts(0)
                            }


                            if (attempts == 2) {
                                wrongDialog = true
                            }

                        }) {
                            Text(if (correctGuesses.all { it } || attempts >= 3) "Next" else "Submit")
                        }

                    }
                }
            }
        }
    }
}

fun getRandomThreeCountries(countries: List<Pair<String, String>>): List<Pair<String, String>> {
    // Ensure the list has at least 3 countries to avoid an infinite loop
    if (countries.size < 3) {
        throw IllegalArgumentException("The countries list must contain at least 3 countries.")
    }

    // Shuffle the list and take the first three elements to ensure randomness and uniqueness
    return countries.shuffled().take(3)
}

