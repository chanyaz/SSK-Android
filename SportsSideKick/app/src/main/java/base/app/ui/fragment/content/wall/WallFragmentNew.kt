package base.app.ui.fragment.content.wall

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.app.R
import base.app.ui.adapter.content.WallAdapter
import base.app.util.ui.inflate
import base.app.util.ui.show
import kotlinx.android.synthetic.main.fragment_wall.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class WallFragmentNew : Fragment() {

    private lateinit var viewModel: WallViewModel
    val adapter by lazy { WallAdapter(context) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return container.inflate(R.layout.fragment_wall)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        headerImage.show(R.drawable.header_background)
        setClickListeners()

        recyclerView.adapter = adapter
        viewModel = ViewModelProviders.of(this).get(WallViewModel::class.java)
        viewModel.getItems().observe(this, Observer {
            adapter.clear()
            adapter.addAll(it)
        })
    }

    private fun setClickListeners() {
        postButton.onClick { viewModel.onPostClicked() }
    }
}