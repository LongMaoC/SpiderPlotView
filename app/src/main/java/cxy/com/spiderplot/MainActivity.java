package cxy.com.spiderplot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cxy.com.spiderplotview.SpiderPlotView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    List<Integer> listNum = new ArrayList<>();
    List<String> listTxt = new ArrayList<>();
    SpiderPlotView spiderPlotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spiderPlotView = (SpiderPlotView) findViewById(R.id.view_spiderPlot);


        ((SeekBar) findViewById(R.id.sb1)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.sb2)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.sb3)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.sb4)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.sb5)).setOnSeekBarChangeListener(this);


        initData();
    }


    @Override
    protected void onResume() {
        super.onResume();

        spiderPlotView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    // 当layout执行结束后回调此方法
                    @Override
                    public void onGlobalLayout() {
                        spiderPlotView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        spiderPlotView.setDataNum(listNum);
                        spiderPlotView.setDataTxt(listTxt);
                        spiderPlotView.setRange(100);
                        spiderPlotView.update();
                    }
                });

    }

    private void initData() {
        listTxt.add("物理");
        listTxt.add("法术");
        listTxt.add("护甲");
        listTxt.add("魔抗");
        listTxt.add("生命值");

        Random random = new Random();
        for (int i = 0; i < listTxt.size(); i++) {
            int temp = random.nextInt(101);
            listNum.add(temp);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int values = seekBar.getProgress();

        switch (seekBar.getId()) {
            case R.id.sb1:
                listNum.set(0, values);
                break;
            case R.id.sb2:
                listNum.set(1, values);
                break;
            case R.id.sb3:
                listNum.set(2, values);
                break;
            case R.id.sb4:
                listNum.set(3, values);
                break;
            case R.id.sb5:
                listNum.set(4, values);
                break;
        }
        spiderPlotView.update();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
