package com.example.new_hopes;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SongsFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SongsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recyclerView;
    SongsListRecyclerAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SongsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SongsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static SongsFrag newInstance(String param1, String param2) {
        SongsFrag fragment = new SongsFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ArrayList<String> getPlayLists()
    {
    ArrayList<String> names=new ArrayList<String>();
        try {

        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator +"new_hopes21"+File.separator);
        ArrayList<PlayList> xyz=(ArrayList<PlayList>) CollectSongs.restore_state(file);
        for (PlayList playList : xyz){
            for(Song song: playList.songs){
                names.add(song.name);
            }
        }}catch(Exception e){}
        return names;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_songs, container, false);

        List<SongsListData> list = new ArrayList<>();

        //list.add(new SongsListData("Homicide","Eminem",R.drawable.ic_launcher_background));
        //list.add(new SongsListData("Venom","Eminem",R.drawable.ic_launcher_background));
        //list.add(new SongsListData("Rap God","Eminem",R.drawable.ic_launcher_foreground));



        ArrayList<String> song_names=getPlayLists();
        String[] array=song_names.toArray(new String[0]);

        for (int i=0;i<array.length;i++)
        {
            list.add(new SongsListData(array[i],"",R.drawable.ic_launcher_foreground));
        }


        recyclerView
                = (RecyclerView)view.findViewById(
                R.id.songsListRV);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter
                = new SongsListRecyclerAdapter(
                list, getActivity().getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity()));


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void getData(){

    }
}
