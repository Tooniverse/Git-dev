package com.Saris.lottery.ui.notifications

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.lang.Exception
import java.util.*
import kotlin.math.roundToInt
import org.jetbrains.anko.alert
import java.text.SimpleDateFormat


class NotificationsFragment : Fragment() {

  val realm = Realm.getDefaultInstance()

  private lateinit var notificationsViewModel: NotificationsViewModel

  var maxPower = 0.0
  var isStart = false
  var isSound = false
  var startTime = 0L
  var DurationTime = 3000
  var Numcount = 0
  var NumList : ArrayList<Int> = ArrayList<Int>()
  var ball_imageList : ArrayList<ImageView> = ArrayList<ImageView>()
  var power_buttonList : ArrayList<Button> = ArrayList<Button>()
  val soundSettingKey ="soundSetting"
  val preference by lazy {
    getActivity()?.getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
  }

  val soundPool = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    SoundPool.Builder().setMaxStreams(3).build()
  } else {
    SoundPool(8, AudioManager.STREAM_MUSIC,0)
  }

  val sensorManager : SensorManager by lazy {
   requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
  }

  val eventListener : SensorEventListener = object : SensorEventListener {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
      event?.let {
        if (event.sensor.type != Sensor.TYPE_LINEAR_ACCELERATION) return@let
        if (Numcount < 6 && isStart) {
          val power = Math.pow(event.values[0].toDouble(), 2.0) + Math.pow(
            event.values[1].toDouble(),
            2.0
          ) + Math.pow(event.values[2].toDouble(), 2.0)

          //if (maxPower < power) maxPower = power
          var timeFromStart = (System.currentTimeMillis() - startTime).toInt()

          maxPower += power
          var sec = (DurationTime-timeFromStart)/1000
          var milisec = (DurationTime-timeFromStart)%1000
          p_result_textView.text = "$sec.$milisec"

          if (timeFromStart > DurationTime) {
            punchPowerTestComplete(maxPower)
            isStart = false
          }
        }
      }
    }

  }

  override fun onStart() {
    super.onStart()
    initGame()
  }

  override fun onDestroy() {
    super.onDestroy()
    try {
      realm.close()
      soundPool.release()
    }catch (e:Exception) {}
  }
  override fun onStop() {
    super.onStop()
    try {
      sensorManager.unregisterListener(eventListener)
    }catch (e:Exception) {}
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    notificationsViewModel =
    ViewModelProviders.of(this).get(NotificationsViewModel::class.java)

    val root = inflater.inflate(R.layout.fragment_notifications, container, false)

    isSound = preference!!.getBoolean(soundSettingKey,false)

    val soundId = soundPool.load(getActivity()?.getApplicationContext(), R.raw.sound_shake,1)

    var resetCard : CardView = root.findViewById(R.id.p_resetCard)
    var saveCard : CardView = root.findViewById(R.id.p_saveCard)
    var autoCard : CardView = root.findViewById(R.id.p_autoCard)


    var tx_result : TextView = root.findViewById(R.id.p_result_textView)
    var image_Start : ImageView = root.findViewById(R.id.p_image_Start)
    var image_Info : ImageView = root.findViewById(R.id.p_image_Info)

    ball_imageList.clear()
    var image_ball1 : ImageView = root.findViewById(R.id.p_image_ball1)
    var image_ball2 : ImageView = root.findViewById(R.id.p_image_ball2)
    var image_ball3 : ImageView = root.findViewById(R.id.p_image_ball3)
    var image_ball4 : ImageView = root.findViewById(R.id.p_image_ball4)
    var image_ball5 : ImageView = root.findViewById(R.id.p_image_ball5)
    var image_ball6 : ImageView = root.findViewById(R.id.p_image_ball6)
    ball_imageList.add(image_ball1)
    ball_imageList.add(image_ball2)
    ball_imageList.add(image_ball3)
    ball_imageList.add(image_ball4)
    ball_imageList.add(image_ball5)
    ball_imageList.add(image_ball6)

    var progressButtonImageStartId = R.id.p_progressButton01
    for(i in 0..11) {
      var nextButton : Button = root.findViewById(progressButtonImageStartId+i)
      power_buttonList.add(nextButton)
    }
    reset_Button()


    reset_ImageView()
    tx_result.text = "준비"

    resetCard.setOnClickListener {
      tx_result.text = "초기화 하였습니다"
      reset_ImageView()
      p_resultCard.visibility = View.GONE
      if(Numcount<6) image_Start.visibility = View.VISIBLE
    }

    autoCard.setOnClickListener {
      auto_SettingNumber(tx_result)
    }

    saveCard.setOnClickListener {
      if(Numcount >= 6) {
        save_Number(tx_result)
        reset_ImageView()
        if (Numcount < 6) image_Start.visibility = View.VISIBLE
      }
    }

    image_Start.setOnClickListener {
      startTime = System.currentTimeMillis()
      isStart = true
      it.visibility = View.INVISIBLE
      if(isSound) {
        soundPool.play(soundId,1.0f,1.0f,0,0,1.0f)
      }
    }

    image_Info.setOnClickListener {
      requireActivity().alert("핸드폰의 3초간 핸드폰의 움직임을 측정하여 값을 결정합니다.\n\n 핸드폰을 약하게 움직이면 작은 번호가, 세게 움직이면 높은 번호가 나옵니다","설명") {
        positiveButton("확인") {}
      }.show()
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
          if(num <= 0) num=1
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

    newItem.saveTitle = "흔들흔들"
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

  fun reset_ImageView() {
    Numcount = 0
    NumList.clear()

    ball_imageList.forEach {
      it.setImageResource(R.drawable.reset_ball)
    }
    reset_Button()
  }

  fun reset_Button() {
    power_buttonList.forEach {
      it.visibility = View.INVISIBLE
    }
  }
  fun initGame() {
    maxPower = 0.0
    isStart = false
    startTime = 0L
    if(Numcount<6) p_image_Start.visibility = View.VISIBLE
    sensorManager.registerListener(eventListener,sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL)
  }

  fun punchPowerTestComplete(power : Double) {
    if( Numcount < 6) {
      reset_Button()
      var Num: Int
      Log.d("PunchFragment", "power : ${String.format("%.5f", power)}")
      ///sensorManager.unregisterListener(eventListener)
      Num = (power / 200).roundToInt() + 1
      if (Num >= 45) Num = 45
      p_result_textView.text = Num.toString() + "번을 뽑으셨어요!"

      for (i in 0..(Num / 5)) {
        power_buttonList[i].visibility = View.VISIBLE
      }
      if (!NumList.contains(element = Num)) {
        Numcount++
        NumList.add(Num)
        set_ImageView()
      } else {
        p_result_textView.text = Num.toString() + "번 중복이에요"
      }
      initGame()
    }
  }

  fun set_ImageView() {
    val lottoImageStartId = R.drawable.ball_01

    NumList.sort()

    for(i in 0..NumList.size-1) {
      ball_imageList[i].setImageResource(lottoImageStartId + NumList[i]-1)
    }

    if(Numcount == 6) {
      p_resultCard.visibility = View.VISIBLE
      var lst = arrayListOf<TextView>(p_text_Allsum,p_text_HighLow,p_text_OddEven,p_text_Chain,p_text_TenOne,p_text_SumHL,p_text_Interval,p_text_Sum123_456)
      fragment_calculateNumber(lst, NumList)
    }
  }

}