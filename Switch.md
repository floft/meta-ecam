Switch to meta-ecam
===================
Here are instructions to hopefully take you from an image with
*gumstix-console-image* and *gstreamer-ti* building correctly
to one with a working 3.2 kernel for use with the camera.

Go into your *~/yocto* directory and switch repo manifests

    repo init -u git://github.com/floft/manifest -m yocto-ecam.xml
    repo sync

Prepare to build

    source poky/oe-init-build-env

Add *meta-ecam* (and verify *meta-ti* is there) to *conf/bblayers.conf*

Chose to build the 3.2 kernel in *conf/local.conf* and add any additional lines
not present

    PREFERRED_VERSION_linux-gumstix = "3.2"

    PREFERRED_PROVIDER_u-boot = "u-boot"
    DISTRO_FEATURES_append = " systemd"
    VIRTUAL-RUNTIME_init_manager = "systemd"
    VIRTUAL-RUNTIME_graphical_init_manager = "gdm"
    DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"

    BBMASK ?= ".*/meta-ti/recipes-(misc|bsp/formfactor)/"

    TOOLCHAIN_PATH ?= "${STAGING_DIR_NATIVE}${prefix_native}/bin/${TUNE_PKGARCH}${HOST_VENDOR}-${HOST_OS}"
    TOOLCHAIN_SYSPATH ?= "${TOOLCHAIN_PATH}/${TARGET_SYS}"

    INSANE_SKIP_gstreamer-ti-dev = "staticdev"

Build the custom image

    bitbake ecam-console-image
