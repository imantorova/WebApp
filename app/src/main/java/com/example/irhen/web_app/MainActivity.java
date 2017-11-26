package com.example.irhen.web_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private ListView lv;
    private StringBuffer sb_user;
    private StringBuffer sb_fr;
    private String[] str;
    private Button vk_button, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButtonListener();
        /*
        Получение отпечатка сертификата
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        System.out.println(Arrays.asList(fingerprints));
        */

    }
        /*
        Обработка событий нажития кнопок:
        кнопка vk_button переходит к авторизации пользователя;
        кнопка exit - выход из приложения
         */
    public void addButtonListener () {
        vk_button = (Button) findViewById(R.id.button);
        exit = (Button)findViewById(R.id.exit);

        vk_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        VKSdk.login(MainActivity.this,"friends");
                    }
                }
        );

        exit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Quit application?")
                                .setCancelable(false)
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setTitle("Close application");
                        alertDialog.show();
                    }
                }
        );
    }

    /*
    Обработка результата авторизации
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
            // Пользователь успешно авторизовался, выводится сообщение "Authorized"
                Toast.makeText(MainActivity.this, "Authorized", Toast.LENGTH_SHORT).show();
                lv = (ListView) findViewById(R.id.listView);
                tv = (TextView) findViewById(R.id.textView);

            //Создается запрос на имя и фамилию авторизованного пользователя

                VKRequest request_user = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                        "first_name, last_name"));
                request_user.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        VKList list_user =  (VKList) response.parsedModel;
                        String s = list_user.get(0).fields.toString();
                        String[] lines = s.trim().split("[{,\":} 0-9]");
                        sb_user  = new StringBuffer();
                        for (String k: lines) {
                            if (!k.isEmpty() &&
                                    !k.equals("id") &&
                                    !k.equals("first_name") &&
                                    !k.equals("last_name")) {
                                sb_user.append(k);
                                sb_user.append(" ");
                            }
                        }
                //Результат запроса выводится на экран
                        tv.setText(sb_user.toString());
                    }

                });

                //Создается запрос на имя и фамилию 5-ти друзей авторизованного пользователя

                    VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS
                            ,"first_name, last_name"));
                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            VKList list = (VKList) response.parsedModel;
                            str = new String[5];
                            sb_fr = new StringBuffer();
                            for (int i = 0; i < 5; ++i){
                                str [i] = list.get(i).fields.toString();
                            }
                            for (String k: str) {
                                String[] lines_fr = k.trim().split("[{,\":} 0-9]");
                                for (String n: lines_fr)
                                    if (!n.isEmpty() && !n.equals("id") && !n.equals("first_name") &&
                                            !n.equals("last_name") && !n.equals("online") &&
                                            !n.equals("lists") && !n.equals( "[") )  {
                                            sb_fr.append(n);
                                            sb_fr.append(" ");
                                    }
                            }
                            String[] w = sb_fr.toString().trim().split( "]");
                            for (String k: w) {
                            }
                            ListAdapter adapter = new ArrayAdapter<String>(
                                    MainActivity.this, android.R.layout.simple_list_item_1, w);

                            //Результат запроса выводится на экран

                            lv.setAdapter(adapter);

                        }

                    });


        }
            @Override
            public void onError(VKError error) {
            // Произошла ошибка авторизации, выводится сообщение "Error"
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
            })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}
