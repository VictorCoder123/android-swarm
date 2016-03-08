# This dockerfile will build an image that can run a full android emulator + the visual emulator over VNC.
# Based on the work by https://github.com/ConSol/docker-headless-vnc-container.
# KVM needs to be enabled by setting BIOS virtualization options, otherwise emulator will be slow.
# Run `sudo apt-get install cpu-checker && kvm-ok` to check if KVM is enabled under current machine

FROM ubuntu:14.04

MAINTAINER Qishen Zhang "privatebiktop@gmail.com"

ENV DEBIAN_FRONTEND noninteractive
ENV DISPLAY :1
ENV NO_VNC_HOME /root/noVNC
ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW vncpassword
ENV JAVA_HOME /usr/lib/jvm/java-7-oracle

# Install core dependencies
RUN apt-get update && apt-get upgrade -y
RUN apt-get install -y supervisor vim xfce4 vnc4server wget unzip nano git

# Install noVNC - HTML5 based VNC viewer
RUN mkdir -p $NO_VNC_HOME/utils/websockify \
    && wget -qO- https://github.com/kanaka/noVNC/archive/master.tar.gz | tar xz --strip 1 -C $NO_VNC_HOME \
    &&  wget -qO- https://github.com/kanaka/websockify/archive/v0.7.0.tar.gz | tar xz --strip 1 -C $NO_VNC_HOME/utils/websockify \
    && chmod +x -v /root/noVNC/utils/*.sh

# Install Java 7
RUN sudo apt-get install -y software-properties-common python3-software-properties
RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN apt-get install -y oracle-java7-installer
RUN rm -rf /var/lib/apt/lists/* && rm -rf /var/cache/oracle-jdk7-installer

# Install KVM dependencies for android acceleration and add user groups
RUN apt-get update
RUN apt-get install qemu-kvm libvirt-bin ubuntu-vm-builder bridge-utils -y
RUN adduser `id -un` libvirtd
RUN adduser `id -un` kvm
RUN service libvirt-bin start

# Setup specifics for android support - glx drivers etc. Otherwise adb: Not found
RUN apt-get install libgl1-mesa-dev -y
RUN apt-get install libc6-i386 lib32stdc++6 lib32gcc1 lib32ncurses5 lib32z1 -y

# Install Android SDK
RUN cd /root && wget -nv http://dl.google.com/android/android-sdk_r23.0.2-linux.tgz && tar xfo android-sdk_r23.0.2-linux.tgz --no-same-permissions && chmod -R a+rX android-sdk-linux
RUN rm -rf /root/android-sdk_r23.0.2-linux.tgz

# Add android tools into PATH variable
ENV ANDROID_HOME /root/android-sdk-linux
ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools

# Install Android tools
RUN echo y | android update sdk --filter tools --no-ui --force -a
RUN echo y | android update sdk --filter platform-tools --no-ui --force -a
RUN echo y | android update sdk --filter platform --no-ui --force -a
RUN echo y | android update sdk --filter build-tools-21.0.1 --no-ui -a

# Install android images
RUN echo y | android update sdk --filter sys-img-x86-android-19 --no-ui -a

# Create and run android virtual device
RUN echo n | android create avd -n myandroid-19 -t android-19

# Update ADB and start adb server
RUN echo y | android update adb
#RUN adb start-server

# Install Apk into AVD
ADD apk /root/apk
#RUN adb -s myandroid install /root/apk/Lockito.apk

# xvnc server porst, if $DISPLAY=:1 port will be 5901
EXPOSE 5901
# novnc web port
EXPOSE 6901

# Import external files with excuteable permission
ADD .vnc /root/.vnc
ADD .config /root/.config
ADD Desktop /root/Desktop
ADD scripts /root/scripts
RUN chmod +x /root/.vnc/xstartup /etc/X11/xinit/xinitrc /root/scripts/*.sh /root/Desktop/*.desktop

# Add entry point
CMD ["/root/scripts/container_startup.sh", "--tail-log"]






