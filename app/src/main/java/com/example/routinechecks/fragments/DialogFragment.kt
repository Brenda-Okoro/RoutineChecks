package com.example.routinechecks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.routinechecks.R
import com.example.routinechecks.dagger.Injectable

class DialogFragment : androidx.fragment.app.Fragment(), Injectable {

    //2
    companion object {

        fun newInstance(): DialogFragment {
            return DialogFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_add_routine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}