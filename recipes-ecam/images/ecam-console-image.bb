SUMMARY = "An image with e-CAM56 37x GSTIX and TI DSP drivers"
HOMEPAGE = "http://e-consystems.com/"
LICENSE = "MIT"

require pansenti-console-image.bb

PR = "1"

IMAGE_INSTALL += " \
 dvsdk
 "

export IMAGE_BASENAME = "ecam-console-image"

