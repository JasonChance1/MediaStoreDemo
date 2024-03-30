package com.example.myapplication

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.example.myapplication.databinding.ActivityMainBinding
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val permissionUtil = PermissionUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.read.setOnClickListener {
            if (!permissionUtil.isExternalStoragePermission()) permissionUtil.requestExternalStoragePermission()
            queryFiles(this)
        }

        binding.write.setOnClickListener {
            createFile()
        }

        binding.writeImage.setOnClickListener {
            createImage()
        }

        binding.writeDCIM.setOnClickListener {
            createDCIM()
        }

        binding.delete.setOnClickListener {
            deleteFile(this)
        }

        binding.readImage.setOnClickListener {
            readImages()
        }
        val bitmapList = mutableListOf<Bitmap>()

        binding.readImageByName.setOnClickListener {
            bitmapList.clear()
            bitmapList.addAll(queryImageByName("test"))
            if(bitmapList.isNotEmpty()){
                binding.imageView.setImageBitmap(bitmapList[0])
            }
        }
        var count = 0

        binding.imageView.setOnClickListener{
            if(bitmapList.isNotEmpty()){
                count++
                binding.imageView.setImageBitmap(bitmapList[count%bitmapList.size])
            }
        }


    }

    private fun createFile() {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "sdf.txt")
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Test1")
        }

        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            contentResolver.openOutputStream(uri).use { outputStream ->
                outputStream?.write("hello world".toByteArray())
            } ?: run {
                Log.e("uri", "Failed to create file")
            }

        }
    }

    private fun createImage() {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "test")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Test1")
        }
        val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val bitmap = AppCompatResources.getDrawable(this,R.drawable.img1)?.toBitmap()
        uri?.let {
            contentResolver.openOutputStream(it).use {outputStream->
                bitmap?.compress(Bitmap.CompressFormat.PNG,90,outputStream!!)
            }
        }
    }

    private fun createDCIM(){
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,"照片名")
            put(MediaStore.MediaColumns.MIME_TYPE,"image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DCIM+"/Test")
        }

        val uri:Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        val bitmap = AppCompatResources.getDrawable(this,R.drawable.img1)?.toBitmap()
        uri?.let {
            contentResolver.openOutputStream(it).use {outputStream->
                bitmap?.compress(Bitmap.CompressFormat.PNG,90,outputStream!!)
            }
        }
    }

    private fun deleteFile(context: Context) {
        queryFiles(context).forEach {
            contentResolver.delete(it,null,null)
        }
    }

    private val sb: StringBuilder = StringBuilder()

    private fun queryFiles(context: Context): List<Uri> {
        val uriList = mutableListOf<Uri>()
        sb.setLength(0)
        val projection = arrayOf(MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME)
        val uri: Uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            Log.e("查询结果数量", cursor.count.toString())
            while (cursor.moveToNext()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val contentUri: Uri = ContentUris.withAppendedId(
                    uri,
                    cursor.getLong(idColumn)
                )
                uriList.add(contentUri)
                sb.append(cursor.getString(nameColumn) + "\n")
                // 打印图片名称和URI
                Log.e("名称", cursor.getString(nameColumn))
                Log.e("URI", contentUri.toString())
            }
        }
        binding.text.text = sb.toString()
        return uriList
    }

    private fun readImages():List<Uri>{
        val uriList = mutableListOf<Uri>()
        sb.setLength(0)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri,null,null,null,null)
        cursor?.let {
            while (cursor.moveToNext()){
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                sb.append("$name, ")
                val contentUri:Uri = ContentUris.withAppendedId(uri,id)
                uriList.add(contentUri)
            }
            cursor.close()
        }
        binding.text.text = sb.toString()
        Log.e("image",sb.toString())
        return uriList
    }

    private fun queryImageByName(name: String): List<Bitmap> {
        val result = mutableListOf<Bitmap>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        // 正确使用LIKE关键字的通配符%
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$name%")

        // 查询
        contentResolver.query(uri, null, selection, selectionArgs, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)

            // 遍历所有匹配项
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                // 修正：使用正确的imageUri来加载图片
                val bitmap = contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
                bitmap?.let {
                    result.add(it)
                }
            }
        }

        Log.e("图片数量", result.size.toString())
        return result
    }

}