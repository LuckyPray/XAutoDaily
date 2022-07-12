package me.teble.xposed.autodaily;

interface IUserService {

    void destroy() = 16777114; // Destroy method defined by Shizuku server

    void exit() = 1; // Exit method defined by user

    boolean isRunning() = 2;

    String execShell(String shell) = 3; // Execute shell command
}