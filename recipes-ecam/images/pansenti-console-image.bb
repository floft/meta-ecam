# A console development image with some C/C++ dev tools

require pansenti-boot-image.bb

CORE_OS = " \
    avahi \
    busybox-hwclock \
    ntp \
    ntp-tickadj \
    task-core-ssh-openssh openssh-keygen \
    term-prompt \
    tzdata \
 "

# Custom kernel modules built out of tree
KERNEL_MODULES_OOT = ""

KERNEL_MODULES_OOT_overo = " \
    omap3-pwm \
    omap3-mux \
    omap3-irqlat \
    hrt-test \
    udelay-test \
 "

KERNEL_EXTRA_INSTALL = " \
    kernel-modules \
    ${KERNEL_MODULES_OOT} \
 "

WIFI_SUPPORT = " \
    linux-firmware-rtl8192ce \
    linux-firmware-rtl8192cu \
    linux-firmware-rtl8192su \
    linux-firmware-wl12xx \
    wpa-supplicant \
 "

WIFI_SUPPORT_duovero = " \
    linux-firmware-sd8787 \
    linux-firmware-rtl8192ce \
    linux-firmware-rtl8192cu \
    linux-firmware-rtl8192su \
    linux-firmware-wl12xx \
    wpa-supplicant \
 "

WIFI_SUPPORT_overo = " \
    linux-firmware-sd8686 \
    linux-firmware-rtl8192ce \
    linux-firmware-rtl8192cu \
    linux-firmware-wl12xx \
    wpa-supplicant \
 "

DEV_SDK_INSTALL = " \
    binutils \
    binutils-symlinks \
    coreutils \
    cpp \
    cpp-symlinks \
    diffutils \
    file \
    gcc \
    gcc-symlinks \
    g++ \
    g++-symlinks \
    gettext \
    git \
    ldd \
    libstdc++ \
    libstdc++-dev \
    libtool \
    make \
    pkgconfig \
 "

EXTRA_TOOLS_INSTALL = " \
    bzip2 \
    ethtool \
    findutils \
    i2c-tools \
    iperf \
    htop \
    less \
    nano \
    sysfsutils \
    tcpdump \
    unzip \
    wget \
    zip \
 "

ALSA_TOOLS = " \
    alsa-dev \
    alsa-lib \
    alsa-lib-dev \
 "

MISC_EXTRA = ""

MISC_EXTRA_overo = " \
    polladc \
 "

MISC_EXTRA_beaglebone = " \
    board-setup \
 "

IMAGE_INSTALL += " \
    ${CORE_OS} \
    ${DEV_SDK_INSTALL} \
    ${EXTRA_TOOLS_INSTALL} \
    ${KERNEL_EXTRA_INSTALL} \
    ${ALSA_TOOLS} \
    ${MISC_EXTRA} \
    ${WIFI_SUPPORT} \
 "

set_local_timezone() {
    ln -sf /usr/share/zoneinfo/EST5EDT ${IMAGE_ROOTFS}/etc/localtime
}

#avahi_no_drop_root() {
#    sed -i -e 's/\$DAEMON -D/\$DAEMON -D --no-drop-root/' ${IMAGE_ROOTFS}/etc/init.d/avahi-daemon
#}

ROOTFS_POSTPROCESS_COMMAND += "set_local_timezone ; "

#ROOTFS_POSTPROCESS_COMMAND_wandboard-dual += "
#    set_local_timezone ;
#    avahi_no_drop_root ;
# "


export IMAGE_BASENAME = "pansenti-console-image"


