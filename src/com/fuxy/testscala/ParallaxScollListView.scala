package com.fuxy.testscala

import android.widget.ListView
import android.util.AttributeSet
import android.content.Context
import android.widget.ImageView
import android.widget.AbsListView
import android.view.View
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.Transformation
import android.util.Log

object ParallaxScollListView {
  val NO_ZOOM = 1.0
  val ZOOM_X2 = 2.0
}

class ParallaxScollListView(context: Context, attrs: AttributeSet) extends ListView(context, attrs) with AbsListView.OnScrollListener {

  trait OnOverScrollByListener {
    def overScrollBy(deltaX: Int, deltaY: Int, scrollX: Int,
      scrollY: Int, scrollRangeX: Int, scrollRangeY: Int,
      maxOverScrollX: Int, maxOverScrollY: Int, isTouchEvent: Boolean): Boolean
  }

  trait OnTouchEventListener {
    def onTouchEvent(ev: MotionEvent)
  }

  init(getContext())

  private var mImageView: ImageView = null
  private var mDrawableMaxHeight = -1
  private var mImageViewHeight = -1
  private var mDefaultImageViewHeight = 320

  //def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)
  def this(context: Context) = this(context, null)

  def init(context: Context) {
    mDefaultImageViewHeight = context.getResources.getDimensionPixelSize(R.dimen.size_default_height);
  }

  override def onScrollStateChanged(view: AbsListView, scrollState: Int) {
  }

  override def overScrollBy(deltaX: Int, deltaY: Int, scrollX: Int,
    scrollY: Int, scrollRangeX: Int, scrollRangeY: Int,
    maxOverScrollX: Int, maxOverScrollY: Int, isTouchEvent: Boolean): Boolean = {
    var isCollapseAnimation = false

    isCollapseAnimation = scrollByListener.overScrollBy(deltaX, deltaY,
      scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
      maxOverScrollY, isTouchEvent) || isCollapseAnimation

    return if (isCollapseAnimation) true else super.overScrollBy(deltaX, deltaY,
      scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
      maxOverScrollY, isTouchEvent)
  }

  override def onScroll(view: AbsListView, firstVisibleItem: Int,
    visibleItemCount: Int, totalItemCount: Int) {
  }

  override def onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
    super.onScrollChanged(l, t, oldl, oldt)
    def firstView = mImageView.getParent.asInstanceOf[View]
    if (firstView.getTop < getPaddingTop && mImageView.getHeight > mImageViewHeight) {
      mImageView.getLayoutParams.height = Math.max(mImageView.getHeight - (getPaddingTop - firstView.getTop), mImageViewHeight)
      firstView.layout(firstView.getLeft, 0, firstView.getRight, firstView.getHeight)
      mImageView.requestLayout
    }
  }

  override def onTouchEvent(ev: MotionEvent): Boolean = {
    touchListener.onTouchEvent(ev)
    super.onTouchEvent(ev)
  }

  def setParallaxImageView(iv: ImageView) {
    mImageView = iv
    mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
  }

  def setViewsBounds(zoomRatio: Double) {
    if (mImageViewHeight == -1) {
      mImageViewHeight = mImageView.getHeight
      if (mImageViewHeight <= 0) {
        mImageViewHeight = mDefaultImageViewHeight
      }
      def ratio = (mImageView.getDrawable.getIntrinsicWidth.toDouble) / (mImageView.getWidth.toDouble)

      mDrawableMaxHeight = ((mImageView.getDrawable.getIntrinsicHeight / ratio) * (if (zoomRatio > 1)
        zoomRatio else 1)).toInt
    }
  }

  def scrollByListener = new OnOverScrollByListener() {

    def overScrollBy(deltaX: Int, deltaY: Int, scrollX: Int,
      scrollY: Int, scrollRangeX: Int, scrollRangeY: Int,
      maxOverScrollX: Int, maxOverScrollY: Int, isTouchEvent: Boolean): Boolean = {
      if (mImageView.getHeight() <= mDrawableMaxHeight && isTouchEvent) {
        if (deltaY < 0) {
          if (mImageView.getHeight - deltaY / 2 >= mImageViewHeight) {
            mImageView.getLayoutParams.height = if (mImageView.getHeight - deltaY / 2 < mDrawableMaxHeight)
              mImageView.getHeight - deltaY / 2 else mDrawableMaxHeight
            mImageView.requestLayout
          }
        } else {
          if (mImageView.getHeight > mImageViewHeight) {
            mImageView.getLayoutParams.height = if (mImageView.getHeight - deltaY > mImageViewHeight)
              mImageView.getHeight - deltaY else mImageViewHeight
            mImageView.requestLayout
            return true;
          }
        }
      }
      return false;
    }
  }

  def touchListener = new OnTouchEventListener() {
    def onTouchEvent(ev: MotionEvent) {
      if (ev.getAction() == MotionEvent.ACTION_UP) {
        if (mImageViewHeight - 1 < mImageView.getHeight) {
          def animation = new ResetAnimimation(
            mImageView, mImageViewHeight)
          animation.setDuration(300)
          mImageView.startAnimation(animation)
        }
      }
    }
  }

  class ResetAnimimation(view: View, height: Int) extends Animation {
    val targetHeight = height
    val originalHeight = view.getHeight
    val extraHeight = this.targetHeight - originalHeight
    val mView = view

    override def applyTransformation(interpolatedTime: Float,
      t: Transformation) = {
      val newHeight = (targetHeight - extraHeight * (1 - interpolatedTime))
      mView.getLayoutParams.height = newHeight.toInt
      mView.requestLayout
    }
  }
}

