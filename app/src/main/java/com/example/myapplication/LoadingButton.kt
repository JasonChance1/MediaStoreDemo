package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView

/**
 * @description:
 * @author Wandervogel
 * @date :2024/4/2
 * @version 1.0.0
 */
class LoadingButton @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attributeSet, defStyleAttr) {
    private var progressBar: ProgressBar
//    private var loading:LottieAnimationView
    private var textView: TextView
    private var isLoading:Boolean = false
    private var container:MaterialCardView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_loading_button, this, true)
        progressBar = view.findViewById(R.id.progress_bar)
//        loading = view.findViewById(R.id.loading)
        textView = view.findViewById(R.id.textView)
        container = view.findViewById(R.id.container)
        setBackgroundColor(resources.getColor(R.color.transparent,context.theme))
        context.theme.obtainStyledAttributes(attributeSet,R.styleable.LoadingButton,0,0).let {
            try {
                textView.text = it.getString(R.styleable.LoadingButton_android_text)
                textView.setTextColor(it.getColor(R.styleable.LoadingButton_android_textColor,resources.getColor(R.color.white,context.theme)))
                textView.textSize = it.getFloat(R.styleable.LoadingButton_android_textSize,14f)

                backgroundTintList = it.getColorStateList(R.styleable.LoadingButton_backgroundTint)
                progressBar.indeterminateTintList = it.getColorStateList(R.styleable.LoadingButton_android_indeterminateTint)
            }finally {
                it.recycle()
            }
        }

    }

    fun onClick(run:()->Unit){
        container.setOnClickListener { run.invoke() }
    }

    fun startLoading(){
        if(!isLoading){
            isLoading = true
            progressBar.setVisible(true)
            textView.setVisible(false)
//        loading.setVisible(true)
        }


    }

    fun stopLoading(){
        if(isLoading){
            textView.setVisible(true)
//        loading.setVisible(false)
            progressBar.setVisible(false)
            isLoading = false
        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 使用测量后的尺寸来确定视图的大小
        val measuredWidth = measuredWidth
        val measuredHeight = measuredHeight

        // 设置视图的测量尺寸
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

}