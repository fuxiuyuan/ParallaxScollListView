package com.fuxy.testscala
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.{ Toast, Button }
import android.content.Context
import ActivityUtil._
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ArrayAdapter
import android.view.Menu

class ScalaMainActivity extends Activity with ActivityUtil {

  //lazy val mListView = findView[ParallaxScollListView](R.id.layout_listview)
  //lazy val header = LayoutInflater.from(this).inflate(R.layout.listview_header, null);
  //lazy val mImageView = header.findViewById(R.id.layout_header_image).asInstanceOf[ImageView];
  var mListView : ParallaxScollListView = null
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_parallax)

    //val mButtom = findView[Button](R.id.submit)
    //    mButtom.onClick { view: View =>
    //      Toast.makeText(this, "scala for android", Toast.LENGTH_SHORT).show()
    //    }

    mListView = findView[ParallaxScollListView](R.id.layout_listview)
    val header = LayoutInflater.from(this).inflate(R.layout.listview_header, null)
    val mImageView = header.findViewById(R.id.layout_header_image).asInstanceOf[ImageView]
    mListView.setParallaxImageView(mImageView)
    mListView.addHeaderView(header)
    val itemList = Array.apply("First Item", "Second Item",
      "Third Item",
      "Fifth Item",
      "Sixth Item",
      "Seventh Item",
      "Eighth Item",
      "Ninth Item",
      "Tenth Item",
      ".....")
    def adapter = new ArrayAdapter[String](this,android.R.layout.simple_expandable_list_item_1,itemList)
    mListView.setAdapter(adapter)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    //getMenuInflater().inflate(R.menu.parallax, menu);
    return true;
  }

  override def onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)

    if (hasFocus) {
      mListView.setViewsBounds(ParallaxScollListView.ZOOM_X2)
    }
  }
  override def toString(): String = "ScalaMainActivity"
}