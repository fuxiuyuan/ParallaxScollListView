package com.fuxy.testscala

import android.app.Activity
import android.widget.Button
import android.view.View

trait ActivityUtil extends Activity {  
  
  def findView[WidgetType](id: Int): WidgetType = {
    findViewById(id).asInstanceOf[WidgetType]
  } 
} 

class ViewWithOnClick(view: View) {
  def onClick(action: View => Any) = {
    view.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) { action(v) }
    })
  }
}

object ActivityUtil extends Activity {
  implicit def addOnClickToViews(view: View) =
    new ViewWithOnClick(view)
}