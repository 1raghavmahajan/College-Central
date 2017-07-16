package com.blackboxindia.PostIT.dataModels;

import java.util.ArrayList;


public class Directory {

    public String name;
    public ArrayList<Directory> folders;
    public ArrayList<String> files;

    public Directory(){
        folders = new ArrayList<>();
        files = new ArrayList<>();
    }

    public Directory(String name){
        this.name = name;
        folders = new ArrayList<>();
        files = new ArrayList<>();
    }

}
