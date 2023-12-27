package com.android.hikers

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainPage : AppCompatActivity() {
    private val tv_info: TextView by lazy {findViewById(R.id.tv_myinfo)}
    private val tv_more: TextView by lazy {findViewById(R.id.view_more)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        setViewMore(tv_info,tv_more)

    }

    private fun setViewMore(contentTextView: TextView, viewMoreTextView: TextView) {
        contentTextView.post {
            val lineCount = contentTextView.layout.lineCount
            if (lineCount > 0) {
                if (contentTextView.layout.getEllipsisCount(lineCount - 1) > 0) {
                    // 더보기 표시
                    viewMoreTextView.visibility = View.VISIBLE

                    // 더보기 클릭 이벤트
                    viewMoreTextView.setOnClickListener {
                        contentTextView.maxLines = Int.MAX_VALUE
                        viewMoreTextView.visibility = View.GONE
                        tv_info.setMovementMethod(ScrollingMovementMethod())
                    }
                }
            }
        }
    }
}