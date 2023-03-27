package com.example.tillist

import android.Manifest
import android.app.Activity
import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.renderscript.ScriptGroup.Input
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.tillist.ui.theme.TillistTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
//import com.example.camerajetpackcomposevideo.ui.theme.CameraJetpackComposeVideoTheme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.tillist.ui.theme.CameraScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {

    lateinit var outputDir: File
     public lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private var navInt: MutableState<Int> = mutableStateOf(0)
    lateinit var imageUri: Uri

    public lateinit var db: ReceiptDatabase
    public lateinit var dbDao: ReceiptDao

//    var totalPrice: Double = 0.00
    var totalPrice = mutableStateOf("")
//    var purchaseDate: String = ""
    var purchaseDate = mutableStateOf("")
    var storeName = mutableStateOf("")
    var receiptTag = mutableStateOf("")
    var receiptId = mutableStateOf(-1)

    var receiptValues = mutableListOf<Receipt>()



    lateinit var myPrefs: SharedPreferences

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("tag", "Permission granted")
//            shouldShowCamera.value = true
        } else {
            Log.i("tag", "Permission denied")
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            if (shouldShowCamera.value) {
//                CameraView(
//                    outputDirectory = outputDirectory,
//                    executor = cameraExecutor,
//                    onImageCaptured = ::handleImageCapture,
//                    onError = { Log.e("tag", "View error:", it) }
//                )
//            }
//
//
//            if (shouldShowPhoto.value) {
//                Image(
//                    painter = rememberAsyncImagePainter(imageUri),
//                    contentDescription = null,
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
//
//
//        // clear cache folder to prevent build up of temp images
//        Log.i("TAG", "cachDir: " + this.cacheDir.toString())
//        (this.cacheDir).deleteRecursively()
//
//        if (! Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//
//
//
//        requestCameraPermission()
//
//        outputDirectory = getOutputDirectory()
//        cameraExecutor = Executors.newSingleThreadExecutor()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Log.i("TAG", "shouldShowCamera = " + shouldShowCamera.value.toString())
        val tempCache = File(cacheDir.toString() + "/images").mkdirs()

        // clear cache folder to prevent build up of temp images
        Log.i("TAG", "cachDir: " + this.cacheDir.toString())
        (this.cacheDir).deleteRecursively()

        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }


        db = ReceiptDatabase.getDatabase(this)
        dbDao = db.receiptDao()
//        lifecycleScope.launch {
//            db.clearAllTables()
//        }

//        val testReceipt1 = Receipt(12.60, "Walmart", "12/07/22", "imaginaryImageUrl.jpg")
//        val testReceipt2 = Receipt(3.50, "Lowe's", "1/08/22", "imaginaryImageUrl2.jpg")

//        var receipts = mutableListOf<Receipt>()
//        lifecycleScope.launch {
//        dbDao.insertAll(testReceipt1)
//        dbDao.insertAll(testReceipt2)
//        var receipts = dbDao.getAll()
//        }

//        var receipts = dbDao.getAll()


        myPrefs = getSharedPreferences("myPrefs", 0)

        // on first run of the app, add the default settings/preferences
        val settingsEditor = myPrefs.edit()
        if (!myPrefs.getBoolean("saveImages", false)) {
            settingsEditor.putBoolean("saveImages", true)
        }


        outputDir = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()



        Log.i("TAG", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path)
//        Log.i("TAG", LocalContext.current.getExternalFilesDir().path)
        Log.i("TAG", this.externalMediaDirs.toString())


        setContent {
            TillistTheme() {
                androidx.compose.material.Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    imagePicker()
//                    HomeApp()
                }
            }
        }


    }



    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun imagePicker(
        modifier: Modifier = Modifier,
    ){

        val py = Python.getInstance()
        val pyModule = py.getModule("helpers")
        val context = LocalContext.current;


        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = { uri ->
                imageUri = uri!!

                context.contentResolver.takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shouldShowPhoto.value = true
                navInt.value = 2
            })



        when (navInt.value) {

            // Database Delete confirmation
            5 -> {
                Scaffold(
                    topBar = {appBar("Are you sure?")}
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            modifier = Modifier
                                .background(Color.Red)
                                .border(5.dp, Color.Black)
                                .padding(10.dp),
                            onClick = {
                                lifecycleScope.launch {
                                    db.clearAllTables()
                                }

                                mToast(context, "Database Deleted")
                                navInt.value = 0
                            }
                        ) {
                           Text(text = "DELETE")
                        }
                        Button(onClick = { navInt.value = 0 }) {
                            Text(text = "Back")
                        }
                    }
                }
            }



            // settings view
            4 -> {
                Scaffold(
                    topBar = {appBar("Settings")}
                ) {
                    
                    Column() {
                        Row {
                            Text(text = "Save Pictures")
                            var checkedState by remember { mutableStateOf(myPrefs.getBoolean("saveImages", true)) }
                            Switch(checked = checkedState, onCheckedChange = {
                                myPrefs.edit().putBoolean("saveImages", it)
                                checkedState = it
                            })
                        }
                        Row {
                            Button(onClick = { navInt.value = 5 }) {
                                Text(text = "DELETE ENTIRE DATABASE")
                            }
                        }
                        Row {
                            Button(onClick = {
                                requestFilePermission()
                                exportCsv(dbDao.getAll())
                                mToast(context, "Exported into documents folder")
                            }) {
                                Text(text = "Export as CSV")
                            }
                        }
                    }
                    
                }

            }



            // Receipt list view
            3 -> {

                Scaffold(
                    topBar = {appBar("List")}
                ) {
                    val len = receiptValues.size

    //                LazyColumn {
    //                    items(len) { index ->
    //
    //                        Row() {
    //                            var data = values[index]
    //
    //                            Text(text = data.storeName!!)
    //                            Text(text = data.totalPrice.toString())
    //                            Text(text = data.dateOfPurchase!!)
    //                            Text(text = data.storeName!!)
    //                            Text(text = data.tag!!)
    //                        }
    //
    //                    }
    //                }

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        for (receipt in receiptValues) {
                            Row(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .height(60.dp)
                                    .fillMaxWidth(1f)
                                    .clip(shape = RoundedCornerShape(5.dp))
                                    .background(Color.hsv(0f, 0f, .3f))
                                    .border(1.dp, Color.White, shape = RoundedCornerShape(5.dp)),
                                verticalAlignment = Alignment.CenterVertically,

                            ) {
                                var modifier = Modifier
//                                    .padding(5.dp)
                                Text(modifier = modifier.fillMaxWidth(.3f), textAlign = TextAlign.Center, text = receipt.storeName ?: "")
                                Text(modifier = modifier.fillMaxWidth(.2f), textAlign = TextAlign.Center, text = "$" + (receipt.totalPrice.toString() ?: ""))
                                Text(modifier = modifier.fillMaxWidth(.5f), textAlign = TextAlign.Center, text = receipt.dateOfPurchase ?: "")
                                Text(modifier = modifier.fillMaxWidth(.7f), textAlign = TextAlign.Center, text = receipt.tag ?: "") // check for null. return empty string if null.



                                var expanded by remember { mutableStateOf(false) }
                                Box(modifier = Modifier) {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = "more options"
                                        )
                                    }
                                    DropdownMenu(expanded = expanded,
                                        onDismissRequest = { expanded = false }

                                    ) {
                                        DropdownMenuItem(onClick = {
                                            loadReceipt(receipt)
                                            navInt.value = 2
                                        }) {
                                            Text(text = "Edit")
                                        }
                                        Divider()
                                        DropdownMenuItem(onClick = {
                                            dbDao.delete(receipt)
                                        }) {
                                            Text(text = "Delete")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }




            // Edit/save receipt view
            2 -> {
                Log.i("tag", "Displaying: " + imageUri.toString())
                // if editing receipt, dont perform OCR
                var title = "Edit Receipt"
                if (receiptId.value == 0) {
                    ocrImage(imageUri)
                    title = "Receipt Entry"
                }
                Scaffold(
                    topBar = {appBar(title)}
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // image
                        AsyncImage(
                            //                        model = processUri(imageUri, context, pyModule),
                            model = imageUri,
                            modifier = Modifier.fillMaxHeight(.5f),
                            contentDescription = "Selected Image"
                        )

                        // textfields
//                        SimpleFilledTextFieldSample("Store", storeName)
//                        SimpleFilledTextFieldSample("Total Price", totalPrice)
//                        SimpleFilledTextFieldSample("Date", purchaseDate)
//                        SimpleFilledTextFieldSample("Tag")
                        var storeNameField by remember { storeName }
                        TextField(
                            value = storeNameField,
                            onValueChange = {storeNameField = it},
                            label = {Text("Store")}
                        )
                        var totalPriceField by remember { totalPrice }
                        TextField(
                            value = totalPriceField,
                            onValueChange = {totalPriceField = it},
                            label = {Text("Total Price")}
                        )
                        var purchaseDateField by remember { purchaseDate }
                        TextField(
                            value = purchaseDateField,
                            onValueChange = {purchaseDateField = it},
                            label = {Text("Date")}
                        )
                        var tagField by remember { receiptTag }
                        TextField(
                            value = tagField,
                            onValueChange = {tagField = it},
                            label = {Text("Tag")}
                        )

                        // Save button
                        Button(
                            onClick = {
                                val inputReceipt = Receipt(
                                    totalPrice.value.toDouble(),
                                    storeName.value,
                                    purchaseDate.value,
                                    imageUri.toString(),
                                    receiptTag.value,
                                    receiptId.value
                                )


                                if (!receiptId.value.equals(0)) {
                                    Log.i("tag", "Receipt edited in database: $inputReceipt")
                                    dbDao.updateReceipt(inputReceipt)
                                    clearReceipt()
                                    receiptValues = dbDao.getAll()
                                    navInt.value = 3
                                } else {
                                    Log.i("tag", "Receipt saved into datbase: $inputReceipt")
                                    dbDao.insertAll(inputReceipt)
                                    clearReceipt()
                                    navInt.value = 0
                                }

                            }) {
                            Text(text = "Save")
                        }
                    }
                }
            }





            1 -> {
                CameraScreen(
                    outputDirectory = outputDir,
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture,
                    onError = { Log.e("tag", "View error:", it) }
                )
            }





            0 -> {
                Scaffold(
                    topBar = {appBar("Tillist")}
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {

                        Row(
                            modifier = Modifier
//                                .align(Alignment.TopCenter)
                                .padding(bottom = 0.dp)
                                .fillMaxWidth()
                                .fillMaxHeight(.3f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            var modifier = Modifier
                                .height(80.dp)
                                .padding(horizontal = 2.dp)
                            Button(
                                modifier = modifier,
                                onClick = {
                                    imagePicker.launch(arrayOf("image/*"))
                                },
                            ) {
                                Text(text = "Select Image")
                            }
                            Button(
                                modifier = modifier,
                                onClick = {
                                    requestCameraPermission()
                                    shouldShowCamera.value = true
                                    navInt.value = 1
                                },
                            ) {
                                Text(text = "Take Photo")
                            }
                        }
                        
                        //tags
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(.3f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                            ) {
                                Text(text = "Tags")
                            }
                            Divider(color = Color.Gray, modifier = Modifier.fillMaxWidth())
                            Row() {
                                // tag boxes/buttons
                                var tagList = dbDao.getTagList()
                                for (tag in tagList) {
                                    if (tag == "") {

                                    } else {
                                        val size = 100.dp
                                        Button(modifier = Modifier
                                            .height(size)
                                            .width(size)
                                            .padding(2.dp)
                                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp)),
                                            onClick = {
                                                receiptValues = dbDao.loadAllByTag(tag) as MutableList<Receipt>
                                                navInt.value = 3
                                            }
//                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(text = tag)
                                        }
                                    }
                                }

                            }
                        }

                        // recent receipts
                        Column() {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                            ) {
                                Text(text = "Recent")
                                Spacer(Modifier.weight(1f))
                                Button(
                                    modifier = Modifier,
                                    onClick = {
                                        receiptValues = dbDao.getAll()
                                        navInt.value = 3
                                    }
                                ) {
                                    Text(text = "View all")
                                }
                            }
                            Divider(color = Color.Gray, modifier = Modifier.fillMaxWidth())


                            var values = dbDao.getRecent(8)
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                            ) {
                                for (receipt in values) {
                                    Row(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .height(60.dp)
                                            .fillMaxWidth(1f)
                                            .clip(shape = RoundedCornerShape(5.dp))
                                            .background(Color.hsv(0f, 0f, .3f))
                                            .border(
                                                1.dp,
                                                Color.White,
                                                shape = RoundedCornerShape(5.dp)
                                            ),
                                        verticalAlignment = Alignment.CenterVertically,

                                        ) {
                                        var modifier = Modifier
//                                    .padding(5.dp)
                                        Text(modifier = modifier.fillMaxWidth(.3f), textAlign = TextAlign.Center, text = receipt.storeName ?: "")
                                        Text(modifier = modifier.fillMaxWidth(.2f), textAlign = TextAlign.Center, text = "$" + (receipt.totalPrice.toString() ?: ""))
                                        Text(modifier = modifier.fillMaxWidth(.5f), textAlign = TextAlign.Center, text = receipt.dateOfPurchase ?: "")
                                        Text(modifier = modifier.fillMaxWidth(.7f), textAlign = TextAlign.Center, text = receipt.tag ?: "") // check for null. return empty string if null.



                                        var expanded by remember { mutableStateOf(false) }
                                        Box(modifier = Modifier) {
                                            IconButton(onClick = { expanded = true }) {
                                                Icon(
                                                    imageVector = Icons.Filled.MoreVert,
                                                    contentDescription = "more options"
                                                )
                                            }
                                            DropdownMenu(expanded = expanded,
                                                onDismissRequest = { expanded = false }

                                            ) {
                                                DropdownMenuItem(onClick = {
                                                    loadReceipt(receipt)
                                                    navInt.value = 2
                                                }) {
                                                    Text(text = "Edit")
                                                }
                                                Divider()
                                                DropdownMenuItem(onClick = {
                                                    dbDao.delete(receipt)
                                                }) {
                                                    Text(text = "Delete")
                                                }
                                            }
                                        }
                                    }
                                }


                            }
                        }

                    }
                }
            }



        }
    }



    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("tag", "Permission previously granted")
//                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("tag", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private fun requestFilePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("tag", "Permission previously granted")
//                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> Log.i("tag", "Show file-write permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun handleImageCapture(uri: Uri) {
        Log.i("tag", "Image captured: $uri")
        shouldShowCamera.value = false

        imageUri = uri
        shouldShowPhoto.value = true
        navInt.value = 2
    }

    private fun getOutputDirectory(): File {
//        val mediaDir = externalMediaDirs.firstOrNull()?.let {
//            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
//        }

        var dir = File(this.cacheDir.toString() + "/images")
        dir.mkdirs()

        // check the settings to see if the user wants to save images or not. Not = dir is cache
        return if (!myPrefs.getBoolean("saveImages", true)) {
            var dir = File(this.cacheDir.toString() + "/images")
            dir.mkdirs()

            dir
        } else {
            var dir = File(this.filesDir.toString() + "/images")
            dir.mkdirs()
            dir
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun ocrImage(uri: Uri) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // get image from file
        val image: InputImage
        image = InputImage.fromFilePath(this, uri)
//        try {
//            image = InputImage.fromFilePath(this, imageUri)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }

        // process image
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                Log.i("tag", "OCR successful!")
//                val text = visionText

                textLog(visionText, "OCR", 2)
                calculate(visionText)

            }
            .addOnFailureListener { e ->
                Log.i("tag", "error with ml kit OCR: " + e.toString())
            }


    }

//    fun getHorizontalLines(): Array<String> {
//
//
//    }

    fun textLog(text: Text, tag: String, option: Int) { // 0 = block, 1 = line, 2 = text
        when (option) {
            0 -> { Log.i(tag, text.text) }
            1 -> {
                for (block in text.textBlocks) {
                    Log.i(tag, block.text) }
            }
            2 -> {
                for (block in text.textBlocks) {
                    for (line in block.lines) {
                        Log.i(tag, line.text + "    ||||||    Coord Center: (" + line.boundingBox?.centerX() + ", " + line.boundingBox?.centerY() + ")") } }
            }
        }
    }

    fun horizontalLines(text: Text) {
        var list = getList(text)


    }

    fun getList(text: Text): List<String> {
//        var list = mutableListOf<String>()
//        for (block in text.textBlocks) {
//            for (line in block.lines) {
//                list.add(line.text)
//            }
//        }
//        return list

        return text.text.split("\n")
    }

    fun getLineHeight(rect: Rect) {
        rect.exactCenterY()
        rect.height()
    }

    fun getPrices(list: List<String>): MutableList<Int> {
        val pattern = Regex("^ *\\d*\\.\\d{2}(?!\\d)(?<!-)")
        var numbers = mutableListOf<Int>()
        list.forEachIndexed { index, x ->
            if (pattern.containsMatchIn(x)) {
                numbers.add(index)
            }
        }
        // returns list of indicies that are prices
        return numbers
    }

    fun calculate(text: Text) {
        var list = getList(text)
        var priceIndexes = getPrices(list)
        var priceList = getListWithIndex(list, priceIndexes)


        // clean the prices
        val pattern = Regex("^ *\\d*\\.\\d{2}(?!\\d)(?<!-)")
        var priceListD = mutableListOf<Double>()
        priceList.forEachIndexed { index, x ->
            var temp = pattern.find(x)!!.value
            priceListD.add(temp.toDouble())
        }

        priceListD.sort()

        totalPrice.value = priceListD.last().toString()


        //Parse the date

        val datePattern = Regex("\\d{1,4}\\/\\d{1,4}\\/\\d{1,4}")

        for (x in list) {
            if (datePattern.containsMatchIn(x)) {
                purchaseDate.value = datePattern.find(x)!!.value
            }
        }


        // calculate store name
        var tallest = 0
        for (block in text.textBlocks) {
            for (line in block.lines) {
                var heightx = line.boundingBox?.height()
                if (1 == heightx?.compareTo(tallest)) {
                    tallest = heightx
                    storeName.value = line.text
                }
            }
        }

    }


    fun getListWithIndex(list: List<String>, index: List<Int>): MutableList<String> {
        var output = mutableListOf<String>()
        for (x in index){
            output.add(list[x])
        }
        return output
    }

//    regex for finding prices
//    ^ *\d*\.\d{2} ?[^\w](?<!-)

//    regex for finding date
//    ^ *\d+\/*\d+\/*\d{2} *


    @Composable
    fun SimpleFilledTextFieldSample(labelText: String, data: MutableState<String> = mutableStateOf("")) {
        var text by remember { data }
        TextField(
            value = text,
            onValueChange = {text = it},
            label = {Text(labelText)}
        )
    }

    @Composable
    fun receiptViewRow() {
        Row(modifier = Modifier.fillMaxWidth()) {

        }
    }

    @Composable
    fun appBar(
        title: String = "",
        modifier: Modifier = Modifier
    ) {
        TopAppBar(
            title = { Text(title) },
            modifier = modifier,
//                .height(20.dp),
            navigationIcon = {
                if (true) {
                    IconButton(onClick = {
                        clearReceipt()
                        when (navInt.value){
                            0 -> {
                                val activity: MainActivity = MainActivity()
                                // on below line we are finishing activity.
                                activity.finish()
                                java.lang.System.exit(0)
                            }
                            1 -> {}
                            2 -> {
                                if (receiptId.equals(0)) {
                                    navInt.value = 0
                                } else {
                                    navInt.value = 3
                                }
                            }
                            3 -> {navInt.value = 0}
                            4 -> {navInt.value = 0}
                        }
                        navInt.value = 0
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back button"
                        )
                    }
                }
            }, actions = {
                IconButton(onClick = { navInt.value = 4}) {
                    Icon(Icons.Filled.Settings, null)
                }
            }

        )
    }

    fun loadReceipt(receipt: Receipt) {
        totalPrice.value = receipt.totalPrice.toString()
        storeName.value = receipt.storeName!!
        purchaseDate.value = receipt.dateOfPurchase!!
        receiptTag.value = receipt.tag!!
        receiptId.value = receipt.id
        imageUri = Uri.parse(receipt.imageUrl)
    }

    fun clearReceipt() {
        totalPrice.value = ""
        storeName.value = ""
        purchaseDate.value = ""
        receiptTag.value = ""
        receiptId.value = 0
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//
//        when(navInt.value){
//            0 -> {}
//            1 -> {navInt.value = 0}
//            2 -> {navInt.value = 0}
//        }
//
//
//    }

    fun exportCsv(database: List<Receipt>) {
        var exportString = "total_price, store_name, date_of_purchase, image_url, tag, id, timestamp"

        for (receipt in database) {
            var row = ""
            row += receipt.totalPrice
            row += ", "
            row += receipt.storeName
            row += ", "
            row += receipt.dateOfPurchase
            row += ", "
            row += receipt.imageUrl
            row += ", "
            row += receipt.tag
            row += ", "
            row += receipt.id
            row += ", "
            row += receipt.timeStamp

            exportString += "\n" + row
        }

        var currentTime = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path

        var filename = "tillist_receipt_database_" + currentTime.format(formatter) + ".csv"
        var file = File("$path/$filename")

        file.writeBytes(exportString.toByteArray())

    }

    // Function to generate a Toast
    private fun mToast(context: Context, string: String){
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }


}
