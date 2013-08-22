#!/bin/sh
# For when restarting this service
rmmod v4l2_driver 2>/dev/null

# For some reason we have to do it twice on boot
modprobe v4l2_driver
modprobe v4l2_driver
