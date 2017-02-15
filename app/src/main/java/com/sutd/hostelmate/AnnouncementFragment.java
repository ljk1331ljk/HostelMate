package com.sutd.hostelmate;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AnnouncementFragment extends Fragment {

    public RecyclerView mRecyclerView;
    private AnnouncementListAdapter mAnnouncementAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "announcement fragment";
    List<Announcement> announcementList = new ArrayList<>();

    DatabaseReference mDatabase;
    ValueEventListener mValueListener;
    FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mUserDatabase;
    String userBlock;
    String userLevel;

    private OnFragmentInteractionListener mListener;

    public AnnouncementFragment() {
    }

    public static AnnouncementFragment newInstance(String param1, String param2) {
        AnnouncementFragment fragment = new AnnouncementFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("residents").child(u.getUid());

        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userBlock = dataSnapshot.child("block").getValue().toString();
                userLevel = dataSnapshot.child("level").getValue().toString();
                Log.i("SingleValue", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getAnnouncementList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"going to get announcement");
        View rootView;

        rootView = inflater.inflate(R.layout.fragment_announcement, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_announcement_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAnnouncementAdapter = new AnnouncementListAdapter(announcementList);
        mRecyclerView.setAdapter(mAnnouncementAdapter);
        return rootView;
    }

    private void getAnnouncementList(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("announcements");


        Log.d(TAG,"going to get announcement");
        mValueListener = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                announcementList.clear();


                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    if(postSnapshot.child("block").getValue() == null) {
                        announcementList.add(postSnapshot.getValue(Announcement.class));
                    }
                    else if (postSnapshot.child("block").child(userBlock).getValue().equals(true)) {
                        if (postSnapshot.child("level").getValue() == null) {
                            announcementList.add(postSnapshot.getValue(Announcement.class));
                        }
                        else if (postSnapshot.child("level").child(userLevel).getValue().equals(true)) {
                            announcementList.add(postSnapshot.getValue(Announcement.class));
                        }
                    }
                }
                mAnnouncementAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class AnnouncementListAdapter extends RecyclerView.Adapter<AnnouncementListAdapter.ViewHolder> {
        private List<Announcement> announcementList;
        private static final int DURATION = 250;


        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private TextView announcementSubject, announcementMessage, announcementAuthor, announcementTimestamp;
            private LinearLayout announcementCard, announcementExpandedView;

            private ViewHolder(View v) {
                super(v);
                announcementSubject = (TextView) v.findViewById(R.id.announcement_subject);
                announcementMessage = (TextView) v.findViewById(R.id.announcement_message);
                announcementAuthor = (TextView) v.findViewById(R.id.announcement_author);
                announcementTimestamp = (TextView) v.findViewById(R.id.announcement_timestamp);
                announcementCard = (LinearLayout) v.findViewById(R.id.announcement_card);
                announcementExpandedView = (LinearLayout) v.findViewById((R.id.announcement_expandedview));
            }
        }

        private AnnouncementListAdapter(List<Announcement> announcementList) {
            this.announcementList = announcementList;
        }

        @Override
        public AnnouncementListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_announcement, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.announcementSubject.setText(announcementList.get(position).getSubject());
            holder.announcementMessage.setText(announcementList.get(position).getMessage());
            holder.announcementAuthor.setText(announcementList.get(position).getAuthor());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.announcementTimestamp.setText(Html.fromHtml(announcementList.get(position).getTimestamp(),Html.FROM_HTML_MODE_LEGACY));
            }
            else {
                holder.announcementTimestamp.setText(Html.fromHtml(announcementList.get(position).getTimestamp()));
            }

            holder.announcementCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("slideAnimation", "test");
                    if (holder.announcementExpandedView.getVisibility() == View.GONE) {
                        ExpandAndCollapseViewUtil.expand(holder.announcementExpandedView, DURATION);
                        rotate(-180.0f);
//                        Toast.makeText(v.getContext(), holder.announcementSubject.getText() + " " + position, Toast.LENGTH_SHORT).show();
                    } else {
                        ExpandAndCollapseViewUtil.collapse(holder.announcementExpandedView, DURATION);
                        rotate(180.0f);
                    }
                }

                private void rotate(float angle) {
                    Animation animation = new RotateAnimation(0.0f, angle, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setFillAfter(true);
                    animation.setDuration(DURATION);
                }
            });
        }

        @Override
        public int getItemCount() {
                return announcementList.size();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mListener != null) {
            mDatabase.removeEventListener(mValueListener);
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}