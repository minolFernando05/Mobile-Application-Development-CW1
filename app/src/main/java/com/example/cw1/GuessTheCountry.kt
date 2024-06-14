package com.example.cw1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.cw1.ui.theme.CW1Theme
import org.json.JSONObject


class GuessTheCountry : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CW1Theme {
                //---------------------------------------------------------------------------------

                // Parse JSON and get the list of countries
                val countries = parseCountriesJson(LocalContext.current)

                // Get a random country from the list
                var randomCountry by rememberSaveable {
                    mutableStateOf(getRandomCountry(countries))
                }

                var (randomCountryCode, randomCountryName) = randomCountry


                //Remember states of code and name
                var answer by rememberSaveable {
                    mutableStateOf(false)
                }

                var submitButtonState by rememberSaveable {
                    mutableStateOf(false)
                }

                //var isSelected by remember { mutableStateOf(false) }
                var showDialog by remember { mutableStateOf(false) }

                if (showDialog) {
                    MinimalDialog(
                        text1 = if (answer) "CORRECT!" else "WRONG!",
                        text2 = randomCountryName,
                        textColor = if (answer) Color.Green else Color.Red,
                        onDismissRequest = { showDialog = false })
                }

                val selectedCountryName = remember { mutableStateOf<String?>(null) }

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
                                Text("Guess The Country")
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


                        val context = LocalContext.current

                        //to get random flag for image id
                        val randomFlagDrawable = context.resources.getIdentifier(
                            randomCountryCode?.lowercase(),
                            "drawable",
                            context.packageName
                        )

                        //to check random country
                        Log.d("Random Country", randomCountryName.toString())


                        if (istimerOn == true) {
                            Text(
                                text = "Timer: $timer",
                            )

                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        )

                        {
                            //show image
                            Image(
                                painter = painterResource(id = randomFlagDrawable),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .aspectRatio(16f / 9f)
//                                    .border(BorderStroke(1.dp, Color.Black)),
                            )
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                        ) {
                            items(countries) { (code, name) ->

                                //Show list of cards of country names
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(2.dp)
                                        .clickable {
                                            selectedCountryName.value = name
                                        },
                                    border = if (selectedCountryName.value == name) BorderStroke(
                                        2.dp,
                                        Color.Red
                                    ) else null,
                                ) {
                                    Text(
                                        text = (name),
                                        modifier = Modifier
                                            .padding(20.dp),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }


                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp)
                        )
                        {
                            Button(onClick = {
                                // Handle correct selection
                                if (selectedCountryName.value == randomCountryName) {
                                    answer = true
                                }

                                showDialog = true
                                submitButtonState = !submitButtonState

                                //to reset values
                                if (!submitButtonState) {
                                    randomCountry = getRandomCountry(countries)
                                    showDialog = false
                                    answer = false
                                }


                            }) {
                                Text(if (submitButtonState) "Next" else "Submit")
                            }

                        }
                    }
                }
            }

        }
    }
}


//Data Class to hold country information
//data class Country(val code: String, val name: String)

// Function read JSON and return a list of countries
fun parseCountriesJson(context: Context): MutableList<Pair<String, String>> {
    val countriesList = mutableListOf<Pair<String, String>>()
    val jsonString = context.assets.open("countries.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(jsonString)
    val keys = jsonObject.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = jsonObject.getString(key)
        countriesList.add(Pair(key, value))
    }
    return countriesList
}

fun getRandomCountry(countries: MutableList<Pair<String, String>>): Pair<String, String> {
    return countries.random()
}

//alert Dialog box
@Composable
fun MinimalDialog(text1: String, text2: String, textColor: Color, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                //.height(160.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        )
        {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                //verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text1,
                    color = textColor,
                    fontSize = 50.sp,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = text2,
                    color = Color.Blue,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}