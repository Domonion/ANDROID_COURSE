package com.domonion.calc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import operations.*;
import parcer.*;

import static java.lang.StrictMath.max;

public class Main extends AppCompatActivity {

    Button[] digits = new Button[10];
    Button openingBrace;
    Button closingBrace;
    Button clear;
    Button delete;
    Button dot;
    Button plus;
    Button minus;
    Button multiply;
    Button divide;
    Button startButton;
    TextView text;
    final String savedTextKey = "savedText";
    Parser<Double> parser = new ExpressionParser<>(new DoubleOperation());

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(savedTextKey, text.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        text.setText(inState.getString(savedTextKey));
    }

    private String makeText(Character c) {
        return text.getText() + c.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 10; i++)
            digits[i] = findViewById(R.id.button_0 + i);
        openingBrace = findViewById(R.id.button_open_brace);
        closingBrace = findViewById(R.id.button_close_brace);
        dot = findViewById(R.id.button_dot);
        plus = findViewById(R.id.button_plus);
        minus = findViewById(R.id.button_minus);
        divide = findViewById(R.id.button_divide);
        multiply = findViewById(R.id.button_multiply);
        clear = findViewById(R.id.button_clear);
        delete = findViewById(R.id.button_delete);
        startButton = findViewById(R.id.button_equal);
        text = findViewById(R.id.display);
        if(savedInstanceState != null)
            text.setText(savedInstanceState.getString(savedTextKey));
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 10; i++)
                    if (v.getId() == digits[i].getId())
                        text.setText(text.getText() + Integer.toString(i));
                if (v.getId() == openingBrace.getId())
                    text.setText(makeText('('));
                if (v.getId() == closingBrace.getId())
                    text.setText(makeText(')'));
                if (v.getId() == clear.getId())
                    text.setText("");
                if (v.getId() == delete.getId())
                    text.setText(text.getText().subSequence(0, max(0, text.getText().length() - 1)));
                if (v.getId() == plus.getId())
                    text.setText(makeText('+'));
                if (v.getId() == minus.getId())
                    text.setText(makeText('-'));
                if (v.getId() == divide.getId())
                    text.setText(makeText('/'));
                if (v.getId() == multiply.getId())
                    text.setText(makeText('*'));
                if (v.getId() == dot.getId())
                    text.setText(makeText('.'));
                if (v.getId() == startButton.getId()) {
                    try {
                        text.setText(parser.parse(text.getText().toString()).evaluate(0.0, 0.0, 0.0).toString());
                    } catch (Exception e) {
                        text.setText("WRONG!");
                    }
                }
            }
        };
        for (int i = 0; i < 10; i++)
            digits[i].setOnClickListener(listener);
        openingBrace.setOnClickListener(listener);
        closingBrace.setOnClickListener(listener);
        clear.setOnClickListener(listener);
        delete.setOnClickListener(listener);
        startButton.setOnClickListener(listener);
        plus.setOnClickListener(listener);
        minus.setOnClickListener(listener);
        divide.setOnClickListener(listener);
        multiply.setOnClickListener(listener);
        dot.setOnClickListener(listener);
    }
}
