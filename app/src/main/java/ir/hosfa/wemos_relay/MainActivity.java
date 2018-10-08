package ir.hosfa.wemos_relay;

//<editor-fold desc="نمونه های یو آر ال">
/*
http://chaluspl.dlinkddns.com:60978/url?relay=0
http://chaluspl.dlinkddns.com:60978/url?builtinled=0
http://chaluspl.dlinkddns.com:60978/url?relay=0&builtinled=0
http://chaluspl.dlinkddns.com:60978/url?relay=0&builtinled=1
*/
//</editor-fold>


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import ir.hosfa.wemos_relay.Extra.Utility;


public class MainActivity extends AppCompatActivity {

    ToggleButton tgbRelay;
    ToggleButton tgbBuiltinLed;
    ToggleButton tgbRelayAndBuiltinLed;

    ImageView btnShowPopup;

    //...
    AlertDialog.Builder builder;
    //...
    CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    //...
    public static SharedPreferences sharedPreferences;
    //...

    String qryString = "";

    //بسته باینکه بر روی اینترنت و یا شبکه محلی اجرا شود، لینک زیر تغییر میکند
    //String urlTemplate = "http://192.168.1.150:60978/url?%s";
    //String urlTemplate = "http://chaluspl.dlinkddns.com:60978/url?%s";
    String urlTemplate = "";

    //...

    Handler handler;
    ProgressDialog progressDialog;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressDialog.dismiss();
        }
    };

    //...

    // مدیریت دکمه ‍"بک" برای خروج از برنامه
    private long m_backPressed;
    private static final int TIME_INTERVAL = 2000; // # milliseconds

    //...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //...

        if (Utility.isConnectedToInternet()) {
            init();
            changeAppFont();

            if (isCheckStatusOnStartup())
                onMenuRefreshStatusClicked();
        } else {
            startActivity(new Intent(MainActivity.this, NoInternetActivity.class));
            this.finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //...
        //TODO: **shayad niaz nabashad ke inj bashad!
        readSharedPreferencesSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //...
    }

    @Override
    public void onBackPressed() {
        if (m_backPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            m_backPressed = System.currentTimeMillis();

            Toast toast = Toast.makeText(this, "کلید بازگشت را دوباره بفشارید",
                    Toast.LENGTH_SHORT);
            //toast.getView().setBackgroundColor(0xFF7282DB);
            //toast.getView().setBackgroundColor(0x1A237E);  // in baes mishe bg hide beshe va faghat text show beshe
            toast.getView().setBackgroundColor(0xFF1A237E);

            //TODO:* badan range matne toast ra avaz konam(sefid konam), font va size_font ra ham avaz konam
            //TextView tvToast = toast.getView().findViewById(android.support.design.R.id.toast);

            toast.show();
        }
    }

    private void setKeepScreenOn(boolean keepScreenOn) {
        if (keepScreenOn)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    void changeAppFont() {
        //NOTE:*  fonte barname ra avaz konam

        tgbRelay.setTypeface(Utility.getTypeFace());
        tgbRelay.setTextSize(18);

        tgbBuiltinLed.setTypeface(Utility.getTypeFace());
        tgbBuiltinLed.setTextSize(18);

        tgbRelayAndBuiltinLed.setTypeface(Utility.getTypeFace());
        tgbRelayAndBuiltinLed.setTextSize(18);

        //--------------------------------------------------------------------
//        progressDialog.

//        SpannableString spannableString =  new SpannableString(message);
//
//        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(getContext().getAssets(), "fonts/Lato-Regular.ttf"));
//        spannableString.setSpan(typefaceSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        dialog.setMessage(spannableString);
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setIndeterminate(true);
//        dialog.setCancelable(false);
//        dialog.show();
        //--------------------------------------------------------------------


    }

    private void init() {

        tgbRelay = findViewById(R.id.tgbRelay);
        tgbBuiltinLed = findViewById(R.id.tgbBuiltinLed);
        tgbRelayAndBuiltinLed = findViewById(R.id.tgbRelayAndBuiltinLed);

        //...

        btnShowPopup = findViewById(R.id.btnShowPopup);
        btnShowPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnShowPopup);
                popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_about:
                                        //Dialog d = new Dialog(this);
                                        //d.requestWindowFeature(Window.FEATURE_NO_TITLE); //d.setTitle("درباره برنامه");
                                        //d.setContentView(R.layout.dialog_about);
                                        //d.setCancelable(true);
                                        //d.show();

                                        Dialog d = new Dialog(MainActivity.this, R.style.PauseDialog);
                                        d.requestWindowFeature(Window
                                                .FEATURE_NO_TITLE); //d.setTitle("درباره برنامه");
                                        d.setContentView(R.layout.dialog_about);
                                        d.setCancelable(true);
                                        d.show();

                                        return true;
                                    case R.id.action_options:
                                        startActivity(new Intent(MainActivity.this, Settings.class));
                                        return true;
                                    case R.id.action_refresh_status:
                                        onMenuRefreshStatusClicked();
                                        return true;
                                }
                                return true;
                            }
                        }
                );
                popupMenu.show();
            }
        });

        //...
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        //...

        //builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("خطا")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        //...

        handler = new Handler();

        progressDialog = new ProgressDialog(this);
        ///progressDialog.setTitle("اندکی صبر!");

        //TODO:* badan in ra ba faraham kardane tamhidate lazem true konam
        progressDialog.setCancelable(false);
//        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//
//            }
//        });

        //...

        tgbRelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qryString = tgbRelay.isChecked() ? "relay=1" : "relay=0";
                progressDialog.setMessage(tgbRelay.isChecked() ? "روشن کردن رله" : "خاموش کردن رله");
                executeUrl(tgbRelay);
            }
        });
        tgbRelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //getDrawable az API 21 be ba'ad kar mikoneh. man min API ra 19 gereftam
//                tgbRelay.setButtonDrawable(isChecked ?
//                        getDrawable(R.drawable.bulb_on) : getDrawable(R.drawable.bulb_off)
//                );

                tgbRelay.setButtonDrawable(
                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_off)
                );

            }
        });

        tgbBuiltinLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qryString = tgbBuiltinLed.isChecked() ? "builtinled=1" : "builtinled=0";
                progressDialog.setMessage(
                        tgbBuiltinLed.isChecked() ? "روشن کردن ال ای دی توکار" : "خاموش کردن ال ای دی توکار");
                executeUrl(tgbBuiltinLed);
            }
        });
        tgbBuiltinLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                tgbBuiltinLed.setButtonDrawable(isChecked ?
//                        getDrawable(R.drawable.bulb_on) : getDrawable(R.drawable.bulb_off)
//                );
                tgbBuiltinLed.setButtonDrawable(
                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_off)
                );
            }
        });

        tgbRelayAndBuiltinLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (true)
//                    return;

                qryString = tgbRelayAndBuiltinLed.isChecked() ? "relay=1&builtinled=1" : "relay=0&builtinled=0";
                progressDialog.setMessage(
                        tgbRelayAndBuiltinLed.isChecked() ? "روشن کردن رله و ال ای دی توکار" : "خاموش کردن رله و ال ای دی توکار");
                executeUrl(tgbRelayAndBuiltinLed);
            }
        });
        tgbRelayAndBuiltinLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                tgbRelayAndBuiltinLed.setButtonDrawable(isChecked ?
//                        getDrawable(R.drawable.bulb_on) : getDrawable(R.drawable.bulb_off)
//                );
                tgbRelayAndBuiltinLed.setButtonDrawable(
                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_off)
                );
            }
        });


        //...

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Disble activity :
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //...

                refreshStatus();
            }
        });

        //...

        //NOTE: actionbar ra hambe toolbar ezafe kikonad!
//        Toolbar toolbar = findViewById(R.id.customToolbar);
//        setSupportActionBar(toolbar);

        //...

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //NOTE: readSharedPreferencesSettings();  // run on : onStart() or onResume() ?????

        //...

    }

    //---------------------------------------------------------------------------------------------

    private Snackbar createErrorSnakbar() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "خطایی رخ داده است",
                Snackbar.LENGTH_LONG);
        snackbar.setDuration(5000);
        View snackbarView = snackbar.getView();
        snackbarView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        //snackbarView.setBackgroundColor(Color.LTGRAY);
        //snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        TextView tvSnackbarText = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tvSnackbarText.setTypeface(Utility.getTypeFace());
        tvSnackbarText.setTextSize(16);
        //tvSnackbarText.setTextColor(Color.WHITE);

        TextView tvSnackbarAction = snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        tvSnackbarAction.setTypeface(Utility.getTypeFace());
        tvSnackbarAction.setTextSize(14);
        snackbar.setActionTextColor(Color.RED);    // tvSnackbarAction.setTextColor(Color.RED);

        ViewCompat.setLayoutDirection(snackbarView, ViewCompat.LAYOUT_DIRECTION_RTL);

        snackbar.setAction("شرح خطا", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
            }
        });
        //snackbar.show();

        return snackbar;
    }

    //---------------------------------------------------------------------------------------------

    void updateUrlTemplate() {
        String portNumber = getDefaultPortNumber();

        urlTemplate = isAppRunOnInternet() ?
                "http://chaluspl.dlinkddns.com:" + portNumber + "/url?%s" :
                "http://192.168.1.150:" + portNumber + "/url?%s";
    }

    void readSharedPreferencesSettings() {
        updateUrlTemplate();
        //...
        setKeepScreenOn(isScreenAlwaysOn());
        //...

    }

    boolean isCheckStatusOnStartup() {
        return sharedPreferences.getBoolean("chk_check_state_at_start", true);
    }

    boolean isAppRunOnInternet() {
        return sharedPreferences.getBoolean("chk_run_on_internet", true);
    }

    boolean isScreenAlwaysOn() {
        return sharedPreferences.getBoolean("chk_keep_screen_on", true);
    }

    String getDefaultPortNumber() {
        return sharedPreferences.getString("edt_port_number", "60978");
    }

    //---------------------------------------------------------------------------------------------

    private void hideProgressDialog() {
        //QTimer::singleShot(2000, SLOT(runnable) )

        if (progressDialog.isShowing())
            handler.postDelayed(runnable, 200);
    }

    private void executeUrl(View sender) {

        ToggleButton tgb;

        switch (sender.getId()) {
            case R.id.tgbRelay:
                tgb = tgbRelay;
                break;
            case R.id.tgbBuiltinLed:
                tgb = tgbBuiltinLed;
                break;
            case R.id.tgbRelayAndBuiltinLed:
                tgb = tgbRelayAndBuiltinLed;
                break;
            default:
                //in aslan nabayad ejra shavad
                Toast.makeText(this, "executeUrl(View sender) : Invalid sender",
                        Toast.LENGTH_SHORT).show();
                return;
        }

        //...
        progressDialog.show();
        //...

        updateUrlTemplate();

        final ToggleButton finalTgb = tgb;
        final String urlFinal = String.format(urlTemplate, qryString);

        StringRequest request = new StringRequest(Request.Method.GET,
                urlFinal,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (finalTgb == tgbRelayAndBuiltinLed) {
                            boolean isTgbRelayAndBuiltinLedChecked = finalTgb.isChecked();
                            tgbRelay.setChecked(isTgbRelayAndBuiltinLedChecked);
                            tgbBuiltinLed.setChecked(isTgbRelayAndBuiltinLedChecked);
                        }

                        if (tgbRelay.isChecked() && tgbBuiltinLed.isChecked()) {
                            tgbRelayAndBuiltinLed.setChecked(true);
//                            tgbRelayAndBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_on));
                            tgbRelayAndBuiltinLed.setButtonDrawable(
                                    ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_on));
                        } else if (!tgbRelay.isChecked() && !tgbBuiltinLed.isChecked()) {
                            tgbRelayAndBuiltinLed.setChecked(false);
//                            tgbRelayAndBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_off));
                            tgbRelayAndBuiltinLed.setButtonDrawable(
                                    ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_off));
                        } else {
//                            tgbRelayAndBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_on_off));
                            tgbRelayAndBuiltinLed.setButtonDrawable(
                                    ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_on_off));
                        }


                        tgbRelay.setButtonDrawable(tgbRelay.isChecked() ?
//                                getDrawable(R.drawable.bulb_on) : getDrawable(R.drawable.bulb_off)
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_on) :
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_off)
                        );

                        tgbBuiltinLed.setButtonDrawable(tgbBuiltinLed.isChecked() ?
//                                getDrawable(R.drawable.bulb_on) : getDrawable(R.drawable.bulb_off)
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_on) :
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_off)
                        );


                        //TODO: ************************************************************************


//                            if (isTgbRelayAndBuiltinLedChecked) {
//                                tgbRelay.setButtonDrawable(getDrawable(R.drawable.bulb_on));
//                                tgbBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_on));
//                            } else {
//                                tgbRelay.setButtonDrawable(getDrawable(R.drawable.bulb_off));
//                                tgbBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_off));
//                            }


                        hideProgressDialog();  // progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();

                finalTgb.setChecked(!finalTgb.isChecked());
//                finalTgb.setButtonDrawable(getDrawable(R.drawable.bulb_broken));
                finalTgb.setButtonDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_broken));


                if (!Utility.isConnectedToInternet()) {
                    startActivity(new Intent(MainActivity.this, NoInternetActivity.class));
                    MainActivity.this.finish();
                } else {
                    String msgErr = error.getMessage();
                    if (msgErr == null)
                        msgErr = "عدم دریافت پاسخ از سرویس دهنده";
                    builder.setMessage("خطایی به شرح زیر رخ داده است:" + "\n" + msgErr);
                    createErrorSnakbar().show();
                }

            }
        }); /*{   // baraye metode post in niaz mishe
            Map<String, String> params = new HashMap<>();

            @Override
            public Map<String, String> getParams() {
                params.put("relay", 1 + "");
                //params.put("xxx", 0 );
                return params;
            }
        };*/

        //...

        int timeOut = isAppRunOnInternet() ? 7000 : DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
        //int maxNumRetries = isAppRunOnInternet() ? DefaultRetryPolicy.DEFAULT_MAX_RETRIES : 0;
        int maxNumRetries = 0;

        request.setRetryPolicy(new DefaultRetryPolicy(timeOut, maxNumRetries,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }

    public void onMenuRefreshStatusClicked() {
        progressDialog.setMessage("در حال بروز رسانی وضعیت");
        progressDialog.show();
        //...
        refreshStatus();
    }

    private void refreshStatus() {

        updateUrlTemplate();
        //...

        final String urlFinal = String.format(urlTemplate, "get_status=1");

        StringRequest request = new StringRequest(Request.Method.GET,
                urlFinal,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideProgressDialog();
                        //...

                        String[] split = response.trim().split("\n");
                        if (split.length == 2) {
                            // خروجی بورد ویموس به شکل زیر است
                            //StateRelay:x \n
                            //StateBuiltinLed:x \n

                            int relayState = Integer.parseInt(split[0].split(":")[1]);
                            int builtinLedState = Integer.parseInt(split[1].split(":")[1]);
                            //...
                            //tgbRelay.setChecked(relayState != 0);
                            //tgbBuiltinLed.setChecked(builtinLedState != 0);

                            if (relayState == 0) {
                                tgbRelay.setChecked(false);
//                                tgbRelay.setButtonDrawable(getDrawable(R.drawable.bulb_off));
                                tgbRelay.setButtonDrawable(
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_off));
                            } else {
                                tgbRelay.setChecked(true);
//                                tgbRelay.setButtonDrawable(getDrawable(R.drawable.bulb_on));
                                tgbRelay.setButtonDrawable(
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_on));
                            }

                            if (builtinLedState == 0) {
                                tgbBuiltinLed.setChecked(false);
//                                tgbBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_off));
                                tgbBuiltinLed.setButtonDrawable(
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_off));
                            } else {
                                tgbBuiltinLed.setChecked(true);
//                                tgbBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_on));
                                tgbBuiltinLed.setButtonDrawable(
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_on));
                            }

                            //...
                            if (relayState == 0 && builtinLedState == 0) {
                                tgbRelayAndBuiltinLed.setChecked(false);
//                                tgbRelayAndBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_off));
                                tgbRelayAndBuiltinLed.setButtonDrawable(
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_off));
                            } else if (relayState == 1 && builtinLedState == 1) {
                                tgbRelayAndBuiltinLed.setChecked(true);
//                                tgbRelayAndBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_on));
                                tgbRelayAndBuiltinLed.setButtonDrawable(
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_on)
                                );
                            } else {
//                                tgbRelayAndBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_on_off));
                                tgbRelayAndBuiltinLed.setButtonDrawable(
                                        ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_on_off));
                            }

                        } else {
                            Snackbar.make(coordinatorLayout,
                                    "داده نادرست از سرویس دهنده دریافت شد",
                                    Snackbar.LENGTH_LONG).show();

                            //TODO: LAMPHA RA UPDAE KONAM
                        }

                        //...
                        swipeRefreshLayout.setRefreshing(false);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    } // End of:  public void onResponse(String response)
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressDialog();
                //...
                swipeRefreshLayout.setRefreshing(false);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                if (!Utility.isConnectedToInternet()) {
                    startActivity(new Intent(MainActivity.this, NoInternetActivity.class));
                    MainActivity.this.finish();
                } else {
                    String msgErr = "خطایی به شرح زیر رخ داده است:" + "\n"
                            + error.getMessage() + "\n\n";
                    //...

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        //The reuest has either time out or there is no connection
                        msgErr += "The reuest has either time out or there is no connection";
                    } else if (error instanceof AuthFailureError) {
                        // There was an Authentication Failure while performing the request
                        msgErr += "There was an Authentication Failure while performing the request";
                    } else if (error instanceof ServerError) {
                        //That the server responded with a error response
                        msgErr += "That the server responded with a error response";
                    } else if (error instanceof NetworkError) {
                        //There was network error while performing the request
                        msgErr += "There was network error while performing the request";
                    } else if (error instanceof ParseError) {
                        // The server response could not be parsed
                        msgErr += "The server response could not be parsed";
                    }

                    //...
                    //getDrawable az API 21 be ba'ad kar mikoneh. man min API ra 19 gereftam
                    //tgbRelay.setButtonDrawable(getDrawable(R.drawable.bulb_broken));
                    tgbRelay.setButtonDrawable(
                            ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_broken));

                    //tgbBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_broken));
                    tgbBuiltinLed.setButtonDrawable(
                            ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_broken));

                    //tgbRelayAndBuiltinLed.setButtonDrawable(getDrawable(R.drawable.bulb_broken));
                    tgbRelayAndBuiltinLed.setButtonDrawable(
                            ContextCompat.getDrawable(MainActivity.this, R.drawable.bulb_broken));

                    //...
                    builder.setMessage(msgErr);
                    createErrorSnakbar().show();

                }
            }
        });

        //...

        int timeOut = isAppRunOnInternet() ? 7000 : DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
        //int maxNumRetries = isAppRunOnInternet() ? DefaultRetryPolicy.DEFAULT_MAX_RETRIES : 0;
        int maxNumRetries = 0;

        request.setRetryPolicy(new DefaultRetryPolicy(timeOut, maxNumRetries,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }

    //---------------------------------------------------------------------------------------------

}
