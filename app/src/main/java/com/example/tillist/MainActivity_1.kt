package com.example.tillist
//
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.ImageDecoder
//import android.net.Uri
//import android.os.Bundle
//import android.provider.OpenableColumns
//import android.util.Base64
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.compose.setContent
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Button
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Surface
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.tillist.ui.theme.TillistTheme
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import androidx.core.content.FileProvider
//import coil.compose.AsyncImage
//import java.io.File
//import androidx.compose.ui.platform.LocalContext
//import com.chaquo.python.PyObject
//import com.chaquo.python.Python
//import com.chaquo.python.android.AndroidPlatform
//import org.opencv.core.CvType
//import org.opencv.core.Mat
//import org.opencv.core.Size
//import org.opencv.imgproc.Imgproc
//import org.opencv.android.Utils
//import org.opencv.imgproc.Imgproc.resize
//import java.io.ByteArrayOutputStream
//import java.io.FileOutputStream
//
//var cacheImgDir = ""
//
//class ComposeFileProvider : FileProvider(
//    R.xml.filepaths
//) {
//    companion object {
//        fun getImageUri(context: Context): Uri {
//            val directory = File(context.cacheDir, "images")
//            directory.mkdirs()
//            val file = File.createTempFile(
//                "selected_image_",
//                ".jpg",
//                directory,
//            )
//            val authority = context.packageName
//            return getUriForFile(
//                context,
//                authority,
//                file,
//            )
//        }
//    }
//}
//
//
//class MainActivity_1 : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            TillistTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
////                    Greeting("Android")
//                    ImagePicker()
//                }
//            }
//        }
//
//
//
//
////        OpenCVLoader.initDebug()
//
//        cacheImgDir = this.cacheDir.toString() + "/images"
//
//        // clear cache folder to prevent build up of temp images
//        Log.d("TAG", this.cacheDir.toString())
//        (this.cacheDir).deleteRecursively()
//
//
//
//        if (! Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//
//    }
//}
//
////@Composable
////fun Greeting(name: String) {
////    Surface( Modifier.fillMaxSize(), color = Color.Magenta) {
////        Text(text = "Hi, my name is $name!", modifier = Modifier.padding(24.dp), textAlign = TextAlign.Center)
////    }
////}
//
//@Composable
//fun ImagePicker(
//    modifier: Modifier = Modifier,
//){
//
//
//    val py = Python.getInstance()
//    val pyModule = py.getModule("helpers")
//
//
//
//    val context = LocalContext.current;
//    var hasImage by remember {
//        mutableStateOf(false)
//    }
//
//    var imageUri by remember {
//        mutableStateOf<Uri?>(null)
//    }
//
//    val imagePicker = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(),
//        onResult = { uri ->
//            hasImage = uri != null
//            imageUri = uri
//        })
//
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture(),
//        onResult = { success ->
//            hasImage = success
//        })
//
//
//    Box(
//        modifier = modifier
//            .fillMaxWidth(),
//    ) {
//
//        if (hasImage && imageUri != null) {
//            Log.d("TAG", imageUri.toString() + " here1")
////            imageProcessor(context, imageUri!!)
//
//            AsyncImage(
////                model = processUri(imageUri!!, context, pyModule),
//                model = imageUri!!,
//                modifier = Modifier.fillMaxWidth(),
//                contentDescription = "Selected Image",
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 0.dp),
//            horizontalArrangement = Arrangement.Center,
//        ) {
//            Button(
//                onClick = {
//                      imagePicker.launch("image/*")
//                },
//            ) {
//                Text(text = "Select Image")
//            }
//            Button(
//                modifier = Modifier.padding(top = 0.dp),
//                onClick = {
//                    val uri = ComposeFileProvider.getImageUri(context)
//                    imageUri = uri
//                    cameraLauncher.launch(uri)
//                },
//            ) {
//                Text(text = "Take Photo")
//            }
//        }
//    }
//}
//
//
//fun processUri(imageUri: Uri, context: Context, pyObject: PyObject): Uri {
//    //get bitmap from uri
//    val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, imageUri!!))
//    // convert bitmap to string, send to python, convert pyobj to img, write.
//    val str = imageToString(bitmap)
//    val img = pyObjToImg(pyObject.callAttr("imageProcessing", str))
//    return saveImg(imageUri!!, img)
//}
//
//fun imageProcessor(context: Context, imageUri: Uri) {
//
//    // get bitmap from uri and convert to opencv mat
//    // opencv imread() is unable to read from the internal app storage
//    val source = ImageDecoder.createSource(context.contentResolver, imageUri)
//    var bitmap = ImageDecoder.decodeBitmap(source)
//    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//    var mat = Mat()
//    Utils.bitmapToMat(bitmap, mat)
//
//
//    // delete original photo
//    File(imageUri.path).delete()
//
//    // image processing
//    var image = Mat()
//    val kernel = Mat.ones(5, 5, CvType.CV_8U)
//    Log.d("tag", kernel.toString())
//    Imgproc.morphologyEx(mat, image, Imgproc.MORPH_CLOSE, kernel)
//    Imgproc.morphologyEx(image, image, Imgproc.MORPH_CLOSE, kernel)
//    Imgproc.morphologyEx(image, image, Imgproc.MORPH_CLOSE, kernel)
//
//
//    var newImage = Mat()
//    val scale = 0.5
//    var w = (image.size().width * scale).toInt()
//    var h = (image.size().height * scale).toInt()
//    val conf = Bitmap.Config.ARGB_8888
//    var newBitmap = Bitmap.createBitmap(w, h, conf)
//    resize(image, newImage, Size(), scale, scale)
//
//    // convert back to bitmap
//    Utils.matToBitmap(newImage, newBitmap)
////    Imgcodecs.imwrite(imageUri.toString(), mat) does not work
//
//    Log.d("TAG", File(imageUri.path).name)
//    Log.d("TAG", cacheImgDir)
////    val fou = context.openFileOutput(File(imageUri.path).name, Context.MODE_PRIVATE)
//
//    val fou = FileOutputStream(File(cacheImgDir, File(imageUri.path).name))
//    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fou)
//    fou.close()
//
//
//
//}
//
//
//fun imageToString(bitmap: Bitmap): String {
//    var boas = ByteArrayOutputStream()
//    bitmap.compress(Bitmap.CompressFormat.PNG, 100, boas)
//    // convert to byte array
//    var imageBytes = boas.toByteArray()
//    // encode byte array to base64 str. return.
//    return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT)
//}
//
//fun pyObjToImg(obj: PyObject): Bitmap {
//    // the pyObj has the img str
//    var str = obj.toString()
//    // convert into byte array
//    var data = android.util.Base64.decode(str, Base64.DEFAULT)
//    // convert to bitmap. return.
//    return BitmapFactory.decodeByteArray(data, 0, data.size)
//}
//
//fun saveImg(imageUri: Uri, bitmap: Bitmap, quality: Int = 100, path: String = cacheImgDir): Uri {
//
////    val name = getNameFromContentUri(context, imageUri)
//    var dir = File(path)
//    dir.mkdirs()
//    var file = File.createTempFile("temp_", ".jpg", dir)
//    val fou = FileOutputStream(file)
//    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fou)
//    fou.flush()
//    fou.close()
//    return Uri.fromFile(file)
//
//}
//
//fun getNameFromContentUri(context: Context, contentUri: Uri): String {
//    val returnCursor = context.getContentResolver().query(contentUri, null, null, null, null)
//    val nameColumnIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//    returnCursor.moveToFirst()
//    val fileName = returnCursor.getString(nameColumnIndex)
//    return fileName
//}
//
////    try {
//////        Files.deleteIfExists(Paths.get(imageUri.path))
////
////        var file = File(path, File(imageUri.path).name)
////        val fou = FileOutputStream(file)
////        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fou)
////        fou.flush()
////        fou.close()
////        return Uri.fromFile(file)
////    } catch (e: java.lang.Exception) {
////        e.printStackTrace()
////        Log.i(null, "Save file error")
////    }
//
//
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    TillistTheme {
//        // A surface container using the 'background' color from the theme
//        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
////                    Greeting("Android")
//            ImagePicker()
//        }
//    }
//}
//
//
//
//
