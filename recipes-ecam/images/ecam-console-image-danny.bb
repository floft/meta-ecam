# Based on: https://github.com/gumstix/meta-gumstix-extras/blob/danny/recipes-images/gumstix/gumstix-console-image.bb
DESCRIPTION = "An image with e-CAM56 37x GSTIX and TI DSP drivers"
LICENSE = "MIT"
PR = "r0"

inherit image
IMAGE_FEATURES += "package-management"
IMAGE_EXTRA_INSTALL ?= ""

DEPENDS = "virtual/kernel"

AUDIO_INSTALL = " \
  alsa-utils-aplay \
  alsa-utils-alsactl \
  alsa-utils-alsamixer \
  alsa-utils-amixer \
  alsa-utils-speakertest \
 "


BASE_INSTALL = " \
  ${MACHINE_EXTRA_RRECOMMENDS} \
  ${@base_contains("DISTRO_FEATURES", "bluetooth", "bluez4", "", d)} \
  avahi-systemd avahi-utils \
  base-files \
  base-passwd \
  bash \
  coreutils \
  dbus \
  devmem2 \
  man \	
  man-pages \
  memtester \
  netbase \
  ntp-systemd \
  net-tools \
  polkit \
  rsyslog-systemd \
  sed \
  shadow tinylogin \
  systemd systemd-compat-units \
  u-boot-mkimage \
  udisks udisks-systemd \
  upower \
  update-alternatives-cworth \
  util-linux \
  which \
  zypper \
 "

FIRMWARE_INSTALL = " \
  linux-firmware-sd8686 \
  linux-firmware-rtl8192cu \
  linux-firmware-rtl8192ce \
  linux-firmware-rtl8192su \
  linux-firmware-wl12xx \
 "
NETWORK_INSTALL = " \
  networkmanager \
  networkmanager-tests \
  rfkill \
  wireless-tools \
  ${@base_contains("DISTRO_FEATURES", "wifi", "iw wpa-supplicant", "", d)} \
 "

TOOLS_INSTALL = " \
  bzip2 \
  cpufrequtils \
  dosfstools \
  e2fsprogs \
  evtest \
  findutils \
  iputils \
  grep \
  gzip \
  htop \
  nano \
  openssh-ssh openssh-keygen openssh-scp openssh-sshd-systemd \
  sudo \
  systemd-analyze \
  tar \
  vim \
  wget \
  zip \
 "

ECAM_INSTALL = " \
gstreamer-ti \
gst-ffmpeg \
gst-plugins-good \
gst-meta-video \
gst-meta-audio \
gst-meta-debug \
opencv \
opencv-dev \
opencv-apps \
"

IMAGE_INSTALL += " \
  ${BASE_INSTALL} \
  ${AUDIO_INSTALL} \
  ${FIRMWARE_INSTALL} \
  ${NETWORK_INSTALL} \
  ${ROOTFS_PKGMANAGE} \
  ${TOOLS_INSTALL} \
  ${ECAM_INSTALL} \
 "

# this section removes remnants of legacy sysvinit support
# for packages installed above
IMAGE_FILE_BLACKLIST += " \
                        /etc/init.d/NetworkManager \
                        /etc/init.d/avahi-daemon \
                        /etc/init.d/dbus-1 \
                        /etc/init.d/dnsmasq \
                        /etc/init.d/networking \
                        /etc/init.d/ntpd \
                        /etc/init.d/sshd \
                        /etc/init.d/udev \
                        /etc/init.d/udev-cache \
                       "

remove_blacklist_files() {
	for i in ${IMAGE_FILE_BLACKLIST}; do
		rm -rf ${IMAGE_ROOTFS}$i
	done

}

set_gumstix_user() {
    echo "gumstix:x:500:" >> "${IMAGE_ROOTFS}/etc/group"
    echo "gumstix:VQ43An5F8LYqc:500:500:Gumstix User,,,:/home/gumstix:/bin/bash"  >> "${IMAGE_ROOTFS}/etc/passwd"

    install -d "${IMAGE_ROOTFS}/home/gumstix"
    cp -f "${IMAGE_ROOTFS}/etc/skel/.bashrc" "${IMAGE_ROOTFS}/etc/skel/.profile" "${IMAGE_ROOTFS}/home/gumstix"
    chown gumstix:gumstix -R "${IMAGE_ROOTFS}/home/gumstix"

    echo "%gumstix ALL=(ALL) ALL" >> "${IMAGE_ROOTFS}/etc/sudoers"
    chmod 0440 "${IMAGE_ROOTFS}/etc/sudoers"
    chmod u+s "${IMAGE_ROOTFS}/usr/bin/sudo"
}

ROOTFS_POSTPROCESS_COMMAND =+ "remove_blacklist_files ; set_gumstix_user ; "
