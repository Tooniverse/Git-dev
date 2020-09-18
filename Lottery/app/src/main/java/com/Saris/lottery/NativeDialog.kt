package com.Saris.lottery

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.dialog_close.*


class MyDialog(context : Context) : Dialog(context){
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감
    private lateinit var lblDesc : TextView
    private lateinit var btnOK : Button
    private lateinit var btnCancel : Button
    //private lateinit var adView_dia : AdView

    fun start(content : String) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.dialog_close)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        lblDesc = dlg.findViewById(R.id.di_text_title)
        lblDesc.text = content

        btnOK = dlg.findViewById(R.id.di_btn_yes)
        btnOK.setOnClickListener {
            android.os.Process.killProcess(android.os.Process.myPid())
            dlg.dismiss()
        }

        btnCancel = dlg.findViewById(R.id.di_btn_no)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }
}