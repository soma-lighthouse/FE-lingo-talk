package com.lighthouse.android.common_ui.base

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.dialog.showOKDialog
import com.lighthouse.android.common_ui.util.Injector
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.splitPath
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.domain.constriant.ErrorTypeHandling
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.navigation.MainNavigator
import com.lighthouse.navigation.deepLinkNavigateTo
import com.lighthouse.navigation.findClassByPath
import dagger.hilt.android.EntryPointAccessors
import java.lang.ref.WeakReference

abstract class BindingFragment<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int,
) : Fragment() {
    private var _binding: T? = null
    protected val binding: T
        get() = requireNotNull(_binding)

    protected var bindingWeakRef: WeakReference<T>? = null

    protected val getResult = MutableLiveData<Intent>()

    protected val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                getResult.value = result.data
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val dataBinding: T = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        _binding = dataBinding
        bindingWeakRef = WeakReference(dataBinding)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    val mainNavigator: MainNavigator by lazy {
        EntryPointAccessors.fromActivity(
            requireActivity(), Injector.MainNavigatorInjector::class.java
        ).mainNavigator()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        EntryPointAccessors.fromActivity(
            requireActivity(), Injector.SharedPreferencesInjector::class.java
        ).sharedPreferences()
    }

    protected fun handleException(uiState: UiState.Error<*>) {
        val exception = uiState.message
        Log.d("ERROR", uiState.toString())
        if (exception is LighthouseException) {
            when (exception.errorType) {
                ErrorTypeHandling.TOAST -> {
                    Log.d("ERROR", "enter toast")
                    context.toast(exception.message.toString())
                }

                ErrorTypeHandling.DIALOG -> {
                    Log.d("ERROR", "enter dialog")
                    showOKDialog(
                        requireContext(),
                        getString(R.string.error_title),
                        exception.message.toString()
                    )
                }


                ErrorTypeHandling.DIRECT_AND_DIALOG -> {
                    showOKDialog(
                        requireContext(),
                        getString(R.string.error_title),
                        exception.message.toString(),
                        false,
                    ) { d, _ ->
                        d.dismiss()
                        logout()
                    }
                }

                ErrorTypeHandling.NONE -> {
                    // do nothing
                }
            }
        } else {
            context.toast(exception.toString())
        }
    }

    protected fun logout() {
        sharedPreferences.edit().clear().apply()
        mainNavigator.navigateToLogin(requireContext())
        requireActivity().finish()
    }

    protected fun redirectToDestination(base: String, path: String) {
        Log.d("TESTING PUSH0", path)
        val split = path.splitPath()

        Log.d("TESTING PUSH1", split.toString())
        if (split.isEmpty()) return
        Log.d("TESTING PUSH2", "$base/${split.first()}")
        val viewType =
            findClassByPath("$base/${split.first()}", "$base/${split.first()}", split.last())
        findNavController().deepLinkNavigateTo(viewType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
