meta-ecam
=========
This is a layer that provides *econ-console-image* with a modified Linux 3.2
kernel and includes the TI DSP drivers for use with the [e-CAM56 37x
GSTIX](http://www.e-consystems.com/5MP-Gumstix-Camera.asp) camera. The camera
driver is not included and will have to be copied from the CD provided with the
camera, but it should insert properly (hence the reason for the modified 3.2
kernel).

Yocto Project
-----
Install Ubuntu in Virtualbox if you don't have it already. Ubuntu 12.04 or
13.04 both work. Either 32-bit or 64-bit should work.

Setup the */bin/sh* symlink. Select no.

    sudo dpkg-reconfigure dash

Install dependencies

    sudo apt-get install build-essential git-core

If you're on a 64-bit system

    sudo apt-get install ia32-libs

Download Yocto Project files

    curl https://dl-ssl.google.com/dl/googlesource/git-repo/repo > ~/repo
    chmod a+x ~/repo
    mkdir ~/yocto
    cd ~/yocto
    ~/repo init -u git://github.com/floft/manifest -m yocto-ecam.xml
    ~/repo sync

Initialize build directory. You'll have to source this every time you want to
start working with bitbake.

    TEMPLATECONF=meta-ecam/conf source poky/oe-init-build-env

Modify *conf/local.conf* to suit your system (e.g. modify max jobs).

TI DSP Drivers
--------------
Downloaded C6000 Code Generation Tools v7.2.7 from [Texas
Instruments](https://www-a.ti.com/downloads/sds_support/TICodegenerationTools/download.htm)
(requires a free account) and copy into the download folder. Run ``touch
ti_cgt_c6000_7.2.7_setup_linux_x86.bin.done`` in the same directory.

Build everything
----------------
Build a console image including the TI DSP drivers that is compatible with the
e-CAM56 37x GSTIX driver.

    bitbake ecam-console-image

You'll probably get some warning messages from meta-ti. If you're on 64-bit,
make sure you have ia32-libs, but it'll still warn if you have it. And then,
there will be a few license warnings and QA issues.

    WARNING: TI installer requires 32bit glibc libraries for proper operation
    run 'yum install glibc.i686' on Fedora or 'apt-get install ia32-libs' on Ubuntu/Debian
    WARNING: ti-cgt6x: No generic license file exists for: TI in any provider
    WARNING: QA Issue: ti-cgt6x: Files/directories were installed but not shipped
    WARNING: The recipe is trying to install files into a shared area when those files already exist. Those files are:
    ...

U-Boot Environment
------------------
Kernel arguments for use without the camera driver:

    setenv optargs 'mem=99M@0x80000000 mem=397M@0x88000000'

For use with the camera driver:

    setenv optargs 'mem=99M@0x80000000 mem=109M@0x88000000 mem=256M@0x90000000'

And, something like this including the ${optargs} if not already there

    setenv mmcargs 'setenv bootargs console=${console} ${optargs} mpurate=${mpurate} vram=${vram} omapfb.mode=dvi:${dvimode} omapdss.def_disp=${defaultdisplay} root=${mmcroot} rootfstype=${mmcrootfstype}'


#### Memory Layout
It works to leave the drivers in their default locations. Below is the memory
layout used above for a 512 MiB Gumstix. If you want to change the cmem memory
locations, modify */usr/share/ti/gst/omap3530/loadmodules.sh* and the kernel
argument in */lib/systemd/system/gstti-init.service*.

    linux    99M@0x80000000 - 0x86300000 at 0M
    cmem     16M@0x86300000 - 0x87300000 at 99M
    linux   109M@0x88000000 - 0x8e000000 at 115M
    camera   32M@0x8e000000 - 0x8fffffff at 224M
    linux   256M@0x90000000 - 0xa0000000 at 256M

Run Test
--------
Test the TI drivers

    gst-launch videotestsrc num-buffers=100 ! 'video/x-raw-yuv,width=1280,height=720,format=(fourcc)UYVY' ! TIVidenc1 codecName=h264enc engineName=codecServer ! filesink location=sample.264

Test the camera driver

    ln -s /home/root/v4l2_driver.ko /lib/modules/`uname -r`/v4l2_driver.ko
    depmod -a
    modprobe v4l2_driver # twice...?
    gst-launch -v v4l2src num-buffers=100 ! video/x-raw-yuv,width=640,height=480,framerate=30/1 ! TIVidenc1 codecName=h264enc engineName=codecServer ! avimux ! filesink location=video.avi


Qemu (optional)
---------------
Build Qemu for ARM if you want to test the image before putting it on an SD card.

    sudo apt-get install libtool libpixman-1-0 libpixman-1-dev
    git clone git://git.linaro.org/qemu/qemu-linaro.git
    cd qemu-linaro
    git submodule update --init dtc
    ./configure --target-list=arm-softmmu
    make -j4
    sudo make install

Create image

    chmod +x ~/yocto/poky/meta-ecam/scripts/qemumkimg.sh
    pushd tmp/deploy/images
    sudo ~/yocto/poky/meta-ecam/scripts/qemumkimg.sh sd.img MLO u-boot.img uImage ecam-console-image-overo.tar.xz $(ls modules*.tgz | tail -n 1)

Set yourself as the owner since you don't want to run qemu as root.

    sudo chown $(id -nu):$(id -nu) sd.img

Emulate the system, note that the keyboard and mouse probably won't work.

    /usr/local/bin/qemu-system-arm -M overo -m 512 -sd sd.img -clock unix -serial stdio -device usb-mouse -device usb-kbd

Deploy
------
In *~/yocto/build/tmp/deploy/images/* you should see some files like *MLO*,
*u-boot.img*, *uImage*, *ecam-console-image-\*.tar.xz*, and *modules-\*.tgz*
(most of which will be symlinks). Create a fat32 boot partion on an SD card and
copy *MLO*, *u-boot.img*, and *uImage* onto it. Extract the other two into a
larger ext3 second partition. You can read the [Gumstix
documentation](http://gumstix.org/create-a-bootable-microsd-card.html) for more
in-depth information. You may want to reset the bootloader environment (bottom
of linked page) to the defaults in the newly compiled one. Or, if the
bootloader doesn't even load, you can try [a precompiled Gumstix
one](http://cumulus.gumstix.org/images/angstrom/factory/2011-08-30-1058/u-boot.bin).

Resources
---------
[Gumstix Emulation for QEMU](http://wiki.gumstix.org/index.php?title=Gumstix_Emulation_for_QEMU)  
[Using the DSP on Gumstix with Yocto](http://www.sleepyrobot.com/?p=210)  
[Gumstix Repo Manifests for the Yocto Project](https://github.com/gumstix/Gumstix-YoctoProject-Repo)
