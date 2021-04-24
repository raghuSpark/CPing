package com.raghu.CPing.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.raghu.CPing.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CodechefFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CodechefFragment extends Fragment {

    private View groupFragmentView;
    private GraphView graphView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CodechefFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CodechefFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CodechefFragment newInstance(String param1, String param2) {
        CodechefFragment fragment = new CodechefFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_codechef, container, false);
        graphView = groupFragmentView.findViewById(R.id.codechefGraphView);
        LineGraphSeries<DataPoint> codechefSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1500),
                new DataPoint(1, 1398),
                new DataPoint(2, 1503),
                new DataPoint(3, 1558),
                new DataPoint(4, 1571),
                new DataPoint(5, 1660),
                new DataPoint(6, 1570),
                new DataPoint(7, 1670),
                new DataPoint(8, 1696)
        });
        codechefSeries.setColor(Color.rgb(255,164,161));
        codechefSeries.setDrawDataPoints(true);
        graphView.setTitleTextSize(18);
        graphView.addSeries(codechefSeries);
        return groupFragmentView;
    }
}