package com.Saris.lottery.ui.dashboard

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.Saris.lottery.R
import com.Saris.lottery.SaveData
import com.Saris.lottery.fragment_calculateNumber
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

  val realm = Realm.getDefaultInstance()

  private lateinit var dashboardViewModel: DashboardViewModel

  val soundPool = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    SoundPool.Builder().setMaxStreams(3).build()
  } else {
    SoundPool(8, AudioManager.STREAM_MUSIC,0)
  }
  var soundId = 0
  var Numcount = 0
  var NumList : ArrayList<Int> = ArrayList<Int>()
  var ball_imageList : ArrayList<ImageView> = ArrayList<ImageView>()
  var isSound = false
  val soundSettingKey ="soundSetting"
  val preference by lazy {
    getActivity()?.getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
  }

  override fun onDestroy() {
    super.onDestroy()
    try {
      realm.close()
      soundPool.release()
    }catch (e:Exception) {}
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    dashboardViewModel =
    ViewModelProviders.of(this).get(DashboardViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

    soundId = soundPool.load(getActivity()?.getApplicationContext(), R.raw.beep,1)
    isSound = preference!!.getBoolean(soundSettingKey,false)


    var resetCard : CardView = root.findViewById(R.id.c_resetCard)
    var saveCard : CardView = root.findViewById(R.id.c_saveCard)
    var autoCard : CardView = root.findViewById(R.id.c_autoCard)

    var tx_result : TextView = root.findViewById(R.id.c_result_textView)

    ball_imageList.clear()
    var image_ball1 : ImageView = root.findViewById(R.id.c_image_ball1)
    var image_ball2 : ImageView = root.findViewById(R.id.c_image_ball2)
    var image_ball3 : ImageView = root.findViewById(R.id.c_image_ball3)
    var image_ball4 : ImageView = root.findViewById(R.id.c_image_ball4)
    var image_ball5 : ImageView = root.findViewById(R.id.c_image_ball5)
    var image_ball6 : ImageView = root.findViewById(R.id.c_image_ball6)
    ball_imageList.add(image_ball1)
    ball_imageList.add(image_ball2)
    ball_imageList.add(image_ball3)
    ball_imageList.add(image_ball4)
    ball_imageList.add(image_ball5)
    ball_imageList.add(image_ball6)

    reset_ImageView(root, tx_result)
    tx_result.text = "준비"

    resetCard.setOnClickListener {
      tx_result.text = "초기화 하였습니다"
      c_resultCard.visibility = View.GONE
      reset_ImageView(root, tx_result)
    }

    autoCard.setOnClickListener {
      auto_SettingNumber(tx_result)
    }

    saveCard.setOnClickListener {
      if(Numcount >= 6) {
        save_Number(tx_result)
        reset_ImageView(root, tx_result)
      }
    }


    return root
  }


  fun auto_SettingNumber(textView: TextView) {
    if(Numcount < 6) {
      var nokori: Int = 6 - Numcount
      for (i in 0 until nokori) {
        var r = Random()
        var num: Int

        do {
          var RandomInt = r.nextInt(10000)
          num = (RandomInt + 1) % 45
          if(num <= 0) num=45
        } while (NumList.contains(element = num))
        Numcount++
        NumList.add(num)
        textView.text = "${nokori}개 자동생성"
        set_ImageView()
      }
    }
  }

  fun save_Number(tx_result: TextView) {
    if(Numcount < 6) return
    tx_result.text = "저장 하였습니다"

    realm.beginTransaction()

    val newItem = realm.createObject<SaveData>(nextId())

    var date = Date()
    var sf = SimpleDateFormat("yyyy-MM-dd HH:mm")
    var tmp = sf.format(date)

    newItem.saveTitle = "뽑아뽑아"
    newItem.saveDate = tmp
    newItem.saveNo1 = NumList[0]
    newItem.saveNo2 = NumList[1]
    newItem.saveNo3 = NumList[2]
    newItem.saveNo4 = NumList[3]
    newItem.saveNo5 = NumList[4]
    newItem.saveNo6 = NumList[5]

    realm.commitTransaction()
  }

  private fun nextId() : Int {
    val maxId = realm.where<SaveData>().max("id")
    if(maxId != null) {
      return maxId.toInt() + 1
    }
    return  0
  }


  fun reset_ImageView(root : View, tx_result: TextView)  {

    var r = Random()
    var image_ballStart = R.id.c_ball01

    Numcount = 0

    NumList.clear()
    for(i in 0..44) {
      var it = root.findViewById<ImageView>((image_ballStart + i))

      when(r.nextInt(3)){
        0->  it.setImageResource(R.drawable.rand_ball01)
        1->  it.setImageResource(R.drawable.rand_ball02)
        2->  it.setImageResource(R.drawable.rand_ball03)
        else->  it.setImageResource(R.drawable.rand_ball04)
      }
      it.isClickable = true
      it.setOnClickListener {
        var r = it.id
        var it = root.findViewById<ImageView>(r)
        if(Numcount < 6) {
          ChangeImage(it,tx_result)
        }
      }
    }
    ball_imageList.forEach {
      it.setImageResource(R.drawable.reset_ball)
    }
  }

  fun ChangeImage(image : ImageView, tx_result : TextView) {
    var r = Random()
    var num : Int
    do {
      var RandomInt = r.nextInt(10000)
      num = (RandomInt+1) % 45
    }while(NumList.contains(element = num))

    NumList.add(num)

    var image_ballStart = R.drawable.ball_01
    tx_result.text = (num+1).toString() + "번을 뽑으셨어요!"
    if(isSound) {
      soundPool.play(soundId,1.0f,1.0f,0,0,1.0f)
    }

    image.setImageResource(image_ballStart+num)
    Numcount++
    set_ImageView()
    image.isClickable = false

  }

  fun set_ImageView() {
    val lottoImageStartId = R.drawable.ball_01

    NumList.sort()

    for(i in 0..NumList.size-1) {
      ball_imageList[i].setImageResource(lottoImageStartId + NumList[i])
      //imageList[i].setImageResource(research_Image(lottoImageStartId + (NumList[i]-1)))
    }
    if(Numcount == 6) {
      c_resultCard.visibility = View.VISIBLE
      var lst = arrayListOf<TextView>(c_text_Allsum,c_text_HighLow,c_text_OddEven,c_text_Chain,c_text_TenOne,c_text_SumHL,c_text_Interval,c_text_Sum123_456)
      fragment_calculateNumber(lst, NumList)
    }
  }
}