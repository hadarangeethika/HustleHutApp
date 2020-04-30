package com.mygym.fonte;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.mygym.DAO.DAOCardio;
import com.mygym.DAO.DAOFonte;
import com.mygym.DAO.DAOMachine;
import com.mygym.DAO.DAOStatic;
import com.mygym.DAO.Machine;
import com.mygym.DAO.Profile;
import com.mygym.MainActivity;
import com.mygym.R;

import java.util.ArrayList;
import java.util.List;

public class FonteGraphFragment extends Fragment {
    MainActivity mActivity = null;
    ArrayAdapter<String> mAdapterMachine = null;
    //Profile mProfile = null;
    List<String> mMachinesArray = null;
    private String name;
    private int id;
    private Spinner functionList = null;
    private Spinner machineList = null;
    private LineChart mLineChart = null;
    private LinearLayout mGraphZoomSelector = null;
    private BarChart mBarChart = null;
    private DAOFonte mDbFonte = null;
    private DAOCardio mDbCardio = null;
    private DAOStatic mDbStatic = null;
    private DAOMachine mDbMachine = null;
    private View mFragmentView = null;
    private OnItemSelectedListener onItemSelectedList = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (parent.getId() == R.id.filterGraphMachine)
                updateFunctionSpinner(); // Update functions only when changing exercise

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private OnClickListener onZoomClick = v -> {

    };

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static FonteGraphFragment newInstance(String name, int id) {
        FonteGraphFragment f = new FonteGraphFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_graph, container, false);
        mFragmentView = view;
        functionList = view.findViewById(R.id.filterGraphFunction);
        machineList = view.findViewById(R.id.filterGraphMachine);
        Button allButton = view.findViewById(R.id.allbutton);
        Button lastyearButton = view.findViewById(R.id.lastyearbutton);
        Button lastmonthButton = view.findViewById(R.id.lastmonthbutton);
        Button lastweekButton = view.findViewById(R.id.lastweekbutton);

        /* Initialisation des evenements */
        machineList.setOnItemSelectedListener(onItemSelectedList);
        functionList.setOnItemSelectedListener(onItemSelectedList);

        allButton.setOnClickListener(onZoomClick);
        lastyearButton.setOnClickListener(onZoomClick);
        lastmonthButton.setOnClickListener(onZoomClick);
        lastweekButton.setOnClickListener(onZoomClick);


        mGraphZoomSelector = view.findViewById(R.id.graphZoomSelector);
        mLineChart = view.findViewById(R.id.graphLineChart);

        mBarChart = view.findViewById(R.id.graphBarChart);

        if (mDbFonte == null) mDbFonte = new DAOFonte(getContext());
        if (mDbCardio == null) mDbCardio = new DAOCardio(getContext());
        if (mDbStatic == null) mDbStatic = new DAOStatic(getContext());
        if (mDbMachine == null) mDbMachine = new DAOMachine(getContext());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getProfil() != null) {
            mMachinesArray = new ArrayList<String>(0); //Data are refreshed on show //mDbFonte.getAllMachinesStrList(getProfil());
            // lMachinesArray = prepend(lMachinesArray, "All");
            mAdapterMachine = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item,
                mMachinesArray);
            mAdapterMachine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            machineList.setAdapter(mAdapterMachine);
            mDbFonte.closeCursor();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (MainActivity) activity;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Save Shared Preferences
    }

    public MainActivity getMainActivity() {
        return this.mActivity;
    }

    private void updateFunctionSpinner() {
        if (machineList.getSelectedItem() == null) return;  // List not yet initialized.
        String lMachineStr = machineList.getSelectedItem().toString();
        Machine machine = mDbMachine.getMachine(lMachineStr);
        if (machine == null) return;

        ArrayAdapter<String> adapterFunction = null;

        if (machine.getType() == DAOMachine.TYPE_FONTE ) {
            adapterFunction = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item,
                mActivity.getResources().getStringArray(R.array.graph_functions));
        } else if (machine.getType() == DAOMachine.TYPE_CARDIO) {
            adapterFunction = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item,
                mActivity.getResources().getStringArray(R.array.graph_cardio_functions));
        } else if (machine.getType() == DAOMachine.TYPE_STATIC) {
            adapterFunction = new ArrayAdapter<>( getContext(), android.R.layout.simple_spinner_item,
                mActivity.getResources().getStringArray(R.array.graph_static_functions));
        }
        adapterFunction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        functionList.setAdapter(adapterFunction);
    }

    public String getName() {
        return getArguments().getString("name");
    }

    public int getFragmentId() {
        return getArguments().getInt("id", 0);
    }

    public DAOFonte getDB() {
        return mDbFonte;
    }

    private ArrayAdapter<String> getAdapterMachine() {
        ArrayAdapter<String> a;
        mMachinesArray = new ArrayList<String>(0); //Data are refreshed on show //mDbFonte.getAllMachinesStrList(getProfil());
        // lMachinesArray = prepend(lMachinesArray, "All");
        mAdapterMachine = new ArrayAdapter<>(
            getContext(), android.R.layout.simple_spinner_item,
            mMachinesArray);
        mAdapterMachine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        machineList.setAdapter(mAdapterMachine);
        return mAdapterMachine;
    }

    private Profile getProfil() {
        return mActivity.getCurrentProfil();
    }

    private String getFontesMachine() {
        return getMainActivity().getCurrentMachine();
    }


    public void saveSharedParams(String toSave, String paramName) {
        SharedPreferences sharedPref = this.mActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(toSave, paramName);
        editor.apply();
    }

    public String getSharedParams(String paramName) {
        SharedPreferences sharedPref = this.mActivity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(paramName, "");
    }

}
