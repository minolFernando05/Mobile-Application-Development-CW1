//video link
//https://drive.google.com/drive/folders/1klZrpOezXauYMcniXjciF0hU6msVMUl6?usp=sharing
package com.example.cw1

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cw1.ui.theme.CW1Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CW1Theme {

                //variables
                var checked by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text("Main Menu")
                            }
                        )
                    },
                ) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(15.dp)
                    )
                    {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 50.dp, bottom = 50.dp)
                        ) {
                            Text(
                                text = "Choose Game mode:",
                                fontSize = 35.sp,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        }
                        //Button 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            mainButtons(text = "Guess the Country", onclick = {
                                val act1 = Intent(applicationContext, GuessTheCountry::class.java)
                                act1.putExtra("timer", checked)
                                startActivity(act1)
                            })
                        }


                        //Button 2
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            mainButtons(text = "Guess-Hints", onclick = {
                                val act2 = Intent(applicationContext, GuessHints::class.java)
                                act2.putExtra("timer", checked)
                                startActivity(act2)
                            })
                        }

                        //Button 3
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            mainButtons(text = "Guess the Flag", onclick = {
                                val act3 = Intent(applicationContext, GuessTheFlag::class.java)
                                act3.putExtra("timer", checked)
                                startActivity(act3)
                            })
                        }

                        //Button 4
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)

                        ) {
                            mainButtons(text = "Advanced Level", onclick = {
                                val act4 = Intent(applicationContext, AdvancedLevel::class.java)
                                act4.putExtra("timer", checked)
                                startActivity(act4)
                            })
                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        ) {
                            Text(
                                text = "Timer",
                                fontSize = 20.sp,
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                }
                            )
                        }


                    }
                }

            }
        }
    }
}


@Composable
fun mainButtons(
    text: String,
    onclick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(50.dp))
        .background(MaterialTheme.colorScheme.primary)
        .clickable { onclick() }
        .padding(12.dp)
    ) {
        Text(
            text,
            color = Color.Black,
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

//Referencing code-> https://www.antondanshin.com/blog/compose-timer-implementation/
@Composable
fun rememberCountdownTimerState(
    initialMillis: Long,
    step: Long = 1000
): MutableState<Long> {
    val timeLeft = remember { mutableLongStateOf(initialMillis) }
    LaunchedEffect(initialMillis, step) {
        val startTime = SystemClock.uptimeMillis()
        while (isActive && timeLeft.longValue > 0) {
            // how much time actually passed
            val duration = (SystemClock.uptimeMillis() - startTime).coerceAtLeast(0)
            timeLeft.longValue = (initialMillis - duration).coerceAtLeast(0)
            delay(step.coerceAtMost(timeLeft.longValue))
        }
    }
    return timeLeft
}

