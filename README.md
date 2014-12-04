meta-ecam
=========
This is a layer that provides *ecam-console-image* with a modified Linux 3.5.7
kernel and includes the TI DSP drivers for use with the [e-CAM56 37x
GSTIX](http://www.e-consystems.com/5MP-Gumstix-Camera.asp) camera. The camera
driver is will be built along with the 3.5.7 kernel.

You'll probably want to modify this layer and the manifest to better suit your
needs, but hopefully this will be a good starting point for working with this
camera.

Files
-----
Here is a summary of the changes made in the .bbappend files in this layer and
the new files provided in it.

| Files | Description
|:------|:-----
| *conf* | Example config files for 3.5.7 kernel with a few hacks like insane\_skip
| *recipes-core/init-ifupdown* | Changes hostname to "gumstix"
| *recipes-ecam/driver* | Provides the camera driver
| *recipes-ecam/images* | Provides images including the TI and camera drivers
| *recipes-ecam/services* | 10.3.14.15 static IP on USB OTG port, DHCP on eth0
| *recipes-kernel/linux* | Adds kernel modifications for camera
| *recipes-support/opencv* | Apply UYVY grayscale patch (You may want to remove this)
| *recipes-support/ntp* | Change servers and allow large first offset for NTP
| *recipes-ti* | Patch TIImgenc1 for [multiple images](http://e2e.ti.com/support/dsp/omap_applications_processors/f/447/t/138400.aspx), PowerVR fixes
| *scripts/qemumkimg.sh* | Generate image files for use with Qemu

Yocto Project
-------------
To build the *ecam-console-image*, you can either setup Yocto like outlined
below or you can just add *meta-ecam* as a layer and make a few changes to
*conf/local.conf*. If you don't have Yocto setup yet, first install Ubuntu in
Virtualbox if you don't have it already. Ubuntu 12.04, 13.04, and 14.04 and
either 32-bit or 64-bit have worked in the past. However, the current version
of this has only been tested on Ubuntu 14.04 64-bit.

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
    ~/repo init -u git://github.com/floft/meta-ecam -b dylan
    ~/repo sync

Initialize build directory. You'll have to source this every time you want to
start working with bitbake.

    TEMPLATECONF=meta-ecam/conf source poky/oe-init-build-env

Modify *conf/local.conf* to suit your system (e.g. modify max jobs).

TI DSP Drivers
--------------
Downloaded C6000 Code Generation Tools v7.2.7 from [Texas
Instruments](https://www-a.ti.com/downloads/sds_support/TICodegenerationTools/download.htm)
(requires a free account) and copy into the download folder. Then create the
.done file in the same directory.

    mkdir -p ~/yocto/build/downloads/
    mv /.../ti_cgt_c6000_7.2.7_setup_linux_x86.bin ~/yocto/build/downloads/
    touch ~/yocto/build/downloads/ti_cgt_c6000_7.2.7_setup_linux_x86.bin.done

Build everything
----------------
Build a console image including the TI DSP drivers that is compatible with the
e-CAM56 37x GSTIX driver.

    bitbake ecam-console-image

Note that you'll probably get lots of warnings. If you get errors, you can
[open an issue](https://github.com/floft/meta-ecam/issues). I may have time to
look into it.

Deploy
------
In *~/yocto/build/tmp/deploy/images/* you should see some files like *MLO*,
*u-boot.img*, *uImage*, *ecam-console-image-...tar.bz2*, and *modules-...tgz*
(most of which will be symlinks). Create a fat32 boot partion on an SD card and
copy *MLO*, *u-boot.img*, and *uImage* onto it. Extract the other two into a
larger ext3 second partition. You can read the [Gumstix
documentation](http://gumstix.org/create-a-bootable-microsd-card.html) for more
in-depth information. You may want to reset the bootloader environment (bottom
of linked page) to the defaults in the newly compiled one. Or, if the
bootloader doesn't even load, you can try [a precompiled Gumstix
one](http://cumulus.gumstix.org/images/angstrom/factory/2011-08-30-1058/u-boot.bin).

U-Boot Environment
------------------
Kernel arguments for use without the camera driver:

    setenv optargs 'mem=55M@0x80000000 mem=384M@0x88000000'

For use with the camera driver:

    setenv optargs 'mem=55M@0x80000000 mem=96M@0x88000000 mem=256M@0x90000000'

And, something like this including the ${optargs} if not already there

    setenv mmcargs 'setenv bootargs console=${console} ${optargs} mpurate=${mpurate} vram=${vram} omapfb.mode=dvi:${dvimode} omapdss.def_disp=${defaultdisplay} root=${mmcroot} rootfstype=${mmcrootfstype}'

#### Memory Layout
It works to leave the drivers in their default locations. Below is the memory
layout used above for a 512 MiB Gumstix. If you want to change the cmem memory
locations, modify */usr/share/ti/gst/omap3530/loadmodules.sh*.

    linux    55M@0x80000000 - 0x83700000 at 0M
    cmem     60M@0x83700000 - 0x87300000 at 55M
    dsplink? 13M@0x87300000 - 0x88000000 at 115M
    linux    96M@0x88000000 - 0x8e000000 at 128M
    camera   32M@0x8e000000 - 0x8fffffff at 224M
    linux   256M@0x90000000 - 0xa0000000 at 256M

**TODO:** it would be a good idea to figure out where dsplink actually is from
the code instead of just figuring out what doesn't cause immediate issues.

Run Test
--------
Test the TI drivers

    gst-launch -v videotestsrc num-buffers=100 ! 'video/x-raw-yuv,width=640,height=480,format=(fourcc)UYVY' ! TIVidenc1 codecName=h264enc engineName=codecServer ! avimux ! filesink location=video.avi

Test the camera driver

    gst-launch -v v4l2src num-buffers=10 ! 'video/x-raw-yuv,width=640,height=480,framerate=5/1,format=(fourcc)UYVY' ! ffmpegcolorspace ! avimux ! filesink location=video.avi

Test both the camera driver and the TI drivers together. Increase *num-buffers*
to capture more frames or remove to continue capturing until pressing Ctrl+C
(use *gst-inspect* for the list of properties, e.g. ``gst-inspect v4l2src``).

    gst-launch -v v4l2src num-buffers=100 ! 'video/x-raw-yuv,width=640,height=480,framerate=25/1,format=(fourcc)UYVY' ! TIVidenc1 codecName=h264enc engineName=codecServer resolution=640x480 framerate=25 ! avimux ! filesink location=video.avi

Testing full-resolution image capture with the TI driver

    gst-launch -v v4l2src num-buffers=120 ! video/x-raw-yuv,width=2592,height=1944 ! TIImgenc1 engineName=codecServer codecName=jpegenc iColorSpace=UYVY oColorSpace=YUV420P qValue=75 numOutputBufs=2 resolution=2592x1944 ! multifilesink location=image-%05d.jpg

### Troubleshooting
It may not work. Here's some things that might have gone wrong.

#### Backtrace from *ioremap.c*
The below backtrace is caused by trying to [remap normal memory to device
memory](http://www.serverphorums.com/read.php?12,396674,396744#msg-396744). You
probably need to adjust your *mem=* kernel parameters near *dsplinkk* or
*v4l2_driver* depending on which you see in the trace.

    [   37.909973] ------------[ cut here ]------------
    [   37.915008] WARNING: at arch/arm/mm/ioremap.c:207 __arm_ioremap_pfn_caller+0xd4/0xf8()
    [   37.924682] Modules linked in: sdmak(O) lpm_omap3530(O) dsplinkk(O) v4l2_driver(O) cmemk(O) libertas_sdio libertas lib80211 cfg80211
    ...
    [   38.022552] [<bf0c8b68>] (OMAP3530_init+0xa8/0x1f4 [dsplinkk]) from [<bf0c80b4>] (DSP_init+0x24/0x2c [dsplinkk])
    ...
    [   38.093627] [<c01054e0>] (sys_ioctl+0x6c/0x7c) from [<c0013440>] (ret_fast_syscall+0x0/0x3c)
    [   38.103790] ---[ end trace 805171ff509f69cd ]---

#### Empty Video file
If in ``dmesg | tail`` you get "Device or resource busy," the camera probably
isn't quite connected. Verify it's connected, and then reload *v4l2_driver* or
run the following. You may get printk output that makes it look like this
command is still running. It's not. It's a more-or-less instantaneous command.
Press return.

    systemctl restart ecam-driver

#### Weird Colors
If you start getting weird coloring, you probably ran something like this.

    gst-launch -v v4l2src num-buffers=10 ! video/x-raw-yuv,width=640,height=480,framerate=5/1 ! avimux ! filesink location=video.avi

This works fine, but if you then use *TIVidenc1* after this, you might get this
weird coloring. As in the example in the above section, you can specify
*format=(fourcc)UYVY* and then run it through *ffmpegcolorspace* before
*avimux* and then you shouldn't run into this problem. The "fix" for this is to
``reboot``.

#### Got unexpected frame size
You may see errors like the following after trying to switch from
full-resolution images back to low-resolution video.

    WARNING: from element /GstPipeline:pipeline0/GstV4l2Src:v4l2src0: Got unexpected frame size of 10077696 instead of 614400.
    Additional debug info:
    gstv4l2src.c(919): gst_v4l2src_get_mmap (): /GstPipeline:pipeline0/GstV4l2Src:v4l2src0
    WARNING: from element /GstPipeline:pipeline0/GstV4l2Src:v4l2src0: Got unexpected frame size of 10077696 instead of 614400.
    Additional debug info:
    ...

A workaround for this is just reloading the camera driver or rebooting.

    systemctl restart ecam-driver

#### Tee with two sinks hangs
It appears that *tee* with multiple sinks hangs when capturing individual frames.
For example, the following does not hang:

    gst-launch -v v4l2src num-buffers=1 ! video/x-raw-yuv,width=640,height=480 ! jpegenc ! tee name=t ! fakesink

However, the following will hang:

    gst-launch -v v4l2src num-buffers=1 ! video/x-raw-yuv,width=640,height=480 ! jpegenc ! tee name=t ! fakesink t. ! fakesink

And, likewise anything going from *TIImgenc1* to *tee* with *multifilesink* and
*appsink* will hang. However, using *TIVidenc1* to *tee* with *filesink* and
*appsink* does not hang. This [may be an
issue](http://e2e.ti.com/support/dsp/omap_applications_processors/f/447/p/138400/776655.aspx#776655)
with the TI drivers.

Resources
---------
[Using the DSP on Gumstix with Yocto](http://www.sleepyrobot.com/?p=210)  
[Gumstix Repo Manifests for the Yocto Project](https://github.com/gumstix/Gumstix-YoctoProject-Repo)
