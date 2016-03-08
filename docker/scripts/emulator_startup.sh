#!/bin/bash

# Start adb server
adb start-server

# Start Android Emulator inside container without GUI
echo "n" | emulator64-x86 -avd myandroid-19 -noaudio -no-window -gpu off -verbose -qemu -enable-kvm

