package com.blackboxindia.TakeIT.dataModels;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.networkMethods;
import com.blackboxindia.TakeIT.Network.onResultListener;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserInfo implements Parcelable {

    //region Variables

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
    private Bitmap profile;
    private String uID;
    private String name;
    private String email;

    //endregion
    private String address;
    //region Constructors
    private String phone;

    public UserInfo(){
    }

    public UserInfo(EditText nm, EditText em, EditText add, EditText ph) {
        try {
            name = nm.getText().toString().trim();
            email = em.getText().toString().trim();
            address = add.getText().toString().trim();
            phone = ph.getText().toString().trim();
        }
        catch (NullPointerException e)
        {
            Log.i("YOYO", e.toString());
        }
    }

    public UserInfo(String uID, String name, String email, String address, String phone) {
        this.uID = uID;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    //endregion

    public UserInfo(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.uID = data[0];
        this.name = data[1];
        this.email = data[2];
        this.address = data[3];
        this.phone = data[4];
    }

    public void newUser(String password, final Context context) {

        networkMethods net = new networkMethods(context, new onResultListener() {
            @Override
            public void onSuccess(FirebaseAuth Auth, UserInfo userInfo) {
                if (Auth.getCurrentUser() != null) {
                    Log.i("YOYO", "newUser onSuccess: " + this.toString());

                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.UpdateUIonLogin(userInfo, Auth);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (e != null) {
                    Log.w("YOYO", "new User: onFailure", e);
                    if (e.getMessage().contains("network"))
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        net.Create_Account(this,password);
    }

    //region Validators

    public void login(String email, String password, final Context context) {

        networkMethods net = new networkMethods(context, new onResultListener() {
            @Override
            public void onSuccess(FirebaseAuth Auth, UserInfo userInfo) {
                Log.i("YOYO", "Logged in - OnLogin: " + this.toString());

                MainActivity mainActivity = (MainActivity) context;
                mainActivity.UpdateUIonLogin(userInfo, Auth);
            }

            @Override
            public void onFailure(Exception e) {
                if (e != null) {
                    Log.w("YOYO", "new User: onFailure", e);
                    if (e.getMessage().contains("network"))
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        net.Login(email, password);
    }

    public Bundle validateNewAccountDetails() {
        Bundle result = new Bundle();
        result.putBoolean("ID", isIDValid(email));

        if(name != null && address!=null && phone!=null && result.getBoolean("ID"))
            result.putBoolean("allGood",true);
        else
            result.putBoolean("allGood",false);

        return result;
    }

    //endregion

    //region Parcelable

    public boolean isIDValid(String id) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(id).matches();
    }

    @Override
    public String toString() {
        String allDet = "";

        if (name != null)
            allDet = allDet.concat(" Name: " + name);
        if (email != null)
            allDet = allDet.concat(" Email: " + email);
        if (address != null)
            allDet = allDet.concat(" Address: " + address);
        if (phone != null)
            allDet = allDet.concat(" phone: " + phone);
        if (uID != null)
            allDet = allDet.concat(" uID: " + uID);

        if (allDet.equals(""))
            return "null";
        else
            return allDet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{this.uID,
                this.name,
                this.email,
                this.address,
                this.phone});

    }

    //endregion

    //region Getters and Setters

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    public String getAuthKey() {
//        return authKey;
//    }

    //endregion

}
