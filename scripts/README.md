meta-ecam scripts
=================
Here are some scripts that might potentially be useful.

Qemu
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
    sudo ~/yocto/poky/meta-ecam/scripts/qemumkimg.sh sd.img MLO u-boot.img uImage ecam-console-image-*.tar.bz2 $(ls modules*.tgz | tail -n 1)

Set yourself as the owner since you don't want to run qemu as root.

    sudo chown $(id -nu):$(id -nu) sd.img

Emulate the system, note that the keyboard and mouse probably won't work.

    /usr/local/bin/qemu-system-arm -M overo -m 512 -sd sd.img -clock unix -serial stdio -device usb-mouse -device usb-kbd

Resources
---------
[Gumstix Emulation for QEMU](http://wiki.gumstix.org/index.php?title=Gumstix_Emulation_for_QEMU)  
