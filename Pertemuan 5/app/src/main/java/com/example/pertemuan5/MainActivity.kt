package com.example.pertemuan5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.pertemuan5.ui.theme.Pertemuan5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            Pertemuan5Theme(){
                DiceRollerApp()
            }
        }
    }
}

@Preview(showBackground=true)
@Composable
fun DiceRollerApp(){
    DiceRollerWithImageAndButton(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(align = Alignment.Center)
    )
}

@Composable
fun DiceRollerWithImageAndButton(modifier: Modifier = Modifier){
    var result by remember {
        mutableStateOf(1)
    }
    var imageOfDice = when(result){
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
        ,modifier = modifier
    ){
        Image(painter = painterResource(id = imageOfDice), contentDescription = null)
        Button(onClick = {result = (1.. 6).random()}){
            Text(text = "Roll")
        }
    }
}