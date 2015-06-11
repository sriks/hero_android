package com.deviceworks.hero.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.deviceworks.hero.R;

/**
 * Signup fragment
 */
public class HRSignupFragment extends Fragment implements View.OnClickListener {

    public interface OnSignupListener {
        void onSignup(String name, String email, String password);
    }

    OnSignupListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_signup, null);
        v.findViewById(R.id.next).setOnClickListener(this);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mListener = (OnSignupListener)activity;
        } catch(ClassCastException e) {
            throw new RuntimeException("Host activity should implement HRSignupFragment.OnSignupListener");
        }
        super.onAttach(activity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                onNextButtonClicked(
                        getTextForResId(R.id.name),
                        getTextForResId(R.id.email),
                        getTextForResId(R.id.password));
                break;
        }
    }

    protected void onNextButtonClicked(String name, String userid, String password) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(userid) || TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
        } else {
            mListener.onSignup(name, userid, password);
        }
    }

    private String getTextForResId(int resId) {
        return ((TextView)getView().findViewById(resId)).getText().toString();
    }

}
