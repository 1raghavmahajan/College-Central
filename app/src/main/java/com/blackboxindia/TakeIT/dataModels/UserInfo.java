package com.blackboxindia.TakeIT.dataModels;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class UserInfo implements Parcelable {

    //region Variables

    private String profileIMG;
    private String uID;
    private String name;
    private String email;
    private String address;
    private String phone;
    private ArrayList<String> userAdKeys;

    //endregion

    //region Constructors

    public UserInfo(){
        userAdKeys = new ArrayList<>();
    }

    public UserInfo(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);

        byte[] bytes = new byte[200];
        in.readByteArray(bytes);
        // the order needs to be the same as in writeToParcel() method
        this.uID = data[0];
        this.name = data[1];
        this.email = data[2];
        this.address = data[3];
        this.phone = data[4];
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
            Log.i("UserInfo: YOYO", e.toString());
        }
    }

    //endregion

    public void setData(String name, String email, String address, String phone) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    public void newUser(String password, final Context context) {
        NetworkMethods net = new NetworkMethods(context);
        net.Create_Account(this,password, new onLoginListener() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    ((MainActivity) context).UpdateUI(userInfo,true,true);
                    ((MainActivity) context).createSnackbar("Account Created Successfully",Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (e != null) {
                    if (e.getMessage().contains("network"))
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void login(final String email, final String password, final Context context, final Boolean saveCred) {

        final ProgressDialog dialog = ProgressDialog.show(context, "Logging in..", "", true, false);
        NetworkMethods net = new NetworkMethods(context);
        net.Login(email, password, new onLoginListener() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                dialog.cancel();

                if(saveCred) {
                    UserCred userCred = new UserCred(email, password);
                    userCred.save_cred(context);
                }
                ((MainActivity) context).UpdateUI(userInfo, true, true);
            }

            @Override
            public void onFailure(Exception e) {
                if (e != null) {
                    dialog.cancel();
                    if (e.getMessage().contains("network")) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (e.getMessage().contains("password") && e.getMessage().contains("invalid")) {
                        Toast.makeText(context, "Invalid Password.", Toast.LENGTH_SHORT).show();
                    } else if (e.getMessage().contains("no user record")) {
                        Toast.makeText(context, "Invalid Email ID.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void addUserAd(String userAdKey) {
        if(userAdKeys==null)
            userAdKeys = new ArrayList<>();
        userAdKeys.add(userAdKey);
    }

    public void removeUserAd(String userAdKey) {
        if(userAdKeys.contains(userAdKey))
            userAdKeys.remove(userAdKey);
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

    public boolean isIDValid(String id) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(id).matches();
    }

    //region Parcelable

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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

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

    public ArrayList<String> getUserAdKeys() {
        return userAdKeys;
    }

    public String getProfileIMG() {
        return profileIMG;
    }

    public void setProfileIMG(String profileIMG) {
        this.profileIMG = profileIMG;
    }

    //endregion

}
