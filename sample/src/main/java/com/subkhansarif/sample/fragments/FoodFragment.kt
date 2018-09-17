package com.subkhansarif.sample.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.subkhansarif.sample.R
import kotlinx.android.synthetic.main.fragment_layout.*

/**
 * Created by subkhansarif on 18/09/18
 **/

class FoodFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        main_label.text = "Food Fragment"
    }
}