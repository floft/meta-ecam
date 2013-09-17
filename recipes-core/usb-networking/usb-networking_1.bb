DESCRIPTION = "Autostart USB Networking on OTG port"
HOMEPAGE = ""
SECTION = "networking"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d41d8cd98f00b204e9800998ecf8427e"
PR = "r1"

SRC_URI = "file://usb-networking.service \
           file://usb-networking.sh"

S = "${WORKDIR}"

do_install() {
    install -d "${D}/usr/share"
    install -d "${D}/lib/systemd/system"
    install -m 0755 "${WORKDIR}/usb-networking.sh" "${D}/usr/share/usb-networking.sh"
    install -m 0644 "${WORKDIR}/usb-networking.service" "${D}/lib/systemd/system/usb-networking.service"
}

FILES_${PN} += "/lib/systemd/system/* /usr/share/*"
