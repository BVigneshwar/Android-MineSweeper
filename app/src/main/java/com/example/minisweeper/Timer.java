package com.example.minisweeper;


import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Timer implements Serializable {
    public static final long SerialVersionUID = 1L;
    long best_time;
    transient Context context;

    Timer(Context context, long best_time){
        this.context = context;
        this.best_time = best_time;
    }
    void storeBestTime(){
        String filePath = context.getFilesDir().getPath().toString() + "/timer.txt";
        try(FileOutputStream fout = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fout);){
            out.writeObject(this);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    long retrieveBestTime(){
        String filePath = context.getFilesDir().getPath().toString() + "/timer.txt";
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));) {
            Timer timer = (Timer) in.readObject();
            return timer.best_time;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
