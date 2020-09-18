package com.Saris.lottery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.recycle_web.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.browse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

data class Lotto(val returnValue : String, val drwtNo1 : Int, val drwtNo2 : Int,val drwtNo3 : Int,val drwtNo4 : Int,val drwtNo5 : Int,val drwtNo6 : Int, val bnusNo : Int,
                 val drwNoDate : String, val firstWinamnt : String, val firstPrzwnerCo : Int, var playCount : Int)

class WebActivity : AppCompatActivity() {

    private lateinit var br: BufferedReader
    private lateinit var searchResult: StringBuilder

    var LottoList : ArrayList<Lotto> = ArrayList<Lotto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val ab = supportActionBar
        ab!!.hide()

        /// 광고
        adView_web.loadAd(AdRequest.Builder().build())

        val latelyRound : Int = LatelyRound()

        /// 최신리스트
        LottoList.clear()
        CoroutineScope(Dispatchers.Main).launch {
            val retVal1 = async(Dispatchers.IO) { getData(latelyRound) }
            val retVal2 = async(Dispatchers.IO) { allRecordfromLotto(latelyRound) }

            val CurrentData = retVal1.await()
            retVal2.await()

            settingCardView(latelyRound,CurrentData)

            /// 리사이클 뷰
            val layoutManager = LinearLayoutManager(this@WebActivity)

            web_recycleView.layoutManager = layoutManager
            web_recycleView.adapter = Myadapter()
        }

        re_image_browse.setOnClickListener {
             browse("https://m.dhlottery.co.kr/gameResult.do?method=byWin&drwNo="+latelyRound)
        }


    }

    fun settingCardView(PlayRound : Int, Data : Lotto) {
        if(Data.returnValue=="success") {
            try {

                val fw = Data.firstWinamnt
                val fwLength = fw.length
                val won_1 = fw.substring(fwLength-4,fwLength)
                val won_2 = fw.substring(fwLength-8,fwLength-4)
                val won_3 = fw.substring(0..fwLength-9)

            txt_round.text = "${PlayRound.toString()}회 로또당첨번호"
            txt_date.text = Data.drwNoDate
            txt_price.text = "1개당 당첨액 : ${won_3},${won_2},${won_1}원"
            txt_price_won.text = "( ${won_3}억 ${won_2}만 ${won_1}원 )"
            txt_people.text = "- 1등 당첨자 :  ${Data.firstPrzwnerCo}명 "
            } catch (e:Exception) {println(e.toString())}
            set_ImageView(Data.drwtNo1,Data.drwtNo2,Data.drwtNo3,Data.drwtNo4,Data.drwtNo5,Data.drwtNo6,Data.bnusNo)
        }
    }

    fun LatelyRound() : Int{

        var sampleDate = "2002-12-07 22:00:00"
        var sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date = sf.parse(sampleDate)
        var today = Calendar.getInstance()
        var calcuDate = ((today.time.time - date.time) / (60 * 60 * 24 * 1000 * 7)) + 1 // 일주일단위

        return calcuDate.toInt()
    }
    fun allRecordfromLotto(values : Int) {
        for(i in (values-1)downTo (values-10)step 1)
        {
            LottoList.add(getData(i))

        }
    }

    fun getData(values : Int) : Lotto {

        try {
            val apiURL =
                "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=" + values;
            val url = URL(apiURL)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.connect()
            val responseCode = con.responseCode
            if (responseCode == 200) {
                br = BufferedReader(InputStreamReader(con.inputStream))
            } else {
                br = BufferedReader(InputStreamReader(con.errorStream))
            }
            searchResult = StringBuilder()
            var inputLine: String?
            do {
                inputLine = br.readLine()
                if (inputLine == null)
                    break
                searchResult.append(inputLine + "\n")
            } while (true)
            br.close()
            con.disconnect()

            var data = searchResult.toString()

            var testModel = Gson().fromJson(data, Lotto::class.java)
            testModel.playCount = values

            Log.d("GourmentSearch", "Result:" + data)

            return testModel
        } catch (e: Exception) {
            Log.d("GourmentSearch", "error: " + e)
            return Lotto("fail",0,0,0,0,0,0,0,"9999-99-99","",0,0)
        }
    }

    fun set_ImageView(no1 : Int,no2 : Int,no3 : Int,no4 : Int,no5 : Int,no6 : Int,no0 : Int) {
        val lottoImageStartId = R.drawable.ball_01

        w_image_ball1.setImageResource(lottoImageStartId + no1 - 1)
        w_image_ball2.setImageResource(lottoImageStartId + no2 - 1)
        w_image_ball3.setImageResource(lottoImageStartId + no3 - 1)
        w_image_ball4.setImageResource(lottoImageStartId + no4 - 1)
        w_image_ball5.setImageResource(lottoImageStartId + no5 - 1)
        w_image_ball6.setImageResource(lottoImageStartId + no6 - 1)
        w_image_ball_bonus.setImageResource(lottoImageStartId + no0 - 1)
    }

    fun set_ImageView(imgView : ImageView, number : Int) {
        val lottoImageStartId = R.drawable.ball_01
        imgView.setImageResource(lottoImageStartId + number-1 )
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val re_txView = itemView.re_textView
        val re_ball_01 = itemView.re_image_ball1
        val re_ball_02 = itemView.re_image_ball2
        val re_ball_03 = itemView.re_image_ball3
        val re_ball_04 = itemView.re_image_ball4
        val re_ball_05 = itemView.re_image_ball5
        val re_ball_06 = itemView.re_image_ball6
        val re_ball_bs = itemView.re_image_ball_bonus
    }

    inner class Myadapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@WebActivity).inflate(R.layout.recycle_web,parent,false))
        }

        override fun getItemCount(): Int {
            return LottoList.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            if (LottoList[position].returnValue == "success") {
                holder.re_txView.text = "${LottoList[position].playCount}회 로또당첨번호"
                set_ImageView(holder.re_ball_01, LottoList[position].drwtNo1)
                set_ImageView(holder.re_ball_02, LottoList[position].drwtNo2)
                set_ImageView(holder.re_ball_03, LottoList[position].drwtNo3)
                set_ImageView(holder.re_ball_04, LottoList[position].drwtNo4)
                set_ImageView(holder.re_ball_05, LottoList[position].drwtNo5)
                set_ImageView(holder.re_ball_06, LottoList[position].drwtNo6)
                set_ImageView(holder.re_ball_bs, LottoList[position].bnusNo)
            }
        }

    }
}