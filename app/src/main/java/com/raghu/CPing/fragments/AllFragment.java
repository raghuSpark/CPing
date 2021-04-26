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

public class AllFragment extends Fragment {

    private View groupFragmentView;
    private GraphView graphView;

    public AllFragment() {
        // Required empty public constructor
    }

    public static AllFragment newInstance(String param1, String param2) {
        AllFragment fragment = new AllFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_all, container, false);
        graphView = groupFragmentView.findViewById(R.id.allGraphView);
        LineGraphSeries<DataPoint> codeForcesSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 363),
                new DataPoint(1, 615),
                new DataPoint(2, 781),
                new DataPoint(3, 825),
                new DataPoint(4, 824),
                new DataPoint(5, 988),
                new DataPoint(6, 973),
                new DataPoint(7, 866),
                new DataPoint(8, 1042)
        });
        codeForcesSeries.setColor(Color.rgb(72,221,205));
        codeForcesSeries.setDrawDataPoints(true);
        graphView.setTitleTextSize(18);
        graphView.addSeries(codeForcesSeries);

        LineGraphSeries<DataPoint> codeChefSeries = new LineGraphSeries<>(new DataPoint[]{
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
        codeChefSeries.setColor(Color.rgb(255,164,161));
        codeChefSeries.setDrawDataPoints(true);
        graphView.setTitleTextSize(18);
        graphView.addSeries(codeChefSeries);

//        LineGraphSeries<DataPoint> leetcodeSeries = new LineGraphSeries<>(new DataPoint[]{
//                new DataPoint(0, 1500),
//                new DataPoint(1, 1454),
//                new DataPoint(2, 1427),
//                new DataPoint(3, 1452),
//                new DataPoint(4, 1441),
//                new DataPoint(5, 1454),
//                new DataPoint(6, 1427),
//                new DataPoint(7, 1442),
//                new DataPoint(8, 1408),
//                new DataPoint(9, 1485)
//        });
//        leetcodeSeries.setColor(Color.rgb(254,167,88));
//        leetcodeSeries.setDrawDataPoints(true);
//        graphView.setTitleTextSize(18);
//        graphView.addSeries(leetcodeSeries);

        return groupFragmentView;
    }
}