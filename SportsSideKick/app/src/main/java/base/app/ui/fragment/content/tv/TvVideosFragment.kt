package base.app.ui.fragment.content.tv


import base.app.util.ui.BaseFragment

class TvVideosFragment : BaseFragment() {

    /*@BindView(R.id.recyclerView)
    internal var recyclerView: RecyclerView? = null
    @BindView(R.id.caption)
    internal var captionTextView: TextView? = null

    internal var adapter: ClubTVPlaylistAdapter
    internal var playlist: Playlist? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tv, container, false)
        ButterKnife.bind(this, view)
        val playlistId = primaryArgument
        playlist = MediaModel.getPlaylistById(playlistId)
        if (Utility.isTablet(activity)) {
            val layoutManager = GridLayoutManager(context, 3)
            recyclerView!!.layoutManager = layoutManager
            val space = resources.getDimension(R.dimen.padding_8).toInt()
            recyclerView!!.addItemDecoration(GridItemDecoration(space, 3))
        } else {
            val layoutManager = LinearLayoutManager(context)
            recyclerView!!.layoutManager = layoutManager
        }
        adapter = ClubTVPlaylistAdapter(context)
        recyclerView!!.adapter = adapter
        if (captionTextView != null) {
            if (playlist != null) {
                captionTextView!!.text = playlist!!.snippet.title
            }
        }
        // TODO: Make back button visible
        return view
    }

    override fun onResume() {
        super.onResume()
        if (playlist != null) {
            MediaModel.requestPlaylist(playlist!!.id, false)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayPlaylist(event: ClubTVEvent) {
        if (event.eventType == ClubTVEvent.Type.VIDEOS_DOWNLOADED) {
            adapter.values.addAll(MediaModel.getPlaylistsVideos(event.id))
            adapter.notifyDataSetChanged()
        }
    }

    @Optional
    @OnClick(R.id.backButton)
    fun goBack() {
        EventBus.getDefault().post(FragmentEvent(TvPlaylistsFragment::class.java, true))
    }*/
}// Required empty public constructor