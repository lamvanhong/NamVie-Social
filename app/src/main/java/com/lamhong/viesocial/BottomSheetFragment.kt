package com.lamhong.viesocial

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.sheet_layout.view.*

class BottomSheetFragment(private var mcontext : Context, private var type: String, private var id: String): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.sheet_layout, container, false)
        view.btn_savePost.setOnClickListener{
            Toast.makeText(mcontext.applicationContext, "Đã lưu bài viết" , Toast.LENGTH_LONG).show()
            this.dismiss()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}