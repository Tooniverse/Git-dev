package com.Saris.lottery

import android.widget.TextView

public fun fragment_calculateNumber(holder: ArrayList<TextView>, numberList : ArrayList<Int>) {

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

    holder[0].text = " - 총  합 : ${allSum}"
    holder[1].text = " - 저고율 : ${low} : ${high}"
    holder[2].text = " - 홀짝율 : ${odd} : ${even}"
    holder[3].text = " - 연  번 : ${chain}"
    holder[4].text = " - 첫수합/끝수합 : ${head} / ${tail}"
    holder[5].text = " - 고저합 : ${max+min}"
    holder[6].text = " - 간격합 : ${intervalSum}"
    holder[7].text = " - 123합/456합 : ${sum123} / ${sum456}"


}