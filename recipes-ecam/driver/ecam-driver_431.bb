DESCRIPTION = "Camera driver for e-CAM56 37x GSTIX"
HOMEPAGE = "http://www.e-consystems.com/5MP-Gumstix-Camera.asp"
SECTION = "kernel/modules"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://inc_header.h;beginline=1;endline=7;md5=b816030b90f9adc8249d6d5d15f9aaef"
PR = "r1"

SRC_URI = "http://roadnarrows.com/distro/e-con/e-CAM56_37x_GSTIX/Software/e-CAM56_37x_GSTIX_LINUX_REL_2.0.zip;name=source \
    file://0001-Add-modules_install-target.patch;name=patch"
SRC_URI[source.md5sum] = "4a4db65442b3473a1a360212eedb3a8d"
SRC_URI[source.sha256sum] = "18b77bfda359ef8796226fad42cdc6e62dc27f9fef1533c130a99ef2408c8a89"
SRC_URI[patch.md5sum] = "4fd07843ad49c2e757a56f124adb48c6"
SRC_URI[patch.sha256sum] = "14adaab961bcc06cca7689f71367ea78647155574411870e307e7ed9f8506e40"

S = "${WORKDIR}/e-CAM56_37x_GSTIX_LINUX_REL_2.0/Driver/Source"

inherit module

do_install_append() {
    install -d "${D}/etc/modules-load.d"
    echo "v4l2_driver" > "${D}/etc/modules-load.d/v4l2_driver"
}
