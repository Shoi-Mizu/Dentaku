package com.shoi.dentaku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView; //表示部
    private HorizontalScrollView scrollView; //表示部（スクロール）
    private int listIndex = 0; //リスト指定用の数字
    private int lastListIndex = 0; //LastListに格納時に使用するインデックス
    private List<String> numberList = new ArrayList<String>(); //数値を格納するリスト
    private List<String> calcList = new ArrayList<String>(); //計算キーを格納するリスト
    private List<Double> lastList = new ArrayList<Double>(); //最後に足し算を行う前の数値を格納する
    private double kari = 0; //数値、計算キー入れ替え時に仮で保管するための変数
    private double answer = 0; //答え
    private BigDecimal displayAnswer; //答えを表示させる為の変数
    private int size = 0; //TextViewの要素数
    private int beforeSize = 0; //sizeにpushを代入する前の値
    private int push = 0; //ボタンを押した回数
    private boolean notCalc = false; //最後の格納時に計算せずに格納したか
    private boolean dotPushed = false; //ドットを押したかどうか
    private boolean numberPushed = false; //数値をおしたかどうか
    private boolean calcPushed = false; //計算キーを押したかどうか
    private int kakkoIn = 0; //括弧内かどうか(0で括弧外)
    private List<Integer> kakkoInListIndex = new ArrayList<Integer>(); //括弧内（グループごと）の要素数を格納する
    private int kakkoIndex = 0; //括弧前のlistIndexの要素数を格納する為
    private int index = 0; //listIndexか、beforeIndexを入れる
    private boolean kakkoleft = false; //括弧始まり
    private boolean kakkoright = false; //括弧終わり
    private boolean answered = false; //答えを出した直後か

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView); //表示部のIDを取得
        textView.setText(null); //表示部の初期化
        scrollView = findViewById(R.id.scrollView);//表示部(スクロール)のIDを取得

    }

    //数字を押した時の処理
    public void numberPush(View view){
        Button number = (Button)view;
        if(kakkoright == false){
            //最初の1文字目の処理
            if(push == 0){
                textView.setText(number.getText());
                numberList.add(listIndex, textView.getText().toString());
                beforeSize = size;
                //計算キー押した直後の数値入力
            } else if(size - beforeSize != 0){
                textView.append(number.getText());
                numberList.add(listIndex, textView.getText().toString().substring(size));
                beforeSize = size;
                dotPushed = false;
                //数値を連続して押した時の処理
            } else {
                textView.append(number.getText());
                numberList.set(listIndex, textView.getText().toString().substring(size));
            }
            push++;
            numberPushed = true;
            calcPushed = false;
            answered = false;
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_RIGHT);
                }
            });
        }
    }

    //ドット（.）を押した時の処理
    public void dotPush(View view){
        if((numberPushed == true) && (dotPushed == false)){
            Button dot = (Button) view;
            textView.append(dot.getText());
            numberList.set(listIndex, textView.getText().toString().substring(size));
            dotPushed = true;
            numberPushed = false;
            push++;
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_RIGHT);
                }
            });
        }
    }

    //括弧を押した時の処理
    public void kakkoPush(View view){
        Button kakko = (Button) view;
        if(answered == false) {
            if ((numberPushed == false) && (dotPushed == false)) {
                //左括弧
                if (kakko.getText().equals("(")) {
                    textView.append(kakko.getText());
                    kakkoIn++;
                    push++;
                    size = push;
                    kakkoInListIndex.add(kakkoIndex, listIndex);
                    kakkoleft = true;
                    kakkoIndex++;
                    answered = false;
                }
            } else if (kakkoIn != 0) {
                //右括弧
                if (kakko.getText().equals(")")) {
                    textView.append(kakko.getText());
                    kakkoIn--;
                    push++;
                    size = push;
                    index = kakkoInListIndex.get(kakkoIndex - 1);
                    if (listIndex != kakkoInListIndex.get(kakkoIndex - 1)) {
                        calculation();
                    } else {
                        answer = Double.parseDouble(numberList.get(index));
                    }
                    lastListIndex = 0;
                    lastList.clear();
                    numberList.set(index, String.valueOf(answer));
                    listIndex = kakkoInListIndex.get(kakkoIndex - 1);
                    kakkoIndex--;
                    notCalc = false;
                    answered = false;
                    kakkoright = true;
                    if (kakkoIn == 0) {
                        numberPushed = true;
                    }
                }
            }
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_RIGHT);
                }
            });
        }
    }

    //計算キーを押した時の処理
    public void calcPush(View view){
        Button calc = (Button)view;
        if((calcPushed == false) || (kakkoleft == true)){
            if((push == 0) && (answer != 0)){
                numberList.add(listIndex, String.valueOf(displayAnswer));
                push = String.valueOf(displayAnswer).length();
                calcList.add(listIndex, calc.getText().toString());
                textView.append(calcList.get(listIndex));
                listIndex++;
                push++;
                size = push;
                numberPushed = false;
                dotPushed = false;
                calcPushed = true;
                answered = false;
                kakkoright = false;
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_RIGHT);
                    }
                });
            } else if((push != 0) && (numberPushed == true)){
                String s = numberList.get(listIndex);
                calcList.add(listIndex, calc.getText().toString());
                textView.append(calcList.get(listIndex));
                listIndex++;
                push++;
                size = push;
                numberPushed = false;
                dotPushed = false;
                calcPushed = true;
                answered = false;
                kakkoright = false;
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_RIGHT);
                    }
                });
            } else if(calc.getText().equals("-")){
                if((push == 0) || (kakkoleft == true)){
                    textView.append(calc.getText());
                    numberList.add(listIndex, "-1");
                    calcList.add(listIndex, "×");
                    listIndex++;
                    push++;
                    size = push;
                    numberPushed = false;
                    dotPushed = false;
                    calcPushed = true;
                    answered = false;
                    kakkoright = false;
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_RIGHT);
                        }
                    });
                }
            }
        }
    }

    //イコールを押した時の処理
    public void equalPush(View view){
        if((numberPushed == true) && (kakkoIn == 0)){
            index = 0;
            calculation();
            textView.setText(String.valueOf(displayAnswer));
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_LEFT);
                }
            });
            numberList.clear();
            calcList.clear();
            lastList.clear();
            listIndex = 0;
            push = 0;
            size = 0;
            beforeSize = 0;
            lastListIndex = 0;
            notCalc = false;
            dotPushed = false;
            numberPushed = false;
            kakkoIn = 0;
            calcPushed = false;
            answered = true;
            kakkoright = false;
        }
    }

    //クリアボタンを押した時の処理
    public void clearPush(View view){
        textView.setText(null);
        numberList.clear();
        calcList.clear();
        lastList.clear();
        push = 0;
        size = 0;
        beforeSize = 0;
        answer = 0;
        lastListIndex = 0;
        listIndex = 0;
        notCalc = false;
        dotPushed = false;
        numberPushed = false;
        kakkoIn = 0;
        calcPushed = false;
        answered = false;
        kakkoright = false;
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_LEFT);
            }
        });
    }

    //計算する処理
    private void calculation(){
        if((listIndex != 0)) {
            //calcListにマイナスがあるが調べて変換する
            for(int i = index; i < listIndex; i++){
                if(calcList.get(i).equals("-")){
                    calcList.set(i, "+");
                    double d = Double.parseDouble(numberList.get(i + 1)) * (-1);
                    numberList.set(i + 1, String.valueOf(d));
                }
            }
            //numberListをLastListに格納する
            for(int i = index; i < listIndex; i++){
                //calcListが+なら、そのまま数値を格納する
                if(calcList.get(i).equals("+") == true){
                    lastList.add(lastListIndex, Double.parseDouble(numberList.get(i)));
                    lastListIndex++;
                    //calcListに×、÷、%があるか調べて、ある場合は計算してからlastListに格納する
                } else {
                    kari = Double.parseDouble(numberList.get(i));
                    while (i < listIndex){
                        if(calcList.get(i).equals("+") == false) {
                            double b = Double.parseDouble(numberList.get(i + 1));
                            if (calcList.get(i).equals("×")) {
                                kari = kari * b;
                            } else if (calcList.get(i).equals("÷")) {
                                kari = kari / b;
                            } else if (calcList.get(i).equals("%")) {
                                kari = (kari / 100) * b;
                            }
                        } else {
                            break;
                        }
                        i++;
                    }
                    lastList.add(lastListIndex,kari);
                    lastListIndex++;
                }
                //最後の数値を格納する
                if(i == listIndex - 1){
                    lastList.add(lastListIndex,Double.parseDouble(numberList.get(i + 1)));
                    notCalc =true;
                }
            }
            //最後の足し算の時の処理回数を合わせる
            if(notCalc == false){
                lastListIndex--;
            }
            //lastListに格納した数値を全部足す
            answer = lastList.get(0);
            if(lastList.size() > 1){
                for(int i = 0; i < lastListIndex; i++){
                    answer = answer + lastList.get(i + 1);
                }
            }
        } else {
            answer = Double.parseDouble(numberList.get(0));
        }
        displayAnswer = BigDecimal.valueOf(answer);
    }

}
