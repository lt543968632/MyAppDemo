package com.example.myapplication;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private boolean StopOperate;
    private boolean ThreadisRun;
    SimpleAdapter adapter;
    private String exFilename;
    private long exavblocknum;
    private long exblocknum;
    private long exblocksize;
    private boolean exfill;
    String[] from;
    private Handler handler;
    private String inFilename;
    private long inavblocknum;
    private long inblocknum;
    private long inblocksize;
    private boolean infill;
    List<Map<String, Object>> list;
    private ProgressBar mProgress;
    Map<String, Object> map;
    private Thread operateThread;
    private TextView operate_indicator_tex;
    private int progressBarStatus;
    int[] to;

    private long avinrom_size;
    private long avexrom_size;

    private CheckBox internal_ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internal_ch = (CheckBox) findViewById(R.id.internal_ch);

        mProgress = (ProgressBar) findViewById(R.id.ProgressBar);
        mProgress.setIndeterminate(false);
        mProgress.setVisibility(View.VISIBLE);
        mProgress.setMax(100);
        //inFilename = "/data/data/inFile.mp3";
        inFilename = Environment.getDataDirectory()+"/inFile.mp3";

        //storage/emulated/0/exFile.mp3
        exFilename = Environment.getExternalStorageDirectory() + "/ex50File.mp3";



        getData();
    }


    @SuppressWarnings("deprecation")
    private void getData() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            exblocksize = (long) sf.getBlockSize();//外部存储块大小
            exavblocknum = (long) sf.getAvailableBlocks();//外部存储可用块数
        }


        File interDir = Environment.getRootDirectory();
        StatFs sfin = new StatFs(interDir.getPath());
        inblocksize = (long) sfin.getBlockSize();//内部存储块大小
        inavblocknum = (long) sfin.getAvailableBlocks();//内部存储可用块数



        avinrom_size = (inblocksize * inavblocknum) / 0x400;//内部存储可用大小 KB
        avexrom_size = (exblocksize * exavblocknum) / 0x400;//外部存储可用大小 KB
        Log.d("hhhh",avinrom_size+"   "+avexrom_size);

    }


    public void fileOperate(View view) {


        if (!ThreadisRun) {
            operateThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    CreateFile.createFile(exFilename, 50, CreateFile.FileUnit.GB);

                }
            });
            operateThread.start();
        }
    }

    public void stopOperate(View view) {
        StopOperate = true;
    }


    public void fileDelete(View view) {
        if (!ThreadisRun) {
            boolean isDelete = false;
            if (infill) {
                File file = new File(inFilename);
                isDelete = true;
                if (file.exists()) {
                    isDelete = file.delete();
                }
                if (isDelete) {
                    Toast.makeText(this, "123", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "123456", Toast.LENGTH_LONG).show();
                }
            }
            if (exfill) {
                File file = new File(exFilename);
                isDelete = true;
                if (file.exists()) {
                    isDelete = file.delete();
                }
                if (isDelete) {
                    Toast.makeText(this, "123", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "123456", Toast.LENGTH_LONG).show();
                }
            }

        }
    }


    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (!ThreadisRun) {
            switch (view.getId()) {
                case R.id.internal_ch: {
                    if (checked) {
                        infill = true;
                        return;
                    }
                    infill = false;
                    return;
                }
                case R.id.external_ch: {
                    if (checked) {
                        exfill = true;
                        return;
                    }
                    exfill = false;
                    break;
                }

            }
        }

    }

}
