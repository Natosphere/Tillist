package com.example.tillist.ui.theme

import android.net.ConnectivityManager.OnNetworkActiveListener
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tillist.MainActivity

@Composable
fun HomeScreen(
    onCameraButtonClicked: (Int) -> Unit,
    onFileButtonClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {







    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {


        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = { onFileButtonClicked },
            ) {
                Text(text = "Select Image")
            }
            Button(
                onClick = { onCameraButtonClicked(1) },
                modifier = Modifier.padding(top = 0.dp)
            ) {
                Text(text = "Take Photo")
            }
        }
    }





}