package base.app.ui.fragment.stream

import android.os.Bundle
import android.view.View
import base.app.R
import base.app.ui.adapter.stream.ClubRadioAdapter
import base.app.ui.fragment.base.IgnoreBackHandling
import base.app.util.ui.BaseFragment
import base.app.util.ui.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_club_radio.*

@IgnoreBackHandling
class RadioFragment : BaseFragment(R.layout.fragment_club_radio) {

    override fun onViewCreated(view: View, state: Bundle?) {
        recyclerView.addItemDecoration(GridItemDecoration(12, 2))

        val adapter = ClubRadioAdapter(context)
        recyclerView.adapter = adapter

        /* TODO Alex Sheiko
        val getStationsTask = MediaModel.getStations()
        getStationsTask.addOnCompleteListener(OnCompleteListener<List<Station>> { task ->
            if (task.isSuccessful) {
                adapter.values.addAll(task.result)
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                if (Utility.isPhone(activity) && task.result.size > 0) {
                    val fragmentEvent = FragmentEvent(RadioStationFragment::class.java)
                    fragmentEvent.id = task.result[0].name
                    EventBus.getDefault().post(fragmentEvent)
                }
            }
        })
        */
    }
}