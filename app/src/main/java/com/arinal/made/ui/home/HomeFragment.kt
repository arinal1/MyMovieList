package com.arinal.made.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.arinal.made.R
import com.arinal.made.data.model.FilmModel

class HomeFragment : Fragment() {

    private lateinit var homeAdapter: HomeAdapter
    private lateinit var mInterface: HomeInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mInterface = arguments?.getParcelable(INTERFACE)!!
        homeAdapter = HomeAdapter(context!!, arguments?.getParcelableArrayList(DATA)!!, mInterface)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<RecyclerView>(R.id.recyclerView).adapter = homeAdapter
        return view
    }

    companion object {
        private const val DATA = "data"
        private const val INTERFACE = "interface"
        @JvmStatic
        fun newInstance(data: List<FilmModel>, mInterface: HomeInterface): HomeFragment {
            return HomeFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(DATA, ArrayList(data))
                    putParcelable(INTERFACE, mInterface)
                }
            }
        }
    }
}