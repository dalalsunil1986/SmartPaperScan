package com.pengke.paper.scanner.crop

import android.widget.ImageView
import com.pengke.paper.scanner.R
import com.pengke.paper.scanner.base.BaseActivity
import com.pengke.paper.scanner.view.PaperRectangle
import kotlinx.android.synthetic.main.activity_crop.*

class CropActivity : BaseActivity(), ICropView.Proxy {

    private var mPresenter: CropPresenter? = null

    override fun prepare() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        crop.setOnClickListener { mPresenter?.crop() }
        enhance.setOnClickListener { mPresenter?.enhance() }
    }

    override fun provideContentViewId(): Int = R.layout.activity_crop


    override fun initPresenter() {
        mPresenter = CropPresenter(this, this)
    }

    override fun getPaper(): ImageView = paper

    override fun getPaperRect(): PaperRectangle = paper_rect

    override fun getCroppedPaper(): ImageView = picture_cropped
}