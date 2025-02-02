package com.example.util.simpletimetracker.core.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

abstract class BaseFragment<T : ViewBinding> : Fragment(), Throttler {

    abstract val inflater: (LayoutInflater, ViewGroup?, Boolean) -> T
    protected val binding: T get() = _binding!!
    private var _binding: T? = null
    private var preDrawListeners: MutableList<OnPreDrawListener> = mutableListOf()
    override var throttleJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflater(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUi()
        initUx()
        initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        preDrawListeners.forEach { view?.viewTreeObserver?.removeOnPreDrawListener(it) }
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        childFragmentManager.fragments.forEach {
            it.onActivityResult(requestCode, resultCode, data)
        }
    }

    open fun initUi() {
        // Override in subclasses
    }

    open fun initUx() {
        // Override in subclasses
    }

    open fun initViewModel() {
        // Override in subclasses
    }

    fun setOnPreDrawListener(block: () -> Unit) {
        val listener = object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view?.viewTreeObserver?.removeOnPreDrawListener(this)
                block()
                return true
            }
        }
        preDrawListeners.add(listener)
        view?.viewTreeObserver?.addOnPreDrawListener(listener)
    }

    inline fun <T> LiveData<T>.observe(
        crossinline onChanged: (T) -> Unit
    ) {
        observe(viewLifecycleOwner) { onChanged(it) }
    }
}