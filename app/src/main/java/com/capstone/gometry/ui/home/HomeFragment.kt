package com.capstone.gometry.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.BuildConfig
import com.capstone.gometry.R
import com.capstone.gometry.adapter.GeometryAdapter
import com.capstone.gometry.databinding.FragmentHomeBinding
import com.capstone.gometry.model.Geometry
import com.capstone.gometry.model.User
import com.capstone.gometry.ui.detail.DetailActivity
import com.capstone.gometry.utils.Constants.EXTRA_DETAIL
import com.capstone.gometry.utils.Constants.REF_USERS
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private lateinit var geometryAdapter: GeometryAdapter

    private val launchPostActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { getGeometriesWithUserInformation { geometryAdapter.submitList(it) } }

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
        getGeometriesWithUserInformation { initialization(it) }
    }

    private fun initialization(listOfGeometries: List<Geometry>) {
        geometryAdapter = GeometryAdapter()
        geometryAdapter.submitList(listOfGeometries)

        val recyclerView = binding?.rvGeometry
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = geometryAdapter
        }

        geometryAdapter.setOnStartActivityCallback(object: GeometryAdapter.OnStartActivityCallback {
            override fun onStartActivityCallback(geometry: Geometry) {
                if (geometry.locked)
                    showAlertLockedGeometry()
                else
                    Intent(requireContext(), DetailActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_DETAIL, geometry)
                        launchPostActivity.launch(intent)
                    }
            }
        })

        binding?.apply {
            rvGeometry.setVisible(true)
            shimmerLayout.stopShimmer()
            fakeRvGeometry.removeAllViews()
        }
    }

    private fun showAlertLockedGeometry() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.locked))
            setMessage(getString(R.string.message_locked_geometry))
            setNegativeButton(getString(R.string.ok)) { _, _ -> }
            create()
            show()
        }
    }

    private fun getGeometriesWithUserInformation(handleAction: (List<Geometry>) -> Unit) {
        lifecycleScope.launchWhenResumed {
            launch {
                val currentUser = Firebase.auth.currentUser!!
                val database = Firebase.database.getReference(REF_USERS).child(currentUser.uid)
                database.get()
                    .addOnSuccessListener {
                        val user: User?
                        if (it.value == null) {
                            user = User(
                                id = currentUser.uid,
                                displayName = currentUser.displayName,
                                email = currentUser.email,
                                photoUrl = currentUser.photoUrl.toString()
                            )
                            database.setValue(user)
                        } else user = it.getValue(User::class.java)

                        handleAction(generateListOfGeometry(user?.geometries))
                    }
            }
        }
    }

    private fun generateListOfGeometry(geometries: List<String>? = null): List<Geometry> {
        val mGeometries: ArrayList<Geometry> = arrayListOf()
        val geometryId = resources.getStringArray(R.array.geometry_id)
        val geometryName = resources.getStringArray(R.array.geometry_name)
        val geometryPreview = resources.obtainTypedArray(R.array.geometry_preview)
        val geometryImage = resources.obtainTypedArray(R.array.geometry_image)
        val geometryTheory = resources.getStringArray(R.array.geometry_theory)
        val geometrySurfaceArea = resources.obtainTypedArray(R.array.geometry_surface_area)
        val geometryVolume = resources.obtainTypedArray(R.array.geometry_volume)
        val geometryExampleQuestion = resources.getStringArray(R.array.geometry_example_question)
        val geometryExampleAnswer = resources.obtainTypedArray(R.array.geometry_example_answer)
        val geometryModel3d = resources.getStringArray(R.array.geometry_model_3d)

        for (i in geometryId.indices) {
            var locked = true

            if (i == 0) locked = false
            else if (mGeometries[i-1].passed) locked = false

            val geometry = Geometry(
                id = geometryId[i],
                name = geometryName[i],
                preview = geometryPreview.getResourceId(i, -1),
                image = geometryImage.getResourceId(i, -1),
                theory = geometryTheory[i],
                surfaceArea = geometrySurfaceArea.getResourceId(i, -1),
                volume = geometryVolume.getResourceId(i, -1),
                exampleQuestion = geometryExampleQuestion[i],
                exampleAnswer = geometryExampleAnswer.getResourceId(i, -1),
                model3dUrl = String.format(BuildConfig.BASE_URL_STORAGE, "models%2F${geometryModel3d[i]}"),
                passed = if (geometries == null) false else geometryId[i] in geometries,
                locked = locked
            )
            mGeometries.add(geometry)
        }
        geometryPreview.recycle()
        geometryImage.recycle()
        geometrySurfaceArea.recycle()
        geometryVolume.recycle()
        geometryExampleAnswer.recycle()

        return mGeometries
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}