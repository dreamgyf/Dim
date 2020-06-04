package com.dreamgyf.dim.login.view;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dreamgyf.dim.main.view.MainActivity;
import com.dreamgyf.dim.R;
import com.dreamgyf.dim.base.http.exception.HttpRespException;
import com.dreamgyf.dim.base.http.exception.NetworkConnectException;
import com.dreamgyf.dim.base.mqtt.exception.MqttConnectException;
import com.dreamgyf.dim.base.mvp.activity.BaseActivity;
import com.dreamgyf.dim.login.listener.OnLoginListener;
import com.dreamgyf.dim.login.model.LoginModel;
import com.dreamgyf.dim.login.presenter.LoginPresenter;

import retrofit2.HttpException;

public class LoginActivity extends BaseActivity<LoginModel, LoginActivity, LoginPresenter> implements ILoginView {

	private LoginPresenter mLoginPresenter;

	private Handler handler = new Handler();

	private RelativeLayout loginButton;

	private EditText usernameText;

	private EditText passwordText;

	private ImageView staticLogin;

	private ImageView animLogin;

	private Animatable loadingAnim;

	private boolean isLogining = false;

	@NonNull
	@Override
	public LoginPresenter bindPresenter() {
		return new LoginPresenter(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mLoginPresenter = getPresenter();
		mLoginPresenter.setOnLoginListener(new OnLoginListener() {
			@Override
			public void onLoginSuccess(String username, String passwordSha256) {
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onLoginFailed(Throwable t) {
				Toast toast = Toast.makeText(LoginActivity.this, null, Toast.LENGTH_SHORT);
				if (t instanceof NetworkConnectException || t instanceof HttpRespException || t instanceof HttpException || t instanceof MqttConnectException) {
					toast.setText(t.getMessage());
				} else {
					toast.setText("未知错误");
				}
				toast.show();
				stopLoadingAnim();
			}
		});

		initEditText();
		initLoginButton();

	}

	private void initEditText() {
		usernameText = findViewById(R.id.username);
		passwordText = findViewById(R.id.password);
		passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					String username = usernameText.getText().toString();
					String password = passwordText.getText().toString();
					mLoginPresenter.login(username, password);
					startLoadingAnim();
				}
				return false;
			}
		});
	}

	private void initLoginButton() {
		loginButton = findViewById(R.id.login);
		staticLogin = findViewById(R.id.staticLogin);
		animLogin = findViewById(R.id.animLogin);
		loadingAnim = (Animatable) animLogin.getDrawable();
		//点击登录
		loginButton.setOnClickListener((view) -> {
			String username = usernameText.getText().toString();
			String password = passwordText.getText().toString();
			mLoginPresenter.login(username, password);
			startLoadingAnim();
		});
	}

	private void startLoadingAnim() {
		staticLogin.setVisibility(View.INVISIBLE);
		animLogin.setVisibility(View.VISIBLE);
		loadingAnim.start();
	}

	private void stopLoadingAnim() {
		staticLogin.setVisibility(View.VISIBLE);
		animLogin.setVisibility(View.INVISIBLE);
		loadingAnim.stop();
	}

}
