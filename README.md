# meta-ecam
This is a layer to setup the TI drivers and Linux 3.2 kernel to be compatible
with the [e-CAM56 37x
GSTIX](http://www.e-consystems.com/5MP-Gumstix-Camera.asp) camera. The camera
driver is not included and will have to be copied from the CD provided with the
camera, but it should insert properly (hence the reason for the modified 3.2
kernel).

## Yocto Project
Install [Ubuntu 10.04 32-bit](http://releases.ubuntu.com/lucid/) in Virtualbox
(see DVSDK section if you want a different version) and disable swap;
otherwise, your host will die when thrashing if you set bitbake to use too many
threads. Give it as many cores as you have, quite a bit of disk space (e.g. 80
GiB), and a lot of memory (e.g. 4 GiB) depending on how many cores have and
jobs you set up.

Setup */bin/sh* symlink. Select no.

    sudo dpkg-reconfigure dash

Install dependencies

    sudo apt-get install build-essential git-core pkg-config diffstat texi2html texinfo gawk chrpath subversion libncurses5-dev uboot-mkimage

Download recipes

    git clone -b dylan git://git.yoctoproject.org/poky.git poky-dylan
    cd ~/poky-dylan
    git clone -b dylan git://git.openembedded.org/meta-openembedded
    git clone -b dylan git://github.com/gumstix/meta-gumstix
    git clone git://github.com/beagleboard/meta-beagleboard
    git clone git://github.com/Freescale/meta-fsl-arm.git
    git clone git://github.com/Freescale/meta-fsl-arm-extra.git
    git clone git://github.com/Pansenti/meta-pansenti
    git clone -b pansenti-base git://github.com/floft/meta-ecam.git
    git checkout 64273e5
    ( cd meta-openembedded; git checkout 13ae510 )
    ( cd meta-gumstix; git checkout 15d9cf4 )
    ( cd meta-beagleboard; git checkout 593a05e )
    ( cd meta-fsl-arm; git checkout fdf3fe6 )
    ( cd meta-fsl-arm-extra; git checkout 4fa3141 )
    ( cd meta-pansenti; git checkout a76933b6 )

Setup environment

    source oe-init-build-env ~/yocto

Copy example files

    cp ~/poky-dylan/meta-ecam/conf/local.conf.sample conf/local.conf
    cp ~/poky-dylan/meta-ecam/conf/bblayers.conf.sample conf/bblayers.conf

Modify BB\_NUMBER\_THREADS and PARALLEL\_MAKE in local.conf to match your CPU count.

## DVSDK
Download from [Texas Instruments](http://www.ti.com/tool/linuxdvsdk-dm37x),
which requires an account. Install it in */usr/local/dvsdk* (or modify the
recipe file). And optionally do ``git init; git add -A; git commit -a -m
"initial commit"`` to allow easily reverting all changes to the > 6 GiB DVSDK
while compiling the TI drivers. If you really, really want to attempt this on
something other than Ubuntu 10.04 32-bit, then run ``sudo
./dvsdk_dm3730-evm_04_03_00_06_setuplinux --forcehost``.

Make yourself the owner since you don't want to run bitbake as root.

    sudo chown -R $(id -nu):$(id -nu) /usr/local/dvsdk

Modify *${TMPDIR}/work/overo-poky-linux-gnueabi/linux-ecam/3.2-r1/git* in
recipe file if it is incorrect.

## Build Everything

    bitbake ecam-console-image

## U-Boot Environment
Without the camera driver

    setenv optargs 'mem=55M@0x80000000 mem=384M@0x88000000'

With the camera driver

    setenv optargs 'mem=55M@0x80000000 mem=96M@0x88000000 mem=256M@0x90000000'

And, something like this including the ${optargs} if not already there

    setenv mmcargs 'setenv bootargs console=${console} ${optargs} mpurate=${mpurate} vram=${vram} omapfb.mode=dvi:${dvimode} omapdss.def_disp=${defaultdisplay} root=${mmcroot} rootfstype=${mmcrootfstype}'

### Memory Layout
It works to leave the drivers in their default locations. Below is the memory
layout used above for a 512 MiB Gumstix.

    linux    55M@0x80000000 - 0x83700000
    DSP  73M@0x83700000 - 0x88000000
        cmem     34M@0x83700000 - 0x85900000
        codec    39M@0x85900000 - 0x88000000
    linux    96M@0x88000000 - 0x8e000000
    camera   32M@0x8e000000 - 0x8fffffff
    linux   256M@0x90000000 - 0xa0000000

## Run Test
Test the TI drivers

    gst-launch videotestsrc num-buffers=100 ! 'video/x-raw-yuv,width=1280,height=720,format=(fourcc)UYVY' ! TIVidenc1 codecName=h264enc engineName=codecServer ! filesink location=sample.264

Transcode it to something you can watch in VLC.

    ffmpeg -i sample.264 -qscale 0 sample.avi

## Qemu (optional)
Build Qemu for ARM if you want to test the image before putting it on an SD card.

    sudo apt-get install libtool libpixman-1-0 libpixman-1-dev
    git clone git://git.linaro.org/qemu/qemu-linaro.git
    cd qemu-linaro
    git submodule update --init dtc
    ./configure --target-list=arm-softmmu
    make -j8
    sudo make install

Create image

    chmod +x ~/poky-dylan/meta-ecam/scripts/qemumkimg.sh
    pushd tmp/deploy/images
    sudo ~/poky-dylan/meta-ecam/scripts/qemumkimg.sh sd.img MLO u-boot.img uImage ecam-console-image-overo.tar.xz $(ls modules*.tgz | tail -n 1)

Set yourself as the owner since you don't want to run qemu as root.

    sudo chown $(id -nu):$(id -nu) sd.img

Emulate the system

    /usr/local/bin/qemu-system-arm -M overo -m 512 -sd sd.img -clock unix -serial stdio -device usb-mouse -device usb-kbd

## Resources
[Gumstix Emulation for QEMU](http://wiki.gumstix.org/index.php?title=Gumstix_Emulation_for_QEMU)  
[Building Gumstix images with the Yocto Project](http://jumpnowtek.com/index.php?option=com_content&view=article&id=85&Itemid=97)
