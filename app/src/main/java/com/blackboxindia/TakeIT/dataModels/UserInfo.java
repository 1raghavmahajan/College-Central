package com.blackboxindia.TakeIT.dataModels;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class UserInfo {

    //region Variables

    private String profileIMG;
    private String uID;
    private String name;
    private String email;
    private String address;
    private String phone;

    private ArrayList<String> userAdKeys;
    private String collegeName;

    //endregion

    //region Constructors

    public UserInfo(){
        uID = null;
        userAdKeys = new ArrayList<>();
    }

    public UserInfo createCopy() {
        UserInfo userInfo = new UserInfo();
        userInfo.setData(name,email,address,phone);
        userInfo.setuID(uID);
        userInfo.setProfileIMG(profileIMG);
        for(String s:userAdKeys){
            userInfo.addUserAd(s);
        }
        if(collegeName!=null)
            userInfo.setCollegeName(collegeName);
        return userInfo;
    }

    //endregion

    public void setData(String name, String email, String address, String phone) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        collegeName = "IIT Indore";
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

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    //endregion

}
