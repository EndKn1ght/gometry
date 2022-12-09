package com.capstone.gometry.ui.play_ar

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.gometry.BuildConfig
import com.capstone.gometry.R
import com.capstone.gometry.adapter.GeometryARAdapter
import com.capstone.gometry.databinding.FragmentPlayArBinding
import com.capstone.gometry.model.GeometryAR
import com.capstone.gometry.utils.HandleIntent.playAR
import com.capstone.gometry.utils.MessageUtility.showToast
import com.capstone.gometry.utils.ViewExtensions.delayOnLifecycle
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException

class PlayARFragment : Fragment() {
    private var _binding: FragmentPlayArBinding? = null
    private val binding get() = _binding
    private var session: Session? = null
    private var supportedAR = false
    private var isCameraAllowToUse = false

    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isCameraAllowToUse = isGranted
        }

    private var startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (ContextCompat.checkSelfPermission(requireContext(), CAMERA) == PackageManager.PERMISSION_GRANTED)
                isCameraAllowToUse = true
        }

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

        checkCameraPermission()
        checkDeviceSupportedAR()
        initialization()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        session?.close()
    }

    private fun initialization() {
        val geometryARAdapter = GeometryARAdapter()
        geometryARAdapter.submitList(listOfGeometryAR)

        binding?.rvGeometryAr?.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = geometryARAdapter
        }

        geometryARAdapter.setOnStartActivityCallback(object: GeometryARAdapter.OnStartActivityCallback {
            override fun onStartActivityCallback(geometryAR: GeometryAR) { handlePlayAR(geometryAR.model3dUrl) }
        })
    }

    private fun handlePlayAR(model3dUrl: String) {
        if (supportedAR) {
            if (isCameraAllowToUse) {
                try {
                    if (session == null) {
                        when (ArCoreApk.getInstance().requestInstall(requireActivity(), true)) {
                            ArCoreApk.InstallStatus.INSTALLED -> {
                                session = Session(requireContext())
                                playAR(requireContext(), model3dUrl)
                                return
                            }
                            ArCoreApk.InstallStatus.INSTALL_REQUESTED -> return
                        }
                    }
                } catch (e: UnavailableUserDeclinedInstallationException) {
                    showToast(requireContext(), e.message.toString())
                    return
                }
            } else showAlertApplicationSetting()
        } else showAlertDeviceUnsupportedAR()
    }

    private fun checkCameraPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(requireContext(), CAMERA) -> isCameraAllowToUse = true
            else -> requestPermissionLauncher.launch(CAMERA)
        }
    }

    private fun showAlertDeviceUnsupportedAR() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.alert_error))
            setMessage(getString(R.string.message_unsupported_device))
            setNegativeButton(getString(R.string.ok)) { _, _ -> }
            create()
            show()
        }
    }

    private fun showAlertApplicationSetting() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.title_setting))
            setMessage(getString(R.string.message_request_camera_permission))
            setPositiveButton(R.string.title_setting) { _, _ ->
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                    it.data = Uri.fromParts("package", requireContext().packageName, null)
                    startForResult.launch(it)
                }
            }
            create()
            show()
        }
    }

    private fun checkDeviceSupportedAR() {
        val availability = ArCoreApk.getInstance().checkAvailability(requireContext())
        if (availability.isTransient) binding?.rvGeometryAr?.delayOnLifecycle(200L) {
            checkDeviceSupportedAR()
        }

        supportedAR = availability.isSupported
    }
}