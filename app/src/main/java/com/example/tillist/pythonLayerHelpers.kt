package com.example.tillist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Base64
import com.chaquo.python.PyObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun processUri(imageUri: Uri, context: Context, pyObject: PyObject): Uri {
    //get bitmap from uri
    val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, imageUri!!))
    // convert bitmap to string, send to python, convert pyobj to img, write.
    val str = imageToString(bitmap)
    val img = pyObjToImg(pyObject.callAttr("imageProcessing", str))
    return saveImg(imageUri, img)
}

fun imageToString(bitmap: Bitmap): String {
    var boas = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, boas)
    // convert to byte array
    var imageBytes = boas.toByteArray()
    // encode byte array to base64 str. return.
    return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT)
}


fun pyObjToImg(obj: PyObject): Bitmap {
    // the pyObj has the img str
    var str = obj.toString()
    // convert into byte array
    var data = android.util.Base64.decode(str, Base64.DEFAULT)
    // convert to bitmap. return.
    return BitmapFactory.decodeByteArray(data, 0, data.size)
}

fun saveImg(imageUri: Uri, bitmap: Bitmap, quality: Int = 100, path: String = MainActivity().outputDir.toString()): Uri {

//    val name = getNameFromContentUri(context, imageUri)
    var dir = File(path)
    dir.mkdirs()
    var file = File.createTempFile("temp_", ".jpg", dir)
    val fou = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fou)
    fou.flush()
    fou.close()
    return Uri.fromFile(file)

}