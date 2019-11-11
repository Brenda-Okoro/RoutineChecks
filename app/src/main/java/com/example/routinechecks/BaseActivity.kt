package com.example.routinechecks

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.example.routinechecks.dagger.ViewModelFactory
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

abstract class BaseActivity(@LayoutRes private val layoutId: Int) : AppCompatActivity() {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    lateinit var viewModelFactory: ViewModelFactory

    protected abstract val TAG: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }

    internal open fun onBackArrowPressed() {
        onBackPressed()
    }

    @Inject
    fun injectViewModelFactory(viewModelFactory: ViewModelFactory) {
        if (this::viewModelFactory.isInitialized) return
        val viewModelFactoryForTesting = viewModelFactoryForTesting
        if (viewModelFactoryForTesting != null) {
            this.viewModelFactory = viewModelFactoryForTesting
        } else {
            this.viewModelFactory = viewModelFactory
        }
    }

    //override fun supportFragmentInjector() = dispatchingAndroidInjector

    /**
     * Get [ViewModel] of requested type
     *
     * NOTE: The [ViewModel] must be handled by [viewModelFactory] (i.e. listed in
     * [ViewModelModule]) to be used by this property delegate.
     */
    inline fun <reified T : ViewModel> lazyViewModel(): Lazy<T> {
        return lazy { ViewModelProviders.of(this, viewModelFactory).get(T::class.java) }
    }

    companion object {
        /**
         * Should only ever be used for tests, by setting this you're overriding the factory used
         * for creating view models. This is useful to be able to mock the models, but shouldn't be
         * used anywhere else.
         */
        @VisibleForTesting()
        var viewModelFactoryForTesting: ViewModelFactory? = null
    }

    protected fun createAlertDialog(message: String): AlertDialog.Builder {
        return AlertDialog.Builder(this)
            .setMessage(message)
    }
}
