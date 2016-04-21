package com.arnaudpiroelle.conference.ui.sessions.details

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import com.arnaudpiroelle.conference.ConferenceApplication.Companion.GRAPH
import com.arnaudpiroelle.conference.R
import com.arnaudpiroelle.conference.core.database.dao.SessionDao
import com.arnaudpiroelle.conference.core.database.dao.SpeakerDao
import com.arnaudpiroelle.conference.core.database.dao.TagDao
import com.arnaudpiroelle.conference.core.utils.Intents
import com.arnaudpiroelle.conference.core.utils.Intents.createSpeakerDetails
import com.arnaudpiroelle.conference.core.utils.ProtocolConstants
import com.arnaudpiroelle.conference.model.Session
import com.arnaudpiroelle.conference.model.Speaker
import com.arnaudpiroelle.conference.model.Tag
import com.arnaudpiroelle.conference.ui.core.BaseActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_session_details.*
import kotlinx.android.synthetic.main.item_view_session_speaker.view.*
import javax.inject.Inject

class SessionDetailsActivity : BaseActivity(), SessionDetailsContract.View {

    @Inject lateinit var sessionDao: SessionDao
    @Inject lateinit var speakerDao: SpeakerDao
    @Inject lateinit var tagDao: TagDao

    val sessionId: String by lazy { intent.getStringExtra(ProtocolConstants.EXTRA_SESSION_ID) }
    val userActionsListener: SessionDetailsContract.UserActionsListener  by lazy { SessionDetailsPresenter(this, sessionDao, speakerDao, tagDao) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_session_details)

        GRAPH.inject(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onStart() {
        super.onStart()

        userActionsListener.subscribe(sessionId)
    }

    override fun onStop() {
        super.onStop()

        userActionsListener.unsubscribe()
    }

    override fun showSession(session: Session) {
        session_title.text = session.title
        session_description.text = session.description

        Picasso.with(this).load(session.photoUrl).into(session_image)
    }

    override fun cleanTags() {
        session_tags.removeAllViews()
    }

    override fun addTag(tag: Tag) {

    }

    override fun cleanSpeakers() {
        session_speakers.removeAllViews()
    }

    override fun addSpeaker(speaker: Speaker) {
        val speakerView = LayoutInflater.from(this).inflate(R.layout.item_view_session_speaker, session_speakers, false)

        speakerView.speaker_name.text = speaker.name
        speakerView.speaker_company.text = speaker.company

        if (!TextUtils.isEmpty(speaker.thumbnailUrl)) {
            Picasso.with(this)
                    .load(speaker.thumbnailUrl)
                    .placeholder(R.drawable.placeholder_speaker_rounded)
                    .error(R.drawable.placeholder_speaker_rounded)
                    .into(speakerView.speaker_image)
        }

        speakerView.setOnClickListener {
            startActivity(createSpeakerDetails(this, speaker))
        }

        session_speakers.addView(speakerView)
    }

}