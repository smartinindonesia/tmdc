package id.pptik.org.homecaretimedic;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.pptik.org.homecaretimedic.R;
import id.pptik.org.homecaretimedic.customuicompt.ButtonModel;
import id.pptik.org.homecaretimedic.model.submitmodel.PickedDateTime;
import id.pptik.org.homecaretimedic.model.submitmodel.SubmitInfo;

public class DateTimePickActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnChooseDate)
    Button btnChooseDate;
    @BindView(R.id.gotoNext)
    Button btnGoToNext;
    @BindView(R.id.datePick)
    EditText tglPelayanan;

    private DatePickerDialog datePickerDialog;
    private PickedDateTime pickedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time_pick);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        createTitleBar();
        createBtnLayout();

        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH) + 2;
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(DateTimePickActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tglPelayanan.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        pickedDateTime.setDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mcurrentTime.get(Calendar.YEAR), mcurrentTime.get(Calendar.MONTH), mcurrentTime.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setTitle("Pilih tanggal pelayanan");
                datePickerDialog.show();
                tglPelayanan.setText(day + "-" + (month + 1) + "-" + year);
            }
        });

        btnGoToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DateTimePickActivity.this, HCAssestmentActivity.class);
                SubmitInfo.selectedDateTime = pickedDateTime;
                startActivity(intent);
                finish();
            }
        });

        pickedDateTime = new PickedDateTime();
    }

    public void createBtnLayout() {
        int row_count = 6;
        LinearLayout layoutVertical = (LinearLayout) findViewById(R.id.btnLayout);
        LinearLayout rowLayout = null;
        final ButtonModel<String>[] buttons = new ButtonModel[24];

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1);

        for (int i = 0; i < buttons.length; i++) {
            if (i % row_count == 0) {
                rowLayout = new LinearLayout(this);
                rowLayout.setWeightSum(row_count);
                layoutVertical.addView(rowLayout, param);
            }

            buttons[i] = new ButtonModel<String>();
            buttons[i].button = new ToggleButton(this);
            buttons[i].button.setText(i+":00");
            buttons[i].button.setTextSize(8.0f);

            buttons[i].button.setTextColor(getResources().getColorStateList(R.color.sign_in_selector_fg));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                buttons[i].button.setBackground(getResources().getDrawable(R.drawable.sign_in_selector_bg));
            } else {
                buttons[i].button.setBackgroundDrawable(getResources().getDrawable(R.drawable.sign_in_selector_bg));
            }

            if (i==8){
                buttons[i].button.setEnabled(false);
            }

            LinearLayout blayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            blayout.setLayoutParams(params);
            blayout.setPadding(2,2,2,2);
            blayout.addView(buttons[i].button);
            rowLayout.addView(blayout, param);

        }

        for (int i = 0; i < buttons.length; i++) {
            final int finalI = i;
            buttons[finalI].button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < buttons.length; j++){
                        buttons[j].button.setText(j+":00");
                        buttons[j].setOn(false);
                    }
                    buttons[finalI].button.setText("On");
                    buttons[finalI].setOn(true);
                    pickedDateTime.setTime(""+finalI);
                }
            });
        }
    }

    public void createTitleBar() {
        ActionBar mActionbar = getSupportActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(false);
        mActionbar.setDefaultDisplayHomeAsUpEnabled(false);
        mActionbar.setDisplayShowTitleEnabled(false);
        mActionbar.setDisplayShowHomeEnabled(false);
        mActionbar.setDisplayShowCustomEnabled(true);
        View view = getLayoutInflater().inflate(R.layout.action_bar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mActionbar.setCustomView(view, params);
    }
}
