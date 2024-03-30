package com.example.cw1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cw1.ui.theme.CW1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CW1Theme {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                )
                {
                    Button(onClick = {
                        Intent(applicationContext,GuessTheFlag::class.java).also{
                            startActivity(it)
                        }
                    }) {
                        Text(text = "Guess the Country")
                    }

                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Guess-Hints")
                    }

                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Guess the Flag")
                    }

                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Advanced Level")
                    }
                }

            }
        }
    }
}










/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    )
    {
        Text("Alfred Sisley")
        Text("3 minutes ago")
        Text(
            text = "Hello $name!",
            color = Color.Blue,
        )

        Text(
            text = "this is text 2",
            color = Color.Blue,
        )
    }
}

@Preview(showBackground = true)

@Composable
fun GreetingPreview() {
    CW1Theme {
        Greeting("Minol")
    }
}
*/