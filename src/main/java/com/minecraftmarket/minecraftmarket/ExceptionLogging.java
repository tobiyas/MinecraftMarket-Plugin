package com.minecraftmarket.minecraftmarket;

import org.bukkit.Bukkit;

import java.beans.ExceptionListener;

public abstract class ExceptionLogging implements ExceptionListener {

    @Override
    public void exceptionThrown(Exception e){
        Bukkit.broadcast("Error has been caught!", e.toString());
    }
}
