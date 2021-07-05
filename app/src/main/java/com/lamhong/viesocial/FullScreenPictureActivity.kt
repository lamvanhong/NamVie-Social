package com.lamhong.viesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_screen_picture.*
import uk.co.senab.photoview.PhotoViewAttacher

class FullScreenPictureActivity : AppCompatActivity() {
    private var imageuri :String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_picture)

        imageuri= intent.getStringExtra("imageuri").toString()

        Picasso.get().load(imageuri).into(image_full)
        btn_finish_full.setOnClickListener{
            finish()
        }
        val attacher = PhotoViewAttacher(image_full)
        attacher.update()
    }
}