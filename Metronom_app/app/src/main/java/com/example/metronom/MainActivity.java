package com.example.metronom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static SoundPool soundPool;
    private static HashMap<String, Integer> soundPoolMap;
    static int mod = 0;
    Spinner spinner;
    ArrayAdapter adapter;
    Thread t1;

    boolean stop = false;
    String zvuk;

    private Handler mHandler = new Handler();
    int pockat;

    EditText editText;
    MediaPlayer player;
    TextView textView7;
    TextView textView8;
    TextView textView4;
    TextView textView5;
    Spinner spinnerZvuky;
    int i=0;
    Switch aSwitch;
    Switch aSwitch2;
    EditText editTextNumber4, editTextNumber3, editTextNumber6, editTextNumber2;
    Button add_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editTextNumber);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<String, Integer>();




        Intent intent = getIntent();
        String str = intent.getStringExtra("message_key");

        editText.setText(str);








       List<String> states = Arrays.asList("metronom.wav","pop2.wav");
       spinner = findViewById(R.id.spinnerZvuky);
       adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, states);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinner.setAdapter(adapter);



        textView5 = findViewById(R.id.textView5);
        textView4 = findViewById(R.id.textView4);

        editTextNumber4=findViewById(R.id.editTextNumber2);
        editTextNumber3=findViewById(R.id.editTextNumber3);

        editTextNumber2=findViewById(R.id.editTextNumber2);
        /*editTextTime=findViewById(R.id.editTextTime);*/






        aSwitch2 = findViewById(R.id.switch4);
        editTextNumber4.setVisibility(View.INVISIBLE);
        editTextNumber3.setVisibility(View.INVISIBLE);
        textView5.setVisibility(View.INVISIBLE);
        textView4.setVisibility(View.INVISIBLE);






        aSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(aSwitch2.isChecked())
                {
                    textView4.setText("Kolik zahrát:");
                    textView5.setText("Kolik vynechat:");
                    editTextNumber2.setVisibility(View.VISIBLE);
                    editTextNumber3.setVisibility(View.VISIBLE);
                    textView5.setVisibility(View.VISIBLE);
                    textView4.setVisibility(View.VISIBLE);

                    Intent intent = getIntent();
                    String str2 = intent.getStringExtra("message_key2");
                    String str3 = intent.getStringExtra("message_key3");
                    editTextNumber3.setText(str2);
                    editTextNumber2.setText(str3);



                }
                else {
                    textView4.setText("");
                    textView5.setText("");
                    editTextNumber2.setVisibility(View.INVISIBLE);
                    editTextNumber3.setVisibility(View.INVISIBLE);
                }
            }
        });




    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.btnAdd:
                Intent ht1 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(ht1);
                NastaveniDatabaze myDB = new NastaveniDatabaze(MainActivity.this);
                myDB.pridaniNastaveni(editText.getText().toString().trim(),
                        editTextNumber3.getText().toString().trim(),
                        Integer.valueOf(editTextNumber4.getText().toString().trim()));
                return true;


            case R.id.btnView:
                Intent ht2 = new Intent(MainActivity.this, Nacti.class);
                startActivity(ht2);
                return true;

            case R.id.napoveda_menu:
                setContentView(R.layout.napoveda);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void Zvuk(View view) {
        Intent ht3 = new Intent(MainActivity.this, Zvuk.class);
        startActivity(ht3);


    }


/**  TOTO JE STARÝ KÓD KLEPÁNÍ ... zde ty vlákna fungovaly jen to klepání nefungovalo

 public void zacniOpakovat(View v) {
 player = MediaPlayer.create(MainActivity.this, R.raw.metronom);
 float inter = (float) (60.0/Integer.parseInt(editText.getText().toString()));
 pockat = Math.round(inter*1000);
 System.out.println("Cekam "+pockat);
 i = 1;

 Opakovani.run();
 }

 public void stopOpakovat(View v) {
 mHandler.removeCallbacks(Opakovani);
 }

 private Runnable Opakovani = new Runnable() {
@Override
public void run() {

player.start();

System.out.println("TIKTAK: "+i);
i++;
mHandler.postDelayed(this, pockat);

}








};

 */


    /** TADY SMĚREM DOLŮ JE NOVÝ KOD KLEPÁNÍ KTERÝ FUNGUJE ALE NENÍ V TOM VLÁKNĚ */




    private class RunnableImpl implements Runnable {

        public void run()
        {
            try
            {
                stop = false;
                NastaveniRychlosti(Integer.parseInt(editText.getText().toString()));

                while(!stop)
                {
                    if (MetronomeWillPlay() == true)
                    {
                        prehravaniZvuku(zvuk);
                    }
                }
            }
            catch (Exception ex)
            {

            }
        }
    }



    public void StartOpakovat(View v)
    {
         t1 = new Thread(new RunnableImpl());
        t1.start();
       zvuk = spinner.getSelectedItem().toString();

    }
    public void StopOpakovat(View v) {

        stop=true;
    }


    public void NastaveniRychlosti(int bpm)
    {
        if (bpm == 0)
        {
            mod = 1000;
        }
        else
        {
            mod = 60000 / bpm;
        }
    }

    public boolean MetronomeWillPlay()
    {
        if ((System.currentTimeMillis() % mod) == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void prehravaniZvuku(final String filePath)
    {
        try
        {
            if (!soundPoolMap.containsKey(filePath))
            {
                AssetFileDescriptor afd = getAssets().openFd(filePath);
                if (afd == null)
                {
                    System.out.println("Nelze najít:" + filePath);
                    return;
                }

                int id = soundPool.load(afd, 1);
                soundPoolMap.put(filePath, id);
            }
            int id = soundPoolMap.get(filePath);
            soundPool.play(id, 1.0f, 1.0f, 1, 0, 1.0f);
            System.out.println("Zvuk " + id);

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage() + ex.toString());
        }
    }






}