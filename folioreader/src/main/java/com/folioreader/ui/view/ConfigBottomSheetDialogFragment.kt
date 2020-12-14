package com.folioreader.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.folioreader.Config
import com.folioreader.Constants
import com.folioreader.R
import com.folioreader.model.event.ReloadDataEvent
import com.folioreader.ui.activity.FolioActivity
import com.folioreader.ui.activity.FolioActivityCallback
import com.folioreader.util.AppUtil
import com.folioreader.util.UiUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.view_config.*
import org.greenrobot.eventbus.EventBus

class ConfigBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        @JvmField
        val LOG_TAG: String = ConfigBottomSheetDialogFragment::class.java.simpleName
    }

    private lateinit var config: Config
    private lateinit var activityCallback: FolioActivityCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_config, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is FolioActivity)
            activityCallback = activity as FolioActivity

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }

        config = AppUtil.getSavedConfig(activity)!!
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.viewTreeObserver?.addOnGlobalLayoutListener(null)
    }

    private fun initViews() {
        inflateView()
        configFonts()
        view_config_font_size_seek_bar.progress = config.fontSize
        configSeekBar()
        selectFont(config.font, false)
        container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
    }

    private fun inflateView() {

        if (config.allowedDirection != Config.AllowedDirection.VERTICAL_AND_HORIZONTAL) {
            view5.visibility = View.GONE
            buttonVertical.visibility = View.GONE
            buttonHorizontal.visibility = View.GONE
        }

        if (activityCallback.direction == Config.Direction.HORIZONTAL) {
            buttonHorizontal.isSelected = true
        } else if (activityCallback.direction == Config.Direction.VERTICAL) {
            buttonVertical.isSelected = true
        }

        buttonVertical.setOnClickListener {
            config = AppUtil.getSavedConfig(context)!!
            config.direction = Config.Direction.VERTICAL
            AppUtil.saveConfig(context, config)
            activityCallback.onDirectionChange(Config.Direction.VERTICAL)
            buttonHorizontal.isSelected = false
            buttonVertical.isSelected = true
        }

        buttonHorizontal.setOnClickListener {
            config = AppUtil.getSavedConfig(context)!!
            config.direction = Config.Direction.HORIZONTAL
            AppUtil.saveConfig(context, config)
            activityCallback.onDirectionChange(Config.Direction.HORIZONTAL)
            buttonHorizontal.isSelected = true
            buttonVertical.isSelected = false
        }
    }

    private fun configFonts() {

        val colorStateList = UiUtil.getColorList(
            config.themeColor,
            ContextCompat.getColor(context!!, R.color.grey_color)
        )
        buttonVertical.setTextColor(colorStateList)
        buttonHorizontal.setTextColor(colorStateList)
        view_config_font_andada.setTextColor(colorStateList)
        view_config_font_lato.setTextColor(colorStateList)
        view_config_font_lora.setTextColor(colorStateList)
        view_config_font_raleway.setTextColor(colorStateList)

        view_config_font_andada.setOnClickListener { selectFont(Constants.FONT_ANDADA, true) }
        view_config_font_lato.setOnClickListener { selectFont(Constants.FONT_LATO, true) }
        view_config_font_lora.setOnClickListener { selectFont(Constants.FONT_LORA, true) }
        view_config_font_raleway.setOnClickListener { selectFont(Constants.FONT_RALEWAY, true) }
    }

    private fun selectFont(selectedFont: Int, isReloadNeeded: Boolean) {
        when (selectedFont) {
            Constants.FONT_ANDADA -> setSelectedFont(true, false, false, false)
            Constants.FONT_LATO -> setSelectedFont(false, true, false, false)
            Constants.FONT_LORA -> setSelectedFont(false, false, true, false)
            Constants.FONT_RALEWAY -> setSelectedFont(false, false, false, true)
        }
        config.font = selectedFont
        if (isAdded && isReloadNeeded) {
            AppUtil.saveConfig(activity, config)
            EventBus.getDefault().post(ReloadDataEvent())
        }
    }

    private fun setSelectedFont(andada: Boolean, lato: Boolean, lora: Boolean, raleway: Boolean) {
        view_config_font_andada.isSelected = andada
        view_config_font_lato.isSelected = lato
        view_config_font_lora.isSelected = lora
        view_config_font_raleway.isSelected = raleway
    }

    private fun configSeekBar() {
        val thumbDrawable = ContextCompat.getDrawable(activity!!, R.drawable.thumb)
        UiUtil.setColorIntToDrawable(config.themeColor, thumbDrawable)
        UiUtil.setColorResToDrawable(R.color.grey_color, view_config_font_size_seek_bar.progressDrawable)
        view_config_font_size_seek_bar.thumb = thumbDrawable

        view_config_font_size_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                config.fontSize = progress
                AppUtil.saveConfig(activity, config)
                EventBus.getDefault().post(ReloadDataEvent())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}
