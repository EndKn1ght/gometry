package com.capstone.gometry.ui.play_ar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.gometry.BuildConfig
import com.capstone.gometry.R
import com.capstone.gometry.adapter.GeometryARAdapter
import com.capstone.gometry.databinding.FragmentPlayArBinding
import com.capstone.gometry.model.GeometryAR
import com.capstone.gometry.utils.HandleIntent.handlePlayAR

class PlayARFragment : Fragment() {
    private var _binding: FragmentPlayArBinding? = null
    private val binding get() = _binding

    private val listOfGeometryAR: ArrayList<GeometryAR>
        get() {
            val geometryPreview = resources.obtainTypedArray(R.array.geometry_preview)
            val geometryModel3d = resources.getStringArray(R.array.geometry_model_3d)

            val listGeometryAr = ArrayList<GeometryAR>()
            for (i in geometryModel3d.indices) {
                val geometryAR = GeometryAR(
                    geometryPreview.getResourceId(i, -1),
                    String.format(BuildConfig.BASE_URL_STORAGE, "models%2F${geometryModel3d[i]}")
                )
                listGeometryAr.add(geometryAR)
            }
            geometryPreview.recycle()

            return listGeometryAr
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayArBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initialization() {
        val geometryARAdapter = GeometryARAdapter()
        geometryARAdapter.submitList(listOfGeometryAR)

        binding?.rvGeometryAr?.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = geometryARAdapter
        }

        geometryARAdapter.setOnStartActivityCallback(object: GeometryARAdapter.OnStartActivityCallback {
            override fun onStartActivityCallback(geometryAR: GeometryAR) {
                handlePlayAR(requireActivity(), geometryAR.model3dUrl)
            }
        })
    }
}