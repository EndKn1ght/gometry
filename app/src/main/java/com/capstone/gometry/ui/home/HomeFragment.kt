package com.capstone.gometry.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.gometry.BuildConfig
import com.capstone.gometry.R
import com.capstone.gometry.adapter.GeometryAdapter
import com.capstone.gometry.databinding.FragmentHomeBinding
import com.capstone.gometry.model.Geometry
import com.capstone.gometry.model.User
import com.capstone.gometry.ui.detail.DetailActivity
import com.capstone.gometry.ui.detail.DetailActivity.Companion.EXTRA_DETAIL
import com.capstone.gometry.utils.ViewExtensions.setVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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
                Intent(requireContext(), DetailActivity::class.java).also { intent ->
                    intent.putExtra(EXTRA_DETAIL, geometry)
                    launchPostActivity.launch(intent)
                }
            }
        })

        binding?.apply {
            rvGeometry.setVisible(true)
            fakeRvGeometry.removeAllViews()
        }
    }

    private fun getGeometriesWithUserInformation(handleAction: (List<Geometry>) -> Unit) {
        val currentUser = Firebase.auth.currentUser!!
        val database = Firebase.database.getReference("users").child(currentUser.uid)
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
            .addOnFailureListener { Log.e(TAG, it.toString()) }
    }

    private fun generateListOfGeometry(geometries: List<String>? = null): List<Geometry> {
        val mGeometries: ArrayList<Geometry> = arrayListOf()
        val geometryId = resources.getStringArray(R.array.id)
        val geometryName = resources.getStringArray(R.array.name)
        val geometryPreview = resources.obtainTypedArray(R.array.preview)
        val geometryModel3d = resources.getStringArray(R.array.model_3d)

        for (i in geometryId.indices) {
            val geometry = Geometry(
                id = geometryId[i],
                name = geometryName[i],
                preview = geometryPreview.getResourceId(i, -1),
                model3dUrl = String.format(BuildConfig.BASE_URL_STORAGE, "models%2F${geometryModel3d[i]}"),
                complete = if (geometries == null) false else geometryId[i] in geometries,
            )
            mGeometries.add(geometry)
        }
        geometryPreview.recycle()

        return mGeometries
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "DATA FIREBASE"
    }
}