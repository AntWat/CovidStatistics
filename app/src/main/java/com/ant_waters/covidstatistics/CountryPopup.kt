package com.ant_waters.covidstatistics


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
//import kotlinx.android.synthetic.main.country_popup.*
import com.ant_waters.covidstatistics.databinding.ActivityCountryPopupBinding
import com.ant_waters.covidstatistics.databinding.ActivityMainBinding


class CountryPopup : AppCompatActivity() {
    private var popupTitle = ""
    private var popupText = ""
    private var popupButton = ""
    private var darkStatusBar = false

    private lateinit var binding: ActivityCountryPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)

        binding = ActivityCountryPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_country_popup)

        // Close the Popup Window when you press the button
        binding.countryPopupButton.setOnClickListener {
            onBackPressed()
        }

        // Get the data
        val bundle = intent.extras
        popupTitle = bundle?.getString("popuptitle", "Title") ?: ""
        popupText = bundle?.getString("popuptext", "Text") ?: ""
        popupButton = bundle?.getString("popupbtn", "Button") ?: ""
        darkStatusBar = bundle?.getBoolean("darkstatusbar", false) ?: false

        // Set the data
        binding.countryPopupTitle.text = popupTitle
        binding.countryPopupText.text = popupText
        binding.countryPopupButton.text = popupButton

        // Set the Status bar appearance for different API levels
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // If you want dark status bar, set darkStatusBar to true
                if (darkStatusBar) {
                    this.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                this.window.statusBarColor = Color.TRANSPARENT
                setWindowFlag(this, false)
            }
        }

        binding.countryPopupBackground.setBackgroundColor(Color.TRANSPARENT)
//        // Fade animation for the background of Popup Window
//        val alpha = 100 //between 0-255
//        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
//        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
//        colorAnimation.duration = 500 // milliseconds
//        colorAnimation.addUpdateListener { animator ->
//            binding.countryPopupBackground.setBackgroundColor(animator.animatedValue as Int)
//        }
//        colorAnimation.start()

//        // Fade animation for the Popup Window
//        binding.countryPopupViewWithBorder.alpha = 0f
//        binding.countryPopupViewWithBorder.animate().alpha(1f).setDuration(500).setInterpolator(
//            DecelerateInterpolator()
//        ).start()
    }

    fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
    }

    override fun onBackPressed() {
//        // Fade animation for the background of Popup Window when you press the back button
//        val alpha = 100 // between 0-255
//        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
//        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
//        colorAnimation.duration = 500 // milliseconds
//        colorAnimation.addUpdateListener { animator ->
//            binding.countryPopupBackground.setBackgroundColor(
//                animator.animatedValue as Int
//            )
//        }
//
//        // Fade animation for the Popup Window when you press the back button
//        binding.countryPopupViewWithBorder.animate().alpha(0f).setDuration(500).setInterpolator(
//            DecelerateInterpolator()
//        ).start()
//
//        // After animation finish, close the Activity
//        colorAnimation.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                finish()
//                overridePendingTransition(0, 0)
//            }
//        })
//        colorAnimation.start()

        finish()
    }
}