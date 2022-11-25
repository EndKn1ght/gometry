package com.capstone.gometry.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.R
import com.capstone.gometry.adapter.GeometryAdapter
import com.capstone.gometry.databinding.FragmentHomeBinding
import com.capstone.gometry.model.Geometry
import com.capstone.gometry.ui.detail.DetailActivity
import com.capstone.gometry.ui.detail.DetailActivity.Companion.EXTRA_DETAIL

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val geometryAdapter = GeometryAdapter()
        geometryAdapter.submitList(listOfGeometry)

        val recyclerView = binding?.rvGeometry
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = geometryAdapter
        }

        geometryAdapter.setOnStartActivityCallback(object: GeometryAdapter.OnStartActivityCallback {
            override fun onStartActivityCallback(geometry: Geometry) {
                Intent(requireContext(), DetailActivity::class.java).also { intent ->
                    intent.putExtra(EXTRA_DETAIL, geometry)
                    startActivity(intent)
                }
            }
        })
    }

    private val listOfGeometry: ArrayList<Geometry>
        get() {
            val dataName = resources.getStringArray(R.array.name)
            val dataColorScheme = resources.getStringArray(R.array.color_scheme)
            val dataPreview = resources.obtainTypedArray(R.array.preview)

            val listOfGeometry = ArrayList<Geometry>()
            for (i in dataName.indices) {
                val geometry = Geometry(
                    i.toString(),
                    dataName[i],
                    dataColorScheme[i],
                    dataPreview.getResourceId(i, -1)
                )
                listOfGeometry.add(geometry)
            }
            dataPreview.recycle()

            return listOfGeometry
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}