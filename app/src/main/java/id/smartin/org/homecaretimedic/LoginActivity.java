package id.smartin.org.homecaretimedic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.shaishavgandhi.loginbuttons.FacebookButton;
import com.shaishavgandhi.loginbuttons.GooglePlusButton;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import id.smartin.org.homecaretimedic.config.Constants;
import id.smartin.org.homecaretimedic.config.PermissionConst;
import id.smartin.org.homecaretimedic.config.RequestCode;
import id.smartin.org.homecaretimedic.manager.HomecareSessionManager;
import id.smartin.org.homecaretimedic.model.User;
import id.smartin.org.homecaretimedic.model.responsemodel.LoginResponse;
import id.smartin.org.homecaretimedic.model.utilitymodel.ChatUser;
import id.smartin.org.homecaretimedic.tools.AesUtil;
import id.smartin.org.homecaretimedic.tools.ViewFaceUtility;
import id.smartin.org.homecaretimedic.tools.restservice.APIClient;
import id.smartin.org.homecaretimedic.tools.restservice.UserAPIInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.facebook.appevents.AppEventsLogger;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "[LoginActivity]";

    @BindView(R.id.btnSignIn)
    Button signIn;
    @BindView(R.id.btnGoogleSignIn)
    ImageButton btnGoogleSignIn;
    @BindView(R.id.btnFacebookSignIn)
    ImageButton btnFacebookSignIn;
    @BindView(R.id.emailAddress)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;
    @BindView(R.id.btnSignup)
    Button signUp;
    @BindView(R.id.forgotPassword)
    TextView forgotPassword;
    @BindView(R.id.orWithText)
    TextView orWithText;

    private UserAPIInterface userAPIInterface;

    private HomecareSessionManager homecareSessionManager;
    private User user;
    private boolean checkPermission = false;
    private SweetAlertDialog progressDialog;

    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager callbackManager;
    private DatabaseReference mUsersDBref;

    private static final int MY_PERMISSIONS_REQUEST = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

        userAPIInterface = APIClient.getClient().create(UserAPIInterface.class);
        homecareSessionManager = new HomecareSessionManager(this, getApplicationContext());
        /*
        if (homecareSessionManager.isLogin()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        */
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewFaceUtility.hideKeyboard(LoginActivity.this, username);
                ViewFaceUtility.hideKeyboard(LoginActivity.this, password);
                Log.e(TAG, "Sudah pencet tombol sign in");
                if(password.getText().toString().equals("") && username.getText().toString().equals("")){
                    Snackbar.make(mainLayout, "Silahkan isi email dan password akun anda", Snackbar.LENGTH_LONG).show();
                }
                else{
                    cekMethod(username.getText().toString());
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newinten = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(newinten);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newinten = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(newinten);
            }
        });
        setPermission();
        googleLoginInit();
        facebookLoginInit();
        setFonts();
    }

    public void setFonts() {
        ArrayList<TextView> tvs = new ArrayList<>();
        tvs.add(signIn);
        tvs.add(username);
        tvs.add(orWithText);
        tvs.add(signUp);
        tvs.add(password);
        tvs.add(forgotPassword);
        ViewFaceUtility.applyFonts(tvs, this, "fonts/Dosis-Medium.otf");
    }

    private void googleLoginInit() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // doLoginFirebase(user, "google");
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    signIn();
                    Log.d(TAG, "Sign in");
                } else {
                    signOut();
                    Log.d(TAG, "Signed Out");
                }
            }
        });
    }

    private void facebookLoginInit() {
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        btnFacebookSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    signInFb();
                    Log.d(TAG, "Sign in");
                } else {
                    signOut();
                    Log.d(TAG, "Signed Out");
                }
            }
        });
    }

    private void gotoFirebaseSignUpPage(FirebaseUser fbaseuser, String type) {
        User user = new User();
        String name[] = fbaseuser.getDisplayName().split(" ");
        String elaborateLastName = "";
        for (int i = 0; i < name.length; i++) {
            if (i == 0) {
                user.setFrontName(name[i]);
            } else if (i == 1) {
                user.setMiddleName(name[i]);
            } else if (i > 1) {
                elaborateLastName = elaborateLastName + " " + name[i];
                user.setLastName(elaborateLastName);
            }
        }
        user.setPhotoPath(fbaseuser.getPhotoUrl().getPath());
        user.setPhoneNumber(fbaseuser.getPhoneNumber());
        user.setEmail(fbaseuser.getEmail());
        if (type.equals("facebook")) {
            user.setFirebaseIdFacebook(fbaseuser.getUid());
        } else if (type.equals("google")) {
            user.setFirebaseIdGoogle(fbaseuser.getUid());
        }
        Intent intent = new Intent(this, FUserSignUpActivity.class);
        intent.putExtra("fbase_user", user);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RequestCode.REQUEST_GOOGLE_SIGN_IN);
    }

    private void signInFb() {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("public_profile", "user_friends", "email")
        );
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void firebaseAuthWithFacebook(AccessToken accessToken) {
        Log.d(TAG, "firebaseAuthWithFacebook:" + accessToken.getToken());
        openProgress("Loading...", "Proses verifikasi!");
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                closeProgress();
                if (!task.isSuccessful()) {
                    Log.e(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(getApplicationContext(), "Autentikasi facebook gagal!", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        doLoginFirebase(user, "facebook");
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                    //Toast.makeText(getApplicationContext(), "Autentikasi google gagal!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        openProgress("Loading...", "Proses verifikasi!");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        closeProgress();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Autentikasi google gagal!", Toast.LENGTH_LONG).show();
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // User is signed in
                                doLoginFirebase(user, "google");

                                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                            } else {
                                // User is signed out
                                Log.d(TAG, "onAuthStateChanged:signed_out");
                            }
                            //Toast.makeText(getApplicationContext(), "Autentikasi google gagal!", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RequestCode.REQUEST_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, PermissionConst.listPermission, MY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                checkPermission = true;
        }
    }

    private void openProgress(String title, String content) {
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.dismiss();
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText(title);
        progressDialog.setContentText(content);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
    }

    private void closeProgress() {
        progressDialog.dismiss();
    }

    public void doLoginWithEmail() {
        String emailStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        if (emailStr.isEmpty() || emailStr.equals("") || emailStr.equals(null)) {
            Snackbar.make(mainLayout, "Email kosong. Harap diisi!", Snackbar.LENGTH_LONG).show();
        } else {
            if (passwordStr.isEmpty() || passwordStr.equals("") || passwordStr.equals(null)) {
                Snackbar.make(mainLayout, "Password kosong. Harap diisi!", Snackbar.LENGTH_LONG).show();
            } else {
                mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    doLoginFirebase(user, "email");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());

                                }
                                // ...
                            }
                        });
            }
        }
    }

    private void cekMethod(String email){
        Call<ResponseBody> responseCall = userAPIInterface.checkCaregiverPasswordIsNullOrNot(email);

        responseCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String password = response.body().string();
                        if(password.equals("false")){
                            doLoginWithEmail();
                        }
                        else{
                            if (password.equals("true,g")){
                                signIn();
                            } else {
                                signInFb();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Snackbar.make(mainLayout, "Login gagal", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(mainLayout, getResources().getString(R.string.network_problem), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void doLogin() {
        openProgress("Loading...", "Proses Login!");

        String shahex = AesUtil.Encrypt(password.getText().toString());
        Call<LoginResponse> responseCall = userAPIInterface.loginUser(username.getText().toString(), shahex);
        responseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {
                closeProgress();
                if (response.code() == 200) {
                    mAuth.signInAnonymously()
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            String provider = user.getProviders().get(0);
                                            Log.i(TAG, "USER LOGIN WITH " + provider);
                                            // User is signed in
                                            ArrayList<String> defaultRoom = new ArrayList<String>();
                                            defaultRoom.add("home");
                                            AdminListActivity.user = new ChatUser();
                                            AdminListActivity.user.setEmail(user.getEmail());
                                            AdminListActivity.user.setId(user.getUid());
                                            AdminListActivity.user.setNickname(user.getDisplayName());
                                            AdminListActivity.user.setOnline(true);
                                            AdminListActivity.user.setUserType(Constants.CHAT_ROLE_ME);
                                            AdminListActivity.user.setRoom(defaultRoom);
                                            isExistOnFirebaseDB(AdminListActivity.user);
                                            Log.i(TAG, response.body().getUser().toString());
                                            Log.i(TAG, "NEW TOKEN " + response.body().getToken());
                                            gotoMainPage(response.body().getUser(), response.body().getToken());
                                        }
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInAnonymously:success");

                                    } else {
                                        Log.d(TAG, "signInAnonymously:success");
                                        // If sign in fails, display a message to the user.
                                    }

                                    // ...
                                }
                            });
                    Log.i(TAG, response.body().getUser().toString());
                    Log.i(TAG, "NEW TOKEN " + response.body().getToken());
                    gotoMainPage(response.body().getUser(), response.body().getToken());
                } else if (response.code() == 401) {
                    Log.i(TAG, response.raw().toString());
                    Snackbar.make(mainLayout, getResources().getString(R.string.login_failed_unauthorized), Snackbar.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    Snackbar.make(mainLayout, getResources().getString(R.string.login_failed_user_not_found), Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mainLayout, getResources().getString(R.string.login_err_unknown), Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(mainLayout, getResources().getString(R.string.network_problem), Snackbar.LENGTH_LONG).show();
                call.cancel();
            }
        });
    }

    public void doLoginFirebase(final FirebaseUser user, final String type) {
        openProgress("Loading...", "Proses Login!");
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            Log.i(TAG, "idToken : " + idToken);
                            // Send token to your backend via HTTPS
                            Call<LoginResponse> responseCall = userAPIInterface.loginUserWithFirebaseToken(idToken, type);
                            responseCall.enqueue(new Callback<LoginResponse>() {
                                @Override
                                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                    closeProgress();

                                    Log.i(TAG, response.message());
                                    if (response.code() == 200) {
                                        Log.i(TAG, response.body().getUser().toString());
                                        Log.i(TAG, "NEW TOKEN " + response.body().getToken());
                                        gotoMainPage(response.body().getUser(), response.body().getToken());
                                    } else if (response.code() == 401) {
                                        Log.i(TAG, response.raw().toString());
                                        gotoFirebaseSignUpPage(user, type);
                                        Snackbar.make(mainLayout, getResources().getString(R.string.login_failed_unauthorized), Snackbar.LENGTH_LONG).show();
                                    } else if (response.code() == 404) {
                                        Snackbar.make(mainLayout, getResources().getString(R.string.login_failed_user_not_found), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(mainLayout, getResources().getString(R.string.login_err_unknown), Snackbar.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<LoginResponse> call, Throwable t) {
                                    progressDialog.dismiss();
                                    t.printStackTrace();
                                    Snackbar.make(mainLayout, getResources().getString(R.string.network_problem), Snackbar.LENGTH_LONG).show();
                                    call.cancel();
                                }

                            });
                        } else {
                            // Handle error -> task.getException();

                        }
                    }
                });

    }

    public void gotoMainPage(User usr, String token) {
        homecareSessionManager.createLoginSession(usr, token);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void checkLogin() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String provider = user.getProviders().get(0);
            Log.i(TAG, "USER LOGIN WITH " + provider);
            // User is signed in
            ArrayList<String> defaultRoom = new ArrayList<String>();
            defaultRoom.add("home");
            AdminListActivity.user = new ChatUser();
            AdminListActivity.user.setEmail(user.getEmail());
            AdminListActivity.user.setId(user.getUid());
            AdminListActivity.user.setNickname(user.getDisplayName());
            AdminListActivity.user.setOnline(true);
            AdminListActivity.user.setUserType(Constants.CHAT_ROLE_ME);
            AdminListActivity.user.setRoom(defaultRoom);
            isExistOnFirebaseDB(AdminListActivity.user);
            String providerStr = provider.replace(".com", "");
            if (!providerStr.equalsIgnoreCase("google") && !providerStr.equalsIgnoreCase("facebook")) {
                providerStr = "email";
            }
            doLoginFirebase(user, providerStr);
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    private void isExistOnFirebaseDB(final ChatUser user) {
        mUsersDBref = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference userNameRef = mUsersDBref.child(user.getId());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    createUserInDb(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);

    }

    private void createUserInDb(ChatUser user) {
        mUsersDBref = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDBref.child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    //error
                    Log.i(TAG, "ERROR ");
                    //Toast.makeText(SignUpActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "SIGNING OK");
                }
            }
        });
    }

}
