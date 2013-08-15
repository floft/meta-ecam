# A minimal sysvinit boot image

LICENSE = "MIT"

inherit core-image

IMAGE_FEATURES += "package-management"

IMAGE_INSTALL += " \
    packagegroup-core-boot \
    ${ROOTFS_PKGMANAGE_BOOTSTRAP} \
 "

disable_bootlogd() {
    echo BOOTLOGD_ENABLE=no > ${IMAGE_ROOTFS}/etc/default/bootlogd
}

ROOTFS_POSTPROCESS_COMMAND += "disable_bootlogd ; "

export IMAGE_BASENAME = "pansenti-boot-image"

