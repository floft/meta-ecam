DESCRIPTION = "Camera driver for e-CAM56 37x GSTIX"
HOMEPAGE = "http://www.e-consystems.com/5MP-Gumstix-Camera.asp"
SECTION = "kernel/modules"
LICENSE = "GPLv2"
SRC_URI = "http://roadnarrows.com/distro/e-con/e-CAM56_37x_GSTIX/Software/e-CAM56_37x_GSTIX_LINUX_REL_2.0.zip"
PR = "r1"

LIC_FILES_CHKSUM = "file://inc_header.h;beginline=1;endline=7;md5=b816030b90f9adc8249d6d5d15f9aaef"
SRC_URI[md5sum] = "4a4db65442b3473a1a360212eedb3a8d"
SRC_URI[sha256sum] = "18b77bfda359ef8796226fad42cdc6e62dc27f9fef1533c130a99ef2408c8a89"

inherit module

S = "${WORKDIR}/e-CAM56_37x_GSTIX_LINUX_REL_2.0/Driver/Source"
MAKE_TARGETS = "omap"

RPROVIDES_${PN} = "ecam-driver"
