package com.pengke.paper.scanner.crop

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import com.pengke.paper.scanner.SourceManager
import com.pengke.paper.scanner.processor.Corners
import com.pengke.paper.scanner.processor.TAG
import com.pengke.paper.scanner.processor.cropPicture
import com.pengke.paper.scanner.processor.enhancePicture
import org.opencv.android.Utils

import org.opencv.core.Mat
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class CropPresenter {
    private val context: Context
    private val iCropView: ICropView.Proxy
    private val picture: Mat? = SourceManager.pic
    private val corners: Corners? = SourceManager.corners
    private var croppedPicture: Mat? = null
    private var enhancedPicture: Bitmap? = null
    private var croppedBitmap: Bitmap? = null

    constructor(context: Context, iCropView: ICropView.Proxy) {
        this.context = context
        this.iCropView = iCropView
        iCropView.getPaperRect().onCorners2Crop(corners, picture?.size())
        val bitmap = Bitmap.createBitmap(picture?.width() ?: 1080, picture?.height() ?: 1920, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(picture, bitmap, true)
        iCropView.getPaper().setImageBitmap(bitmap)
    }

    fun crop() {
        if (picture == null) {
            Log.i(TAG, "picture null?")
            return
        }
        Observable.create(Observable.OnSubscribe { t: Subscriber<in Mat>? ->
            t?.onNext(cropPicture(picture, iCropView.getPaperRect().getCorners2Crop()))
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pc ->
                    kotlin.run {
                        Log.i(TAG, "cropped picture: " + pc.toString())
                        croppedPicture = pc
                        croppedBitmap = Bitmap.createBitmap(pc.width(), pc.height(), Bitmap.Config.ARGB_8888)
                        Utils.matToBitmap(pc, croppedBitmap)
                        iCropView.getCroppedPaper().setImageBitmap(croppedBitmap)
                        iCropView.getPaper().visibility = View.GONE
                        iCropView.getPaperRect().visibility = View.GONE
                    }
                }
    }

    fun enhance() {
        if (croppedBitmap == null) {
            Log.i(TAG, "picture null?")
            return
        }
        Observable.create(Observable.OnSubscribe { t: Subscriber<in Bitmap>? ->
            t?.onNext(enhancePicture(croppedBitmap))
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pc ->
                    kotlin.run {
                        enhancedPicture = pc
                        iCropView.getCroppedPaper().setImageBitmap(pc)
                    }
                }
    }

    fun save() {

    }
}