package com.example.cw1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cw1.ui.theme.CW1Theme


class GuessHints : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CW1Theme {
                //---------------------------------------------------------------

                // Parse JSON and get the list of countries
                val countries = parseCountriesJson(LocalContext.current)

                // Get a random country from the list
                var randomCountry by rememberSaveable {
                    mutableStateOf(getRandomCountry(countries))
                }

                var (randomCountryCode, randomCountryName) = randomCountry

                var dashes = remember { (mutableStateListOf<Char>()) }
                if (dashes.size == 0) repeat(randomCountryName.length) {
                    dashes.add('-')
                }

                // Input text state
                var inputText by remember { mutableStateOf("") }

                var guessCounter by remember {
                    mutableStateOf(3)
                }

                //to get answer is correct state
                var changeToNext by rememberSaveable {
                    mutableStateOf(false)
                }

                //to get answer is correct state
                var answer by rememberSaveable {
                    mutableStateOf(false)
                }

                var resetStates by rememberSaveable {
                    mutableStateOf(false)
                }

                var showDialog by remember { mutableStateOf(false) }

                if (showDialog) {
                    MinimalDialog(
                        text1 = if (answer) "CORRECT!" else "WRONG!",
                        text2 = randomCountryName,
                        textColor = if (answer) Color.Green else Color.Red,
                        onDismissRequest = { showDialog = false })
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
                                Text("Guess Hints")
                            }
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {

                        //to check random country
                        Log.d("Random Country", randomCountryName.toString())

                        //Timer
                        if (istimerOn == true) {
                            Text(
                                text = "Timer: $timer",
                            )

                        }

                        val context = LocalContext.current

                        val randomFlagDrawable = context.resources.getIdentifier(
                            randomCountryCode?.lowercase(),
                            "drawable",
                            context.packageName
                        )

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = dashes.joinToString(" "),
                                fontSize = 20.sp,
                                //letterSpacing = 1.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }


                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        )

                        {
                            Image(
                                painter = painterResource(id = randomFlagDrawable),
                                contentDescription = null,
                                modifier = Modifier
                                    .border(BorderStroke(1.dp, Color.Black)),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Row()
                        {
                            TextField(
                                modifier = Modifier
                                    .padding(5.dp),
                                value = inputText,
                                onValueChange = { inputText = it },
                                label = { Text("Guess a letter") }
                            )
                            Log.d("test", guessCounter.toString())
                            Button(onClick = {
                                //run submitGuess function
                                submitGuess(
                                    randomCountryName,
                                    dashes,
                                    inputText,
                                    guessCounter = { guessCounter = guessCounter - 1 },
                                )

                                if (changeToNext) {
                                    randomCountry = getRandomCountry(countries)
                                    changeToNext = false
                                    guessCounter = 3
                                    dashes.clear()
                                    repeat(randomCountry.second.length) {
                                        dashes.add('-')
                                    }
                                }

                                //clear text field
                                inputText = ""

                                //to check full correct guess
                                if (dashes.all { it != '-' }) {
                                    answer = true
                                    changeToNext = true
                                    showDialog = true
                                    resetStates = true
                                }

                                //when loose all guess
                                else if (guessCounter == 0) {
                                    answer = false
                                    changeToNext = true
                                    showDialog = true
                                }

                            })
                            {
                                Text(if (changeToNext) "Next" else "Submit")
                            }
                        }

                        Text(text = "Remaining guesses: ${guessCounter.toString()}")

                        Log.d("test", answer.toString())

                    }
                }
            }

        }
    }
}


// Function to submit user's guess and update dashes
fun submitGuess(
    countryName: String,
    dashes: MutableList<Char>,
    guess: String,
    guessCounter: () -> Unit
) {
    //to check single character
    if (guess.length == 1) {
        val guessedChar = guess[0].lowercaseChar() // Convert to lowercase for case insensitivity
        var updatedDashes = false
        for (i in countryName.indices) {
            if (countryName[i].lowercaseChar() == guessedChar && dashes[i] == '-') {
                dashes[i] = countryName[i]
                updatedDashes = true

            }
        }
        if (!updatedDashes) {
            guessCounter()//run guess counter and reduce guesses
        }
    }
}
