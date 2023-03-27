package com.example.tillist

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.res.stringResource
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tillist.ui.theme.ReceiptViewModel
import com.example.tillist.ui.theme.HomeScreen
import com.example.tillist.ui.theme.CameraScreen
import com.example.tillist.ui.theme.EditScreen
import com.example.tillist.ui.theme.ViewScreen
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

enum class ReceiptScreen() {
    Start,
    Camera,
    Edit,
    View
}

@Composable
fun ReceiptAppBar(
    currentScreen: ReceiptScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        modifier = Modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}


@Composable
fun HomeApp(
    modifier: Modifier = Modifier,
    viewmodel: ReceiptViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
    ) {


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            MainActivity().imageUri = uri!!
            navController.navigate(ReceiptScreen.Edit.name)
        })


    var cameraExecutor = Executors.newSingleThreadExecutor()
    fun handleImageCapture(uri: Uri) {
        Log.i("tag", "Image captured: $uri")

    //    imageUri = uri
        navController.navigate(ReceiptScreen.Edit.name)
    }

    //navController


    // get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()

    //get name of current screen
    val currentScreen = ReceiptScreen.valueOf(
        backStackEntry?.destination?.route ?: ReceiptScreen.Start.name
    )


    Scaffold(
        topBar = {
            ReceiptAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewmodel.uiState.collectAsState()


        NavHost(
            navController = navController,
            startDestination = ReceiptScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = ReceiptScreen.Start.name) {
                HomeScreen(
                    onCameraButtonClicked = {
                        navController.navigate(ReceiptScreen.Camera.name)
                    },
                    onFileButtonClicked = {
                        imagePicker.launch("image/*")
                    }
                )
            }

            composable(route = ReceiptScreen.Camera.name) {
                val context = LocalContext.current
                CameraScreen(
                    outputDirectory = File(stringResource(R.string.outputDir)),
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture,
                    onError = { Log.e("tag", "View error:", it) }
                )
            }

            composable(route = ReceiptScreen.Edit.name) {
                val context = LocalContext.current
                EditScreen(

                )
            }

            composable(route = ReceiptScreen.View.name) {
                val context = LocalContext.current
                ViewScreen(

                )
            }
        }

    }

}


