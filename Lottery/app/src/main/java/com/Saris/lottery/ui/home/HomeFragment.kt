package com.Saris.lottery.ui.home

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
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
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

var degree : Int= 0
var degree_old : Int = 0


var isSound = false
var isRunning = false
var isSecond = false
var Numcount : Int = 0
var NumList : ArrayList<Int> = ArrayList<Int>()
var ball_imageList : ArrayList<ImageView> = ArrayList<ImageView>()


class __AnimationListener : Animation.AnimationListener {
  private var _onAnimationRepeat: ((animation: Animation?) -> Unit)? = null
  private var _onAnimationEnd: ((animation: Animation?) -> Unit)? = null
  private var _onAnimationStart: ((animation: Animation?) -> Unit)? = null

  override fun onAnimationRepeat(animation: Animation?) {
    _onAnimationRepeat?.invoke(animation)
  }

  fun onAnimationRepeat(func: (animation: Animation?) -> Unit) {
    _onAnimationRepeat = func
  }

  override fun onAnimationEnd(animation: Animation?) {
    _onAnimationEnd?.invoke(animation)
  }

  fun onAnimationEnd(func: (animation: Animation?) -> Unit) {
    _onAnimationEnd = func
  }

  override fun onAnimationStart(animation: Animation?) {
    _onAnimationStart?.invoke(animation)
  }

  fun onAnimationStart(func: (animation: Animation?) -> Unit) {
    _onAnimationStart = func
  }

}


class HomeFragment : Fragment() {

  val realm = Realm.getDefaultInstance()

  val soundSettingKey ="soundSetting"
  val preference by lazy {
    getActivity()?.getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
  }

  private lateinit var homeViewModel: HomeViewModel

  val soundPool = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    SoundPool.Builder().setMaxStreams(3).build()
  } else {
    SoundPool(8, AudioManager.STREAM_MUSIC,0)
  }


  override fun onDestroy() {
    super.onDestroy()
    try {
      soundPool.release()
      realm.close()
    }catch (e:Exception) {}
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    homeViewModel =
    ViewModelProviders.of(this).get(HomeViewModel::class.java)

    Log.d("Start","Roulette Start")
    val root = inflater.inflate(R.layout.fragment_home, container, false)

    isSound = preference!!.getBoolean(soundSettingKey,false)

    var resetCard : CardView = root.findViewById(R.id.r_resetCard)
    var saveCard : CardView = root.findViewById(R.id.r_saveCard)
    var autoCard : CardView = root.findViewById(R.id.r_autoCard)


    var iv_wheelbutton : ImageView = root.findViewById(R.id.r_CenterWheel)
    var iv_wheel : ImageView = root.findViewById(R.id.image_wheel)
    var tx_result : TextView = root.findViewById(R.id.r_result_textView)

    val soundId = soundPool.load(getActivity()?.getApplicationContext(), R.raw.sound_roulette,1)


    ball_imageList.clear()
    var image_ball1 : ImageView = root.findViewById(R.id.image_ball1)
    var image_ball2 : ImageView = root.findViewById(R.id.image_ball2)
    var image_ball3 : ImageView = root.findViewById(R.id.image_ball3)
    var image_ball4 : ImageView = root.findViewById(R.id.image_ball4)
    var image_ball5 : ImageView = root.findViewById(R.id.image_ball5)
    var image_ball6 : ImageView = root.findViewById(R.id.image_ball6)
    ball_imageList.add(image_ball1)
    ball_imageList.add(image_ball2)
    ball_imageList.add(image_ball3)
    ball_imageList.add(image_ball4)
    ball_imageList.add(image_ball5)
    ball_imageList.add(image_ball6)

    reset_ImageView()
    var r = Random()

    tx_result.text = "준비"


    iv_wheel.setImageResource(R.drawable.circle3)
    iv_wheelbutton.setOnClickListener {
      var Num : Int

      if (Numcount < 6) {

        do {
          degree_old = degree % 360
          degree = r.nextInt(3600) + 720
          Num = ceil((364 - (degree % 360)).toDouble() / 8).toInt()
          if(Num>45) {Num = 45}
        }while(NumList.contains(element = Num))

        isSecond = false

        var rotate: RotateAnimation = RotateAnimation(
          degree_old.toFloat(), degree.toFloat(),
          RotateAnimation.RELATIVE_TO_SELF, 0.5f,
          RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )

        var interpol : DecelerateInterpolator = DecelerateInterpolator()
        rotate.setInterpolator(interpol)

        rotate.duration = 1800
        rotate.fillAfter = true
        rotate.interpolator = DecelerateInterpolator()



        val animationListener = object : Animation.AnimationListener {
          override fun onAnimationRepeat(animation: Animation?) {
          }
          override fun onAnimationStart(animation: Animation?) {
            isRunning = true
            if(isSound) {
              soundPool.play(soundId,1.0f,1.0f,0,0,1.0f)
            }
          }
          override fun onAnimationEnd(animation: Animation?) {
            isRunning = false
            if(Numcount < 6) {
              Numcount++
              NumList.add(Num)
              tx_result.text = Num.toString() + "번을 뽑으셨어요!"
              set_ImageView()
            }
          }
        }

        if(isRunning) {
          rotate.duration = 10
          rotate.setAnimationListener(animationListener)
          iv_wheel.startAnimation(rotate)
        }
        else {
          rotate.setAnimationListener(animationListener)
          iv_wheel.startAnimation(rotate)
        }

      }
    }
    resetCard.setOnClickListener {
      tx_result.text = "초기화 하였습니다"
      r_resultCard.visibility = View.GONE
      reset_ImageView()
    }

    autoCard.setOnClickListener {
      auto_SettingNumber(tx_result)
    }

    saveCard.setOnClickListener {
      if(Numcount >= 6) {
        save_Number(tx_result)
        reset_ImageView()
      }
    }

      return root
  }


  fun reset_ImageView()  {
    Numcount = 0
    NumList.clear()
    ball_imageList.forEach {
      it.setImageResource(R.drawable.reset_ball)
    }

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
          if(num <= 0) num=1
        } while (NumList.contains(element = num))
        Numcount++
        NumList.add(num)
        textView.text = "${nokori}개 자동생성"
        set_ImageView()
      }
      Log.d("Auto Result","${NumList[0]} / ${NumList[1]} / ${NumList[2]} / ${NumList[3]} / ${NumList[4]} / ${NumList[5]}")
    }
  }


  fun save_Number(tx_result: TextView) {
    tx_result.text = "저장 하였습니다"

    realm.beginTransaction()

    val newItem = realm.createObject<SaveData>(nextId())

    var date = Date()
    var sf = SimpleDateFormat("yyyy-MM-dd HH:mm")
    var tmp = sf.format(date)

    newItem.saveTitle = "돌려돌려"
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

  fun set_ImageView() {
    val lottoImageStartId = R.drawable.ball_01

    NumList.sort()

    for(i in 0..NumList.size-1) {
      ball_imageList[i].setImageResource(lottoImageStartId + NumList[i]-1)
    }

    if(Numcount == 6) {
      r_resultCard.visibility = View.VISIBLE
      var lst = arrayListOf<TextView>(r_text_Allsum,r_text_HighLow,r_text_OddEven,r_text_Chain,r_text_TenOne,r_text_SumHL,r_text_Interval,r_text_Sum123_456)
      fragment_calculateNumber(lst, NumList)
    }
  }



}