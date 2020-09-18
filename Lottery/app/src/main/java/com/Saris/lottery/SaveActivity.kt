package com.Saris.lottery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import io.realm.*
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_save.*
import kotlinx.android.synthetic.main.recycle_save.*
import kotlinx.android.synthetic.main.recycle_save.view.*


open class SaveData(
    @PrimaryKey var id : Long = 0 , var saveTitle : String = "", var saveNo1 : Int = 0 ,
    var saveNo2 : Int = 0, var saveNo3 : Int = 0, var saveNo4 : Int = 0, var saveNo5 : Int = 0, var saveNo6 : Int = 0, var saveDate : String = "") : RealmObject() {

}


class SaveActivity : AppCompatActivity() {


    private lateinit var mInterstitialAd: InterstitialAd
    val realm = Realm.getDefaultInstance()

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)

        val ab = supportActionBar
        ab!!.hide()

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-6656436186511889/6519970537"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        val realmResult = realm.where<SaveData>()
            .findAll()
            .sort("saveDate",Sort.ASCENDING)

        val layoutManager = LinearLayoutManager(this@SaveActivity)

        val adapter = Myadapter(realmResult)
        save_recycleView.layoutManager = layoutManager
        adapter.notifyDataSetChanged()
        save_recycleView.adapter = adapter


        realmResult.addChangeListener { _ -> adapter.notifyDataSetChanged() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }
    fun deleteData(id : Long) {
        realm.beginTransaction()
        val deleteItem = realm.where<SaveData>().equalTo("id",id).findFirst()!!

        deleteItem.deleteFromRealm()
        realm.commitTransaction()
    }

    fun set_ImageView(imgView : ImageView, number : Int) {
        val lottoImageStartId = R.drawable.ball_01
        imgView.setImageResource(lottoImageStartId + number-1 )
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sa_txView_name = itemView.sa_textView_name
        val sa_txView_date = itemView.sa_textView_date
        val sa_ball_01 = itemView.sa_image_ball_1
        val sa_ball_02 = itemView.sa_image_ball_2
        val sa_ball_03 = itemView.sa_image_ball_3
        val sa_ball_04 = itemView.sa_image_ball_4
        val sa_ball_05 = itemView.sa_image_ball_5
        val sa_ball_06 = itemView.sa_image_ball_6
        val sa_logo = itemView.sa_image_logo
        val sa_trash = itemView.sa_image_trash


        // Expandable RecyclerView
        val sa_text_HighLow = itemView.sa_text_HighLow
        val sa_text_OddEven = itemView.sa_text_OddEven
        val sa_text_Chain = itemView.sa_text_Chain
        val sa_text_TenOne = itemView.sa_text_TenOne
        val sa_text_SumHL = itemView.sa_text_SumHL
        val sa_text_Interval = itemView.sa_text_Interval
        val sa_text_Sum123_456 = itemView.sa_text_Sum123_456
        val sa_text_Allsum = itemView.sa_text_Allsum

    }

    inner class Myadapter(var dataList: List<SaveData>) : RecyclerView.Adapter<MyViewHolder>() {

        private val expandedPositionSet: HashSet<Int> = HashSet()


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@SaveActivity).inflate(R.layout.recycle_save,parent,false))
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]
            holder.sa_txView_name.text = data.saveTitle
            holder.sa_txView_date.text = data.saveDate

            if (data.saveTitle == "돌려돌려") {
                holder.sa_logo.setImageResource(R.drawable.casino)
            } else if (data.saveTitle == "뽑아뽑아") {
                holder.sa_logo.setImageResource(R.drawable.touch)
            } else {
                holder.sa_logo.setImageResource(R.drawable.call)
            }

            set_ImageView(holder.sa_ball_01, data.saveNo1)
            set_ImageView(holder.sa_ball_02, data.saveNo2)
            set_ImageView(holder.sa_ball_03, data.saveNo3)
            set_ImageView(holder.sa_ball_04, data.saveNo4)
            set_ImageView(holder.sa_ball_05, data.saveNo5)
            set_ImageView(holder.sa_ball_06, data.saveNo6)

            calculateNumber(holder,data)
            holder.sa_trash.setOnClickListener({
                deleteData(data.id)
            })

            // Expand when you click on cell
            holder.itemView.expand_layout.setOnExpandListener(object :
                ExpandableLayout.OnExpandListener {
                override fun onExpand(expanded: Boolean) {
                    if (expandedPositionSet.contains(position)) {
                        expandedPositionSet.remove(position)
                    } else {
                        expandedPositionSet.add(position)
                    }
                }
            })
            holder.itemView.expand_layout.setExpand(expandedPositionSet.contains(position))

        }

        fun calculateNumber(holder: MyViewHolder, wrData : SaveData) {

            var high: Int = 0
            var low: Int = 0
            var even: Int = 0
            var odd: Int = 0
            var max: Int = 0
            var min: Int = 0
            var chain: Int = 0
            var interval: Int = 0
            var intervalSum: Int = 0
            var head: Int = 0
            var tail: Int = 0
            var allSum: Int = 0
            var sum123: Int = 0
            var sum456: Int = 0
            var mok: Int = 0
            var preData: Int = 0

            var numberList =  arrayListOf(wrData.saveNo1,wrData.saveNo2,wrData.saveNo3,wrData.saveNo4,wrData.saveNo5,wrData.saveNo6)
            numberList.sort()

            sum123 =numberList[0] + numberList[1] + numberList[2]
            sum456 = numberList[3] + numberList[4] + numberList[5]


            min = numberList[0]
            max = numberList[5]


            preData = numberList[0]


            numberList.forEach{

                if (it % 2 == 0) even++; else odd++;
                if (it > 22) high++; else low++;

                allSum += it;
                mok = it / 10;
                tail += it % 10;
                head += mok;

                interval = it - preData;
                intervalSum += interval;
                if (interval == 1) chain++;
                preData = it;
            }

            holder.sa_text_Allsum.text = " - 총  합 : ${allSum}"
            holder.sa_text_HighLow.text = " - 저고율 : ${low} : ${high}"
            holder.sa_text_OddEven.text = " - 홀짝율 : ${odd} : ${even}"
            holder.sa_text_Chain.text = " - 연  번 : ${chain}"
            holder.sa_text_TenOne.text = " - 첫수합/끝수합 : ${head} / ${tail}"
            holder.sa_text_SumHL.text = " - 고저합 : ${max+min}"
            holder.sa_text_Interval.text = " - 간격합 : ${intervalSum}"
            holder.sa_text_Sum123_456.text = " - 123합/456합 : ${sum123} / ${sum456}"


        }

    }
}