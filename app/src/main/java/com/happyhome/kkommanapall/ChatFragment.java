package com.happyhome.kkommanapall;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.happyhome.kkommanapall.dummy.DummyContent;
import com.happyhome.kkommanapall.model.CareService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int TAG_SERVICEID = 0;
    private static final String TAG = "ChatServiceFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View mPrevView = null;

    private OnFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ValidateUserTask().execute("hello");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onClickChatExecutive(View v){
        Intent i = new Intent(getActivity(), ChatMessageActivity.class);
        i.putExtra("EXTRA_CARESERVICEID", (Integer) v.getTag());
        startActivity(i);
    }

    public void onClickCallExecutive(View v){
        String phone="9000051535";
        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phone));
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // mCareserviceId = getArguments().getInt("EXTRA_CARESERVICEID");
        View rootView = inflater.inflate(R.layout.fragment_cardview, container, false);
        return rootView;
    }

    public void registerCareServiceEventHandlers(){
        ((CareRecyclerViewAdapter) mAdapter).setOnItemClickListener(new CareRecyclerViewAdapter
                .CareItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                LinearLayout detailsView = (LinearLayout)v.findViewById(R.id.extra_servicedetails);
                Integer serviceId = ((CareRecyclerViewAdapter) mAdapter).getItem(position).getCareServiceId();

                if(detailsView.getVisibility() == View.VISIBLE) {
                    detailsView.setVisibility(View.GONE);
                }
                else{
                    detailsView.setVisibility(View.VISIBLE);
                }

                if(mPrevView != null && mPrevView!=detailsView){
                    mPrevView.setVisibility(View.GONE);
                }
                mPrevView = detailsView;
                TextView txtChatExec = (TextView) detailsView.findViewById(R.id.chatExecutive);
                TextView txtCallExec = (TextView) detailsView.findViewById(R.id.callExecutive);
                txtCallExec.setTag(serviceId);
                txtChatExec.setTag(serviceId);
                txtChatExec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickChatExecutive(v);
                    }
                });
                txtCallExec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickCallExecutive(v);
                    }
                });
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    private class ValidateUserTask extends AsyncTask<String, Void, List<CareService>> {
        @Override
        protected List<CareService> doInBackground(String... params) {
            // TODO Auto-generated method stub
            String uri = Constants.BASE_URL + "/careservices";
            List<CareService> careServiceList = new ArrayList<CareService>();
            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Accept", "application/json");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                Type careServiceType = new TypeToken<ArrayList<CareService>>() {
                }.getType();
                careServiceList = new Gson().fromJson(response.toString(), careServiceType);
                in.close();
            } catch (Exception ex) {
                Log.e(TAG, "Some Exception");
            }
            return careServiceList;
        }//close doInBackground

        @Override
        protected void onPostExecute(List<CareService> results) {
            mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.care_recycle_view);
            mAdapter = new CareRecyclerViewAdapter(new ArrayList<CareService>(results));
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mAdapter);
            registerCareServiceEventHandlers();
        }//close onPostExecute
    }

}
