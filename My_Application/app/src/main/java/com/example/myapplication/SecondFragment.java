package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SecondFragment extends Fragment {
    private Button write2FileButton;
    private EditText XeditText;
    private EditText YeditText;
    private ListView listView;
    private WiFiManagent wiFiManagent;
    private String fileName = null;
    private boolean isNewFile = true;
    private boolean isRecording = false;
    private Float x = 0f;
    private Float y = 0f;
    private int z = -1;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // final User user ;
        Button scanButton = getActivity().findViewById(R.id.scanButton);
        write2FileButton = getActivity().findViewById(R.id.write2File);
        XeditText = getActivity().findViewById(R.id.xeditTextNumber);
        YeditText = getActivity().findViewById(R.id.yeditTextNumber);
        listView = getActivity().findViewById(R.id.listView);
        Spinner spinner = getActivity().findViewById(R.id.Zspinner);
        wiFiManagent = new WiFiManagent(getActivity());
        write2FileButton = getActivity().findViewById(R.id.write2File);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //扫描并显示
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }

                System.out.println(wiFiManagent.getBasicInfo());
                wiFiManagent.scanWiFi();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, wiFiManagent.getBasicInfo());
                System.out.println(arrayAdapter);
                listView.setAdapter(arrayAdapter);
            }
        });

        write2FileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取坐标
                x = Float.valueOf(XeditText.getText().toString());
                y = Float.valueOf(YeditText.getText().toString());
                //写入文件
                if (isNewFile){
                    fileName = getCurrentTime()+".txt";
                    isNewFile =false;
                }
                try {
                    write2File(fileName);
                    Toast.makeText(getActivity(),"内容已写入"+fileName,Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Log.e("写入文件出错",e.toString());
                }

            }
        });
    }

    String getCurrentTime(){
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        return simpleDateFormat.format(date);
    }
    void write2File(String fileName) throws IOException {
        ArrayList<ScanResult> wifiList = wiFiManagent.getWifiList();
        File file = new File(this.getActivity().getExternalFilesDir(null),fileName);
        FileWriter fileWriter = new FileWriter(file,!isNewFile);
        fileWriter.write(x + "|" + y + "|" + z + ' ');
        StringBuilder stringBuilder = new StringBuilder();
        for (ScanResult e:wifiList){
            stringBuilder.append(e.BSSID).append('|').append(e.level).append(' ');
        }
        stringBuilder.append("\r");
        fileWriter.write(stringBuilder.toString());
        fileWriter.flush();
        fileWriter.close();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setContentView(R.layout.fragment_second);
//        scanButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "scanButton is cliked", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//            }
//        });
        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }
}