package tw.com.walkablecity.ext

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import tw.com.walkablecity.R
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.factory.ViewModelFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

fun Activity.getVMFactory(): ViewModelFactory {
    val repo = (applicationContext as WalkableApp).repo
    return ViewModelFactory(repo)
}

fun FragmentActivity.shareCacheDirBitmap() {

    try {

        val file = File(WalkableApp.instance.cacheDir, "images")
        val stream = File("${file}/image.png")

        val contentUri = FileProvider.getUriForFile(this, this.packageName + ".provider", stream)

        val shareIntent = Intent(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_STREAM, contentUri)
            .setType("image/*")

        this.startActivity(Intent.createChooser(shareIntent, getString(R.string.share_badge)))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
}