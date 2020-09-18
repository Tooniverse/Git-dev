package com.saris.drinkarrow

import android.app.Dialog
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_spin.*
import java.lang.Exception
import java.util.*

class SpinActivity : AppCompatActivity() {

    var degree : Int= 0
    var degree_old : Int = 0
    var isRunning = false
    var isSoundPlaying = false
    var rotate_Duration_rate : Int = 3

    private lateinit var mInterstitialAd: InterstitialAd
    var mediaPlayer: MediaPlayer? = null


    val soundPool = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().build()
    } else {
        SoundPool(8, AudioManager.STREAM_MUSIC,0)
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            soundPool.release()
        }catch (e: Exception) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin)

        val ab = supportActionBar
        ab!!.hide()

        adView.loadAd(AdRequest.Builder().build())

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-6656436186511889/3762456006"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        var sound_FinishId = soundPool.load(this,R.raw.bugle_tune,1)




        var r = Random()

        CardButtonSetting_duration()
        CardButtonSetting_image()
        CardButtonSetting_Background()
        SwitchSetting()

        iv_wheel.setOnClickListener {

                    degree_old = degree % 360
                    degree = r.nextInt(3600) + 720 * rotate_Duration_rate

                var rotate: RotateAnimation = RotateAnimation(
                    degree_old.toFloat(), degree.toFloat(),
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f
                )

                var interpol : DecelerateInterpolator = DecelerateInterpolator()
                rotate.setInterpolator(interpol)

                rotate.duration = (1800 * rotate_Duration_rate).toLong()
                rotate.fillAfter = true
                rotate.interpolator = DecelerateInterpolator()



                val animationListener = object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                    override fun onAnimationStart(animation: Animation?) {
                        isRunning = true
                        if(isSoundPlaying) {
                            stopSound(sound_FinishId)
                            playMedia()
                        }
                    }
                    override fun onAnimationEnd(animation: Animation?) {
                        isRunning = false
                        if(isSoundPlaying) {
                            stopMedia()
                            playSound(sound_FinishId)
                        }
                    }
                }

                if(isRunning) {
                    if(isSoundPlaying) {
                        stopMedia()
                        stopSound(sound_FinishId)
                    }
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
    override fun onBackPressed() {
        super.onBackPressed()
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }

    private fun stopSound(scale : Int) {
        soundPool.stop(scale)
    }

    private fun playSound(scale : Int) {
        soundPool.play(scale,1.0f,1.0f,0,0,1.0f)
    }

    private fun playMedia() {
        mediaPlayer = MediaPlayer.create(this, R.raw.falling_whistle)
        if(!mediaPlayer!!.isPlaying()){
            mediaPlayer?.start()
        }
    }

    private fun stopMedia() {
        mediaPlayer?.stop()
    }

    private fun SwitchSetting() {
        switch_sound.setOnClickListener {
            if(switch_sound.isChecked) {
                isSoundPlaying = true
            }
            else {
                isSoundPlaying = false
            }
        }

    }

    private fun CardButtonSetting_image() {
        // 이미지버튼
        setting_imageCard.setOnClickListener {
            Log.d("Card_image","ImageCardClick Completed")
            val dialog = Dialog(this)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_pointer)

            var image_wheel : ImageView = findViewById(R.id.iv_wheel)

            val image_punch : ImageView = dialog.findViewById(R.id.dialog_image_punch)
            val image_pencil : ImageView = dialog.findViewById(R.id.dialog_image_pencil)
            val image_magic : ImageView = dialog.findViewById(R.id.dialog_image_magic)
            val image_arrow : ImageView = dialog.findViewById(R.id.dialog_image_arrow)
            val image_flower : ImageView = dialog.findViewById(R.id.dialog_image_flower)
            val image_finger : ImageView = dialog.findViewById(R.id.dialog_image_finger)
            val image_spoon : ImageView = dialog.findViewById(R.id.dialog_image_spoon)
            val image_mic : ImageView = dialog.findViewById(R.id.dialog_image_mic)
            val image_drink : ImageView = dialog.findViewById(R.id.dialog_image_drink)
            val card_image : CardView = dialog.findViewById(R.id.dialog_card_image)

            image_punch.setOnClickListener {
                image_wheel.setImageResource(R.drawable.punch)
                dialog.dismiss()
            }
            image_pencil.setOnClickListener {
                image_wheel.setImageResource(R.drawable.pencil)
                dialog.dismiss()
            }
            image_magic.setOnClickListener {
                image_wheel.setImageResource(R.drawable.magic)
                dialog.dismiss()
            }
            image_arrow.setOnClickListener {
                image_wheel.setImageResource(R.drawable.arrow)
                dialog.dismiss()
            }
            image_flower.setOnClickListener {
                image_wheel.setImageResource(R.drawable.flower)
                dialog.dismiss()
            }
            image_finger.setOnClickListener {
                image_wheel.setImageResource(R.drawable.finger)
                dialog.dismiss()
            }
            image_spoon.setOnClickListener {
                image_wheel.setImageResource(R.drawable.spoon)
                dialog.dismiss()
            }
            image_mic.setOnClickListener {
                image_wheel.setImageResource(R.drawable.mic)
                dialog.dismiss()
            }
            image_drink.setOnClickListener {
                image_wheel.setImageResource(R.drawable.drink)
                dialog.dismiss()
            }
            card_image.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

    }

    private fun CardButtonSetting_duration() {
                // 속도버튼
        setting_speedCard.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_duration)

            var duration : Int
            val text_display : TextView = findViewById(R.id.display_text_monitor)
            val text_duration : TextView = dialog.findViewById(R.id.dialog_text_duration)
            val card_small : CardView = dialog.findViewById(R.id.dialog_card_small)
            val card_midium : CardView = dialog.findViewById(R.id.dialog_card_midium)
            val card_large : CardView = dialog.findViewById(R.id.dialog_card_large)
            val card_cancel : CardView = dialog.findViewById(R.id.dialog_card_cancel)
            val card_apply : CardView = dialog.findViewById(R.id.dialog_card_apply)
            var dialog_seekbar : SeekBar = dialog.findViewById(R.id.dialog_seekBar)

            duration = rotate_Duration_rate
            text_duration.text = duration.toString()
            dialog_seekbar.progress = duration

            card_small.setOnClickListener {
                duration = 1
                text_duration.text = duration.toString()
            }
            card_midium.setOnClickListener {
                duration = 3
                text_duration.text = duration.toString()
            }
            card_large.setOnClickListener {
                duration = 5
                text_duration.text = duration.toString()
            }
            card_apply.setOnClickListener {
                rotate_Duration_rate = duration
                text_display.text = rotate_Duration_rate.toString()
                dialog.dismiss()
            }
            card_cancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    duration = progress
                    text_duration.text = "${progress}"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })

            dialog.show()

        }


    }

    private fun CardButtonSetting_Background() {
        // 배경버튼
        setting_backCard.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_background)

            var colorValue : Int = 0
            val image_A : ImageView = dialog.findViewById(R.id.dialog_back_image_A)
            val image_B : ImageView = dialog.findViewById(R.id.dialog_back_image_B)
            val image_C : ImageView = dialog.findViewById(R.id.dialog_back_image_C)
            val image_D : ImageView = dialog.findViewById(R.id.dialog_back_image_D)
            val image_E : ImageView = dialog.findViewById(R.id.dialog_back_image_E)
            val image_F : ImageView = dialog.findViewById(R.id.dialog_back_image_F)
            val image_R : ImageView = dialog.findViewById(R.id.dialog_back_image_random)
            val card_random : CardView = dialog.findViewById(R.id.dialog_back_random)
            val card_cancel : CardView = dialog.findViewById(R.id.dialog_back_cancel)

            image_A.setOnClickListener {
                val colorValue = ContextCompat.getColor(this, R.color.image_A)
                layout_main.setBackgroundColor(colorValue)
                dialog.dismiss()
            }
            image_B.setOnClickListener {
                val colorValue = ContextCompat.getColor(this, R.color.image_B)
                layout_main.setBackgroundColor(colorValue)
                dialog.dismiss()
            }
            image_C.setOnClickListener {
                val colorValue = ContextCompat.getColor(this, R.color.image_C)
                layout_main.setBackgroundColor(colorValue)
                dialog.dismiss()
            }
            image_D.setOnClickListener {
                val colorValue = ContextCompat.getColor(this, R.color.image_D)
                layout_main.setBackgroundColor(colorValue)
                dialog.dismiss()
            }
            image_E.setOnClickListener {
                val colorValue = ContextCompat.getColor(this, R.color.image_E)
                layout_main.setBackgroundColor(colorValue)
                dialog.dismiss()
            }
            image_F.setOnClickListener {
                val colorValue = ContextCompat.getColor(this, R.color.image_F)
                layout_main.setBackgroundColor(colorValue)
                dialog.dismiss()
            }
            image_R.setOnClickListener {
                if(colorValue != 0) {
                    layout_main.setBackgroundColor(colorValue)
                    dialog.dismiss()
                }
            }
            card_random.setOnClickListener {
                var rnd = Random()
                colorValue = Color.argb(255,rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256))
                image_R.setBackgroundColor(colorValue)
            }

            card_cancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }


    }

}